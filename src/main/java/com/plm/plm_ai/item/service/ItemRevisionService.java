package com.plm.plm_ai.item.service;

import com.plm.plm_ai.change.service.ChangeHistoryService;
import com.plm.plm_ai.item.BOMLine;
import com.plm.plm_ai.item.Item;
import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.LifecycleStatus;
import com.plm.plm_ai.item.repository.BOMLineRepository;
import com.plm.plm_ai.item.repository.ItemRepository;
import com.plm.plm_ai.item.repository.ItemRevisionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemRevisionService {

    private final ItemRevisionRepository revisionRepository;
    private final ItemRepository itemRepository;
    private final BOMLineRepository bomLineRepository;
    private final ChangeHistoryService changeHistoryService;

    public ItemRevisionService(
            ItemRevisionRepository revisionRepository,
            ItemRepository itemRepository,
            BOMLineRepository bomLineRepository,
            ChangeHistoryService changeHistoryService) {

        this.revisionRepository = revisionRepository;
        this.itemRepository = itemRepository;
        this.bomLineRepository = bomLineRepository;
        this.changeHistoryService = changeHistoryService;
    }

    // =========================================================
    // CREATE REVISION
    // =========================================================

    public ItemRevision createRevision(
            Long itemId,
            ItemRevision revision) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item not found with id: " + itemId
                        )
                );

        if (revisionRepository.existsByItemAndRevisionCode(
                item,
                revision.getRevisionCode())) {

            throw new RuntimeException(
                    "Revision already exists: "
                            + revision.getRevisionCode()
            );
        }

        revision.setItem(item);

        return revisionRepository.save(revision);
    }


    // =========================================================
    // GET ALL REVISIONS FOR AN ITEM
    // =========================================================

    public List<ItemRevision> getRevisionsByItem(Long itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item not found with id: " + itemId
                        )
                );

        return revisionRepository.findByItem(item);
    }


    // =========================================================
    // GET ONE REVISION
    // =========================================================

    public ItemRevision getRevisionById(Long revisionId) {

        return revisionRepository.findById(revisionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Revision not found with id: "
                                        + revisionId
                        )
                );
    }


    // =========================================================
    // UPDATE REVISION
    // =========================================================

    public ItemRevision updateRevision(
            Long revisionId,
            ItemRevision updatedRevision) {

        ItemRevision existingRevision =
                getRevisionById(revisionId);

        /*
         * RELEASED revisions must never be modified.
         */
        if (existingRevision.getStatus()
                == LifecycleStatus.RELEASED) {

            throw new RuntimeException(
                    "Released revision cannot be modified. "
                            + "Create a new revision instead."
            );
        }

        existingRevision.setDescription(
                updatedRevision.getDescription()
        );

        existingRevision.setStatus(
                updatedRevision.getStatus()
        );

        return revisionRepository.save(existingRevision);
    }


    // =========================================================
    // SAVE REVISION
    // =========================================================

    public ItemRevision saveRevision(ItemRevision revision) {
        return revisionRepository.save(revision);
    }


    // =========================================================
    // CREATE NEW REVISION FROM RELEASED REVISION
    // =========================================================

    @Transactional
    public ItemRevision createRevisionFromExisting(
            Long sourceRevisionId) {

        // -----------------------------------------------------
        // 1. Get source revision
        // -----------------------------------------------------

        ItemRevision sourceRevision =
                getRevisionById(sourceRevisionId);


        // -----------------------------------------------------
        // 2. Source revision MUST be RELEASED
        // -----------------------------------------------------

        if (sourceRevision.getStatus()
                != LifecycleStatus.RELEASED) {

            throw new RuntimeException(
                    "New revision can only be created from "
                            + "a RELEASED revision."
            );
        }


        // -----------------------------------------------------
        // 3. Get parent Item
        // -----------------------------------------------------

        Item item = sourceRevision.getItem();


        // -----------------------------------------------------
        // 4. Determine next revision code
        // -----------------------------------------------------

        String nextRevisionCode =
                getNextRevisionCode(
                        sourceRevision.getRevisionCode()
                );


        // -----------------------------------------------------
        // 5. Make sure revision doesn't already exist
        // -----------------------------------------------------

        if (revisionRepository.existsByItemAndRevisionCode(
                item,
                nextRevisionCode)) {

            throw new RuntimeException(
                    "Revision already exists: "
                            + nextRevisionCode
            );
        }


        // -----------------------------------------------------
        // 6. Create new revision
        // -----------------------------------------------------

        ItemRevision newRevision =
                new ItemRevision();

        newRevision.setItem(item);

        newRevision.setRevisionCode(
                nextRevisionCode
        );

        newRevision.setDescription(
                sourceRevision.getDescription()
        );

        /*
         * New revision always starts IN_WORK.
         */
        newRevision.setStatus(
                LifecycleStatus.IN_WORK
        );


        // -----------------------------------------------------
        // 7. Save new revision
        // -----------------------------------------------------

        ItemRevision savedRevision =
                revisionRepository.save(newRevision);


        // -----------------------------------------------------
        // 8. AUDIT REVISION CREATION
        // -----------------------------------------------------

        changeHistoryService.record(
                "REVISION",
                savedRevision.getId(),
                "CREATED",
                "Created revision "
                        + savedRevision.getRevisionCode()
                        + " from released revision "
                        + sourceRevision.getRevisionCode(),
                "SYSTEM",
                null,
                null
        );


        // -----------------------------------------------------
        // 9. Copy source BOM
        // -----------------------------------------------------

        List<BOMLine> sourceBom =
                bomLineRepository
                        .findByParentRevisionId(
                                sourceRevisionId
                        );


        for (BOMLine sourceLine : sourceBom) {

            BOMLine newLine =
                    new BOMLine();

            newLine.setParentRevision(
                    savedRevision
            );

            newLine.setChildRevision(
                    sourceLine.getChildRevision()
            );

            newLine.setQuantity(
                    sourceLine.getQuantity()
            );

            bomLineRepository.save(newLine);
        }


        // -----------------------------------------------------
        // 10. Return newly created revision
        // -----------------------------------------------------

        return savedRevision;
    }


    // =========================================================
    // GENERATE NEXT REVISION CODE
    // =========================================================

    private String getNextRevisionCode(
            String currentRevisionCode) {

        if (currentRevisionCode == null
                || currentRevisionCode.isBlank()) {

            throw new RuntimeException(
                    "Invalid revision code."
            );
        }

        /*
         * Current design:
         *
         * A → B
         * B → C
         * C → D
         *
         * ...
         */

        if (currentRevisionCode.length() != 1
                || !Character.isLetter(
                currentRevisionCode.charAt(0))) {

            throw new RuntimeException(
                    "Revision code must currently "
                            + "be a single letter."
            );
        }

        char current =
                Character.toUpperCase(
                        currentRevisionCode.charAt(0)
                );

        if (current == 'Z') {

            throw new RuntimeException(
                    "Revision sequence exceeded Z."
            );
        }

        return String.valueOf(
                (char) (current + 1)
        );
    }
}