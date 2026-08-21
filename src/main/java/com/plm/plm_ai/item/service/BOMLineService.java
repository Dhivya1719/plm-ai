package com.plm.plm_ai.item.service;

import com.plm.plm_ai.item.BOMLine;
import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.exception.BOMCircularDependencyException;
import com.plm.plm_ai.item.exception.BOMDuplicateException;
import com.plm.plm_ai.item.exception.BOMNotFoundException;
import com.plm.plm_ai.item.exception.BOMSelfReferenceException;
import com.plm.plm_ai.item.repository.BOMLineRepository;
import com.plm.plm_ai.item.repository.ItemRevisionRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BOMLineService {

    private final BOMLineRepository bomLineRepository;
    private final ItemRevisionRepository itemRevisionRepository;

    public BOMLineService(
            BOMLineRepository bomLineRepository,
            ItemRevisionRepository itemRevisionRepository) {

        this.bomLineRepository = bomLineRepository;
        this.itemRevisionRepository = itemRevisionRepository;
    }

    // ============================================================
    // ADD COMPONENT
    // ============================================================

    public BOMLine addComponent(
            Long parentRevisionId,
            Long childRevisionId,
            Integer quantity) {

        // Rule 1: Quantity must be greater than zero
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        // Rule 2: Parent and child cannot be the same
        if (parentRevisionId.equals(childRevisionId)) {
            throw new BOMSelfReferenceException(
                    "A revision cannot be its own component"
            );
        }

        // Rule 3: Parent revision must exist
        ItemRevision parentRevision =
                itemRevisionRepository.findById(parentRevisionId)
                        .orElseThrow(() ->
                                new BOMNotFoundException(
                                        "Parent revision not found"
                                )
                        );

        // Rule 4: Child revision must exist
        ItemRevision childRevision =
                itemRevisionRepository.findById(childRevisionId)
                        .orElseThrow(() ->
                                new BOMNotFoundException(
                                        "Child revision not found"
                                )
                        );

        // Rule 5: Prevent circular BOM dependency
        //
        // Example:
        // A -> B
        // B -> C
        // C -> A  ❌
        //
        // Before adding C -> A, we check whether
        // A can already reach C.
        if (hasPathToTarget(
                childRevisionId,
                parentRevisionId,
                new HashSet<>())) {

            throw new BOMCircularDependencyException(
                    "Circular BOM dependency detected"
            );
        }

        // Rule 6: Prevent duplicate component
        if (bomLineRepository
                .existsByParentRevisionIdAndChildRevisionId(
                        parentRevisionId,
                        childRevisionId)) {

            throw new BOMDuplicateException(
                    "This component already exists in the BOM"
            );
        }

        // Create BOM line
        BOMLine bomLine = new BOMLine();

        bomLine.setParentRevision(parentRevision);
        bomLine.setChildRevision(childRevision);
        bomLine.setQuantity(quantity);

        return bomLineRepository.save(bomLine);
    }

    // ============================================================
    // CIRCULAR BOM CHECK
    // ============================================================

    private boolean hasPathToTarget(
            Long currentRevisionId,
            Long targetRevisionId,
            Set<Long> visited) {

        // Target reached.
        // Therefore adding the new relationship would create a cycle.
        if (currentRevisionId.equals(targetRevisionId)) {
            return true;
        }

        // Prevent infinite recursion if a graph already contains
        // a repeated revision.
        if (!visited.add(currentRevisionId)) {
            return false;
        }

        // Get all components belonging to the current revision
        List<BOMLine> components =
                bomLineRepository.findByParentRevisionId(
                        currentRevisionId
                );

        // Traverse through every child revision
        for (BOMLine component : components) {

            Long childId =
                    component.getChildRevision().getId();

            if (hasPathToTarget(
                    childId,
                    targetRevisionId,
                    visited)) {

                return true;
            }
        }

        return false;
    }

    // ============================================================
    // GET BOM
    // ============================================================

    public List<BOMLine> getBOM(Long parentRevisionId) {

        // Make sure the parent revision actually exists
        if (!itemRevisionRepository.existsById(parentRevisionId)) {
            throw new BOMNotFoundException(
                    "Parent revision not found"
            );
        }

        return bomLineRepository
                .findByParentRevisionId(parentRevisionId);
    }

    // ============================================================
    // DELETE BOM LINE
    // ============================================================

    public void deleteBOMLine(Long bomLineId) {

        if (!bomLineRepository.existsById(bomLineId)) {
            throw new BOMNotFoundException(
                    "BOM line not found"
            );
        }

        bomLineRepository.deleteById(bomLineId);
    }
}
