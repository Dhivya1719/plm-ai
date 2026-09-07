package com.plm.plm_ai.change.service;

import com.plm.plm_ai.change.ECOBOMChange;
import com.plm.plm_ai.change.ECOBOMChangeType;
import com.plm.plm_ai.change.ECOStatus;
import com.plm.plm_ai.change.EngineeringChangeOrder;
import com.plm.plm_ai.change.repository.ECOBOMChangeRepository;
import com.plm.plm_ai.change.repository.EngineeringChangeOrderRepository;
import com.plm.plm_ai.item.BOMLine;
import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.LifecycleStatus;
import com.plm.plm_ai.item.repository.BOMLineRepository;
import com.plm.plm_ai.item.repository.ItemRevisionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ECOBOMChangeService {

    private final ECOBOMChangeRepository ecoBOMChangeRepository;
    private final EngineeringChangeOrderRepository ecoRepository;
    private final BOMLineRepository bomLineRepository;
    private final ItemRevisionRepository itemRevisionRepository;
    private final ChangeHistoryService changeHistoryService;

    public ECOBOMChangeService(
            ECOBOMChangeRepository ecoBOMChangeRepository,
            EngineeringChangeOrderRepository ecoRepository,
            BOMLineRepository bomLineRepository,
            ItemRevisionRepository itemRevisionRepository,
            ChangeHistoryService changeHistoryService
    ) {
        this.ecoBOMChangeRepository = ecoBOMChangeRepository;
        this.ecoRepository = ecoRepository;
        this.bomLineRepository = bomLineRepository;
        this.itemRevisionRepository = itemRevisionRepository;
        this.changeHistoryService = changeHistoryService;
    }

    // ---------------------------------------------------------
    // COMMON VALIDATIONS
    // ---------------------------------------------------------

    private EngineeringChangeOrder validateECO(Long ecoId) {

        EngineeringChangeOrder eco = ecoRepository.findById(ecoId)
                .orElseThrow(() ->
                        new RuntimeException("ECO not found: " + ecoId)
                );

        if (eco.getStatus() != ECOStatus.IMPLEMENTATION) {
            throw new RuntimeException(
                    "BOM changes are allowed only when ECO is in IMPLEMENTATION status"
            );
        }

        return eco;
    }

    private ItemRevision validateParentRevision(Long parentRevisionId) {

        ItemRevision parentRevision = itemRevisionRepository
                .findById(parentRevisionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Parent revision not found: " + parentRevisionId
                        )
                );

        if (parentRevision.getStatus() == LifecycleStatus.RELEASED) {
            throw new RuntimeException(
                    "Cannot modify BOM of a RELEASED revision. Create a new revision first."
            );
        }

        return parentRevision;
    }

    private ItemRevision getChildRevision(Long childRevisionId) {

        return itemRevisionRepository.findById(childRevisionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Child revision not found: " + childRevisionId
                        )
                );
    }

    // ---------------------------------------------------------
    // ADD COMPONENT
    // ---------------------------------------------------------

    @Transactional
    public ECOBOMChange addComponent(
            Long ecoId,
            Long parentRevisionId,
            Long childRevisionId,
            Integer quantity,
            String reason,
            String changedBy
    ) {

        EngineeringChangeOrder eco = validateECO(ecoId);

        ItemRevision parentRevision =
                validateParentRevision(parentRevisionId);

        ItemRevision childRevision =
                getChildRevision(childRevisionId);

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        if (parentRevisionId.equals(childRevisionId)) {
            throw new RuntimeException(
                    "A revision cannot contain itself in its BOM"
            );
        }

        if (bomLineRepository.existsByParentRevisionIdAndChildRevisionId(
                parentRevisionId,
                childRevisionId
        )) {
            throw new RuntimeException(
                    "This component already exists in the BOM"
            );
        }

        // Create BOM line
        BOMLine bomLine = new BOMLine();
        bomLine.setParentRevision(parentRevision);
        bomLine.setChildRevision(childRevision);
        bomLine.setQuantity(quantity);

        bomLineRepository.save(bomLine);

        // Create ECO BOM change record
        ECOBOMChange change = new ECOBOMChange();

        change.setEco(eco);
        change.setChangeType(ECOBOMChangeType.ADD);
        change.setParentRevision(parentRevision);
        change.setOldChildRevision(null);
        change.setNewChildRevision(childRevision);
        change.setOldQuantity(0);
        change.setNewQuantity(quantity);
        change.setReason(reason);
        change.setChangedBy(changedBy);

        ECOBOMChange savedChange =
                ecoBOMChangeRepository.save(change);

        // -----------------------------------------------------
        // AUDIT: BOM ADD
        // -----------------------------------------------------

        changeHistoryService.record(
                "BOM",
                savedChange.getId(),
                "ADD",
                "Added child revision "
                        + childRevision.getItem().getItemNumber()
                        + " Rev "
                        + childRevision.getRevisionCode()
                        + " quantity "
                        + quantity
                        + " to parent revision "
                        + parentRevision.getRevisionCode(),
                changedBy,
                eco.getSourceEcr(),
                eco
        );

        return savedChange;
    }

    // ---------------------------------------------------------
    // REMOVE COMPONENT
    // ---------------------------------------------------------

    @Transactional
    public ECOBOMChange removeComponent(
            Long ecoId,
            Long parentRevisionId,
            Long childRevisionId,
            String reason,
            String changedBy
    ) {

        EngineeringChangeOrder eco = validateECO(ecoId);

        ItemRevision parentRevision =
                validateParentRevision(parentRevisionId);

        ItemRevision childRevision =
                getChildRevision(childRevisionId);

        BOMLine bomLine = bomLineRepository
                .findByParentRevisionId(parentRevisionId)
                .stream()
                .filter(line ->
                        line.getChildRevision()
                                .getId()
                                .equals(childRevisionId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "BOM component not found"
                        )
                );

        Integer oldQuantity = bomLine.getQuantity();

        // Remove BOM line
        bomLineRepository.delete(bomLine);

        // Create ECO BOM change record
        ECOBOMChange change = new ECOBOMChange();

        change.setEco(eco);
        change.setChangeType(ECOBOMChangeType.REMOVE);
        change.setParentRevision(parentRevision);
        change.setOldChildRevision(childRevision);
        change.setNewChildRevision(null);
        change.setOldQuantity(oldQuantity);
        change.setNewQuantity(0);
        change.setReason(reason);
        change.setChangedBy(changedBy);

        ECOBOMChange savedChange =
                ecoBOMChangeRepository.save(change);

        // -----------------------------------------------------
        // AUDIT: BOM REMOVE
        // -----------------------------------------------------

        changeHistoryService.record(
                "BOM",
                savedChange.getId(),
                "REMOVE",
                "Removed child revision "
                        + childRevision.getItem().getItemNumber()
                        + " Rev "
                        + childRevision.getRevisionCode()
                        + " quantity "
                        + oldQuantity
                        + " from parent revision "
                        + parentRevision.getRevisionCode(),
                changedBy,
                eco.getSourceEcr(),
                eco
        );

        return savedChange;
    }

    // ---------------------------------------------------------
    // CHANGE QUANTITY
    // ---------------------------------------------------------

    @Transactional
    public ECOBOMChange changeQuantity(
            Long ecoId,
            Long parentRevisionId,
            Long childRevisionId,
            Integer newQuantity,
            String reason,
            String changedBy
    ) {

        EngineeringChangeOrder eco = validateECO(ecoId);

        ItemRevision parentRevision =
                validateParentRevision(parentRevisionId);

        ItemRevision childRevision =
                getChildRevision(childRevisionId);

        if (newQuantity == null || newQuantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than zero"
            );
        }

        BOMLine bomLine = bomLineRepository
                .findByParentRevisionId(parentRevisionId)
                .stream()
                .filter(line ->
                        line.getChildRevision()
                                .getId()
                                .equals(childRevisionId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "BOM component not found"
                        )
                );

        Integer oldQuantity = bomLine.getQuantity();

        // Update BOM quantity
        bomLine.setQuantity(newQuantity);

        bomLineRepository.save(bomLine);

        // Create ECO BOM change record
        ECOBOMChange change = new ECOBOMChange();

        change.setEco(eco);
        change.setChangeType(ECOBOMChangeType.QUANTITY_CHANGE);
        change.setParentRevision(parentRevision);
        change.setOldChildRevision(childRevision);
        change.setNewChildRevision(childRevision);
        change.setOldQuantity(oldQuantity);
        change.setNewQuantity(newQuantity);
        change.setReason(reason);
        change.setChangedBy(changedBy);

        ECOBOMChange savedChange =
                ecoBOMChangeRepository.save(change);

        // -----------------------------------------------------
        // AUDIT: BOM QUANTITY CHANGE
        // -----------------------------------------------------

        changeHistoryService.record(
                "BOM",
                savedChange.getId(),
                "QUANTITY_CHANGE",
                "Changed quantity of child revision "
                        + childRevision.getItem().getItemNumber()
                        + " Rev "
                        + childRevision.getRevisionCode()
                        + " from "
                        + oldQuantity
                        + " to "
                        + newQuantity
                        + " in parent revision "
                        + parentRevision.getRevisionCode(),
                changedBy,
                eco.getSourceEcr(),
                eco
        );

        return savedChange;
    }

    // ---------------------------------------------------------
    // REPLACE COMPONENT
    // ---------------------------------------------------------

    @Transactional
    public ECOBOMChange replaceComponent(
            Long ecoId,
            Long parentRevisionId,
            Long oldChildRevisionId,
            Long newChildRevisionId,
            Integer newQuantity,
            String reason,
            String changedBy
    ) {

        EngineeringChangeOrder eco = validateECO(ecoId);

        ItemRevision parentRevision =
                validateParentRevision(parentRevisionId);

        ItemRevision oldChildRevision =
                getChildRevision(oldChildRevisionId);

        ItemRevision newChildRevision =
                getChildRevision(newChildRevisionId);

        if (oldChildRevisionId.equals(newChildRevisionId)) {
            throw new RuntimeException(
                    "Old and new components cannot be the same"
            );
        }

        if (parentRevisionId.equals(newChildRevisionId)) {
            throw new RuntimeException(
                    "A revision cannot contain itself in its BOM"
            );
        }

        BOMLine existingLine = bomLineRepository
                .findByParentRevisionId(parentRevisionId)
                .stream()
                .filter(line ->
                        line.getChildRevision()
                                .getId()
                                .equals(oldChildRevisionId)
                )
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Old BOM component not found"
                        )
                );

        if (bomLineRepository
                .existsByParentRevisionIdAndChildRevisionId(
                        parentRevisionId,
                        newChildRevisionId
                )) {

            throw new RuntimeException(
                    "New component already exists in the BOM"
            );
        }

        Integer oldQuantity = existingLine.getQuantity();

        if (newQuantity == null || newQuantity <= 0) {
            newQuantity = oldQuantity;
        }

        // Replace component
        existingLine.setChildRevision(newChildRevision);
        existingLine.setQuantity(newQuantity);

        bomLineRepository.save(existingLine);

        // Create ECO BOM change record
        ECOBOMChange change = new ECOBOMChange();

        change.setEco(eco);
        change.setChangeType(ECOBOMChangeType.REPLACE);
        change.setParentRevision(parentRevision);
        change.setOldChildRevision(oldChildRevision);
        change.setNewChildRevision(newChildRevision);
        change.setOldQuantity(oldQuantity);
        change.setNewQuantity(newQuantity);
        change.setReason(reason);
        change.setChangedBy(changedBy);

        ECOBOMChange savedChange =
                ecoBOMChangeRepository.save(change);

        // -----------------------------------------------------
        // AUDIT: BOM REPLACE
        // -----------------------------------------------------

        changeHistoryService.record(
                "BOM",
                savedChange.getId(),
                "REPLACE",
                "Replaced "
                        + oldChildRevision.getItem().getItemNumber()
                        + " Rev "
                        + oldChildRevision.getRevisionCode()
                        + " with "
                        + newChildRevision.getItem().getItemNumber()
                        + " Rev "
                        + newChildRevision.getRevisionCode()
                        + " in parent revision "
                        + parentRevision.getRevisionCode(),
                changedBy,
                eco.getSourceEcr(),
                eco
        );

        return savedChange;
    }

    // ---------------------------------------------------------
    // GET ECO BOM CHANGE HISTORY
    // ---------------------------------------------------------

    public List<ECOBOMChange> getChangesByECO(Long ecoId) {

        // Make sure ECO exists
        ecoRepository.findById(ecoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "ECO not found: " + ecoId
                        )
                );

        return ecoBOMChangeRepository.findByEcoId(ecoId);
    }
}