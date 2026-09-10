package com.plm.plm_ai.item.service;

import com.plm.plm_ai.item.BOMLine;
import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.exception.BOMCircularDependencyException;
import com.plm.plm_ai.item.exception.BOMDuplicateException;
import com.plm.plm_ai.item.exception.BOMNotFoundException;
import com.plm.plm_ai.item.exception.BOMSelfReferenceException;
import com.plm.plm_ai.item.repository.BOMLineRepository;
import com.plm.plm_ai.item.repository.ItemRevisionRepository;
import com.plm.plm_ai.item.dto.WhereUsedResponse;
import com.plm.plm_ai.change.dto.ImpactAnalysisResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
        // Before adding C -> A, check whether
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

    @Transactional
    public void deleteBOMLine(Long bomLineId) {

        if (!bomLineRepository.existsById(bomLineId)) {
            throw new BOMNotFoundException(
                    "BOM line not found"
            );
        }

        bomLineRepository.deleteById(bomLineId);
    }

    // ============================================================
    // UPDATE BOM QUANTITY
    // ============================================================

    @Transactional
    public BOMLine updateQuantity(
            Long bomLineId,
            Integer quantity) {

        // Rule 1: Quantity must be greater than zero
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        // Find BOM line
        BOMLine bomLine =
                bomLineRepository.findById(bomLineId)
                        .orElseThrow(() ->
                                new BOMNotFoundException(
                                        "BOM line not found"
                                )
                        );

        // Update quantity
        bomLine.setQuantity(quantity);

        return bomLineRepository.save(bomLine);
    }

    // ============================================================
    // REPLACE BOM COMPONENT
    // ============================================================

    @Transactional
    public BOMLine replaceComponent(
            Long bomLineId,
            Long newChildRevisionId) {

        // Find existing BOM line
        BOMLine bomLine =
                bomLineRepository.findById(bomLineId)
                        .orElseThrow(() ->
                                new BOMNotFoundException(
                                        "BOM line not found"
                                )
                        );

        // Find new child revision
        ItemRevision newChildRevision =
                itemRevisionRepository.findById(newChildRevisionId)
                        .orElseThrow(() ->
                                new BOMNotFoundException(
                                        "Replacement revision not found"
                                )
                        );

        // Get parent revision
        ItemRevision parentRevision =
                bomLine.getParentRevision();

        // Prevent self-reference
        if (parentRevision.getId().equals(newChildRevisionId)) {
            throw new BOMSelfReferenceException(
                    "A revision cannot be its own component"
            );
        }

        // Prevent circular dependency
        if (hasPathToTarget(
                newChildRevisionId,
                parentRevision.getId(),
                new HashSet<>())) {

            throw new BOMCircularDependencyException(
                    "Circular BOM dependency detected"
            );
        }

        // Prevent duplicate component
        boolean duplicate =
                bomLineRepository
                        .existsByParentRevisionIdAndChildRevisionId(
                                parentRevision.getId(),
                                newChildRevisionId
                        );

        if (duplicate) {
            throw new BOMDuplicateException(
                    "Replacement component already exists in the BOM"
            );
        }

        // Preserve the existing quantity.
        // Only the child component is replaced.
        bomLine.setChildRevision(newChildRevision);

        return bomLineRepository.save(bomLine);
    }

    // ============================================================
    // WHERE USED
    // ============================================================

    public List<WhereUsedResponse> getWhereUsed(Long revisionId) {

        // Verify that the revision exists
        itemRevisionRepository.findById(revisionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Item Revision not found with id: "
                                        + revisionId
                        ));

        // Find all BOM lines where this revision
        // is used as a child
        List<BOMLine> bomLines =
                bomLineRepository.findByChildRevisionId(
                        revisionId
                );

        List<WhereUsedResponse> response =
                new ArrayList<>();

        for (BOMLine bomLine : bomLines) {

            ItemRevision parentRevision =
                    bomLine.getParentRevision();

            response.add(
                    new WhereUsedResponse(
                            parentRevision.getId(),
                            parentRevision.getItem().getItemNumber(),
                            parentRevision.getItem().getName(),
                            parentRevision.getRevisionCode(),
                            bomLine.getQuantity()
                    )
            );
        }

        return response;
    }

    // ============================================================
    // IMPACT ANALYSIS
    // ============================================================

    public ImpactAnalysisResponse analyzeImpact(
            Long revisionId) {

        ItemRevision affectedRevision =
                itemRevisionRepository.findById(revisionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Revision not found"
                                ));

        // Find direct parents using this revision
        List<BOMLine> directWhereUsed =
                bomLineRepository.findByChildRevisionId(
                        revisionId
                );

        List<ImpactAnalysisResponse.ImpactItem>
                directImpacts =
                new ArrayList<>();

        Set<Long> visited =
                new HashSet<>();

        // ========================================================
        // DIRECT IMPACTS
        // ========================================================

        for (BOMLine bomLine : directWhereUsed) {

            ItemRevision parent =
                    bomLine.getParentRevision();

            directImpacts.add(
                    new ImpactAnalysisResponse.ImpactItem(
                            parent.getId(),
                            parent.getItem().getItemNumber(),
                            parent.getRevisionCode()
                    )
            );

            visited.add(parent.getId());
        }

        // ========================================================
        // INDIRECT IMPACTS
        // ========================================================

        List<ImpactAnalysisResponse.ImpactItem>
                indirectImpacts =
                new ArrayList<>();

        for (BOMLine bomLine : directWhereUsed) {

            findIndirectImpacts(
                    bomLine.getParentRevision().getId(),
                    visited,
                    indirectImpacts
            );
        }

        // ========================================================
        // RISK CALCULATION
        // ========================================================

        int bomImpactCount =
                directImpacts.size()
                        + indirectImpacts.size();

        String risk;

        if (bomImpactCount == 0) {
            risk = "LOW";
        }
        else if (bomImpactCount <= 2) {
            risk = "MEDIUM";
        }
        else {
            risk = "HIGH";
        }

        // ========================================================
        // RESPONSE
        // ========================================================

        return new ImpactAnalysisResponse(
                affectedRevision.getId(),
                affectedRevision.getItem().getItemNumber(),
                affectedRevision.getRevisionCode(),
                directImpacts,
                indirectImpacts,
                bomImpactCount,
                risk
        );
    }

    // ============================================================
    // FIND INDIRECT IMPACTS
    // ============================================================

    private void findIndirectImpacts(
            Long revisionId,
            Set<Long> visited,
            List<ImpactAnalysisResponse.ImpactItem>
                    indirectImpacts) {

        // Find all assemblies where this revision is used
        List<BOMLine> whereUsed =
                bomLineRepository.findByChildRevisionId(
                        revisionId
                );

        for (BOMLine bomLine : whereUsed) {

            ItemRevision parent =
                    bomLine.getParentRevision();

            // Already processed
            if (visited.contains(parent.getId())) {
                continue;
            }

            visited.add(parent.getId());

            indirectImpacts.add(
                    new ImpactAnalysisResponse.ImpactItem(
                            parent.getId(),
                            parent.getItem().getItemNumber(),
                            parent.getRevisionCode()
                    )
            );

            // Continue recursively upward
            findIndirectImpacts(
                    parent.getId(),
                    visited,
                    indirectImpacts
            );
        }
    }
}