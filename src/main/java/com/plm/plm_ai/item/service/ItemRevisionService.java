package com.plm.plm_ai.item.service;

import com.plm.plm_ai.item.Item;
import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.repository.ItemRepository;
import com.plm.plm_ai.item.repository.ItemRevisionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemRevisionService {

    private final ItemRevisionRepository revisionRepository;
    private final ItemRepository itemRepository;

    public ItemRevisionService(
            ItemRevisionRepository revisionRepository,
            ItemRepository itemRepository) {

        this.revisionRepository = revisionRepository;
        this.itemRepository = itemRepository;
    }

    // CREATE REVISION
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

    // GET ALL REVISIONS FOR AN ITEM
    public List<ItemRevision> getRevisionsByItem(Long itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item not found with id: " + itemId
                        )
                );

        return revisionRepository.findByItem(item);
    }

    // GET ONE REVISION
    public ItemRevision getRevisionById(Long revisionId) {

        return revisionRepository.findById(revisionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Revision not found with id: " + revisionId
                        )
                );
    }

    // UPDATE REVISION
    public ItemRevision updateRevision(
            Long revisionId,
            ItemRevision updatedRevision) {

        ItemRevision existingRevision =
                getRevisionById(revisionId);

        existingRevision.setDescription(
                updatedRevision.getDescription()
        );

        existingRevision.setStatus(
                updatedRevision.getStatus()
        );

        return revisionRepository.save(existingRevision);
    }
    public ItemRevision saveRevision(ItemRevision revision) {
        return revisionRepository.save(revision);
    }
}