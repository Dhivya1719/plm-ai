package com.plm.plm_ai.ai.assistant;

import com.plm.plm_ai.item.BOMLine;
import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.repository.BOMLineRepository;
import com.plm.plm_ai.item.repository.ItemRepository;
import com.plm.plm_ai.item.repository.ItemRevisionRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PLMQueryService {

    private final ItemRepository itemRepository;

    private final ItemRevisionRepository revisionRepository;

    private final BOMLineRepository bomLineRepository;

    public PLMQueryService(
            ItemRepository itemRepository,
            ItemRevisionRepository revisionRepository,
            BOMLineRepository bomLineRepository) {

        this.itemRepository =
                itemRepository;

        this.revisionRepository =
                revisionRepository;

        this.bomLineRepository =
                bomLineRepository;
    }

    public String findItemInformation(
            String itemNumber) {

        return itemRepository
                .findByItemNumber(itemNumber)
                .map(item -> {

                    StringBuilder result =
                            new StringBuilder();

                    result.append(
                            "ITEM INFORMATION\n"
                    );

                    result.append(
                            "Item Number: "
                                    + item.getItemNumber()
                                    + "\n"
                    );

                    result.append(
                            "Name: "
                                    + item.getName()
                                    + "\n"
                    );

                    result.append(
                            "Type: "
                                    + item.getItemType()
                                    + "\n"
                    );

                    List<ItemRevision> revisions =
                            revisionRepository
                                    .findByItemId(
                                            item.getId()
                                    );

                    result.append(
                            "Revisions:\n"
                    );

                    for (ItemRevision revision
                            : revisions) {

                        result.append(
                                "- Revision "
                                        + revision
                                        .getRevisionCode()
                                        + " (ID "
                                        + revision.getId()
                                        + ", Status "
                                        + revision
                                        .getStatus()
                                        + ")\n"
                        );
                    }

                    return result.toString();

                })
                .orElse(
                        "No item found for item number: "
                                + itemNumber
                );
    }

    public String findRevisionInformation(
            Long revisionId) {

        ItemRevision revision =
                revisionRepository
                        .findById(revisionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Revision not found: "
                                                + revisionId
                                ));

        StringBuilder result =
                new StringBuilder();

        result.append(
                "REVISION INFORMATION\n"
        );

        result.append(
                "Revision ID: "
                        + revision.getId()
                        + "\n"
        );

        result.append(
                "Revision Code: "
                        + revision.getRevisionCode()
                        + "\n"
        );

        result.append(
                "Description: "
                        + revision.getDescription()
                        + "\n"
        );

        result.append(
                "Lifecycle Status: "
                        + revision.getStatus()
                        + "\n"
        );

        result.append(
                "BOM:\n"
        );

        List<BOMLine> bomLines =
                bomLineRepository
                        .findByParentRevisionId(
                                revisionId
                        );

        for (BOMLine bomLine : bomLines) {

            ItemRevision child =
                    bomLine.getChildRevision();

            result.append(
                    "- "
                            + child.getItem()
                            .getItemNumber()
                            + " | "
                            + child.getItem()
                            .getName()
                            + " | Revision "
                            + child.getRevisionCode()
                            + " | Quantity "
                            + bomLine.getQuantity()
                            + "\n"
            );
        }

        return result.toString();
    }
}