package com.plm.plm_ai.change.service;

import com.plm.plm_ai.item.BOMLine;
import com.plm.plm_ai.item.repository.BOMLineRepository;
import com.plm.plm_ai.item.repository.ItemRevisionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhereUsedService {

    private final BOMLineRepository bomLineRepository;
    private final ItemRevisionRepository itemRevisionRepository;

    public WhereUsedService(
            BOMLineRepository bomLineRepository,
            ItemRevisionRepository itemRevisionRepository) {

        this.bomLineRepository = bomLineRepository;
        this.itemRevisionRepository = itemRevisionRepository;
    }

    // FIND ALL PARENT REVISIONS THAT USE THIS REVISION
    public List<BOMLine> findWhereUsed(Long revisionId) {

        // Check that the revision exists
        itemRevisionRepository.findById(revisionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item Revision not found"
                        ));

        // Find all BOM lines where this revision is the child
        return bomLineRepository.findByChildRevisionId(revisionId);
    }
}