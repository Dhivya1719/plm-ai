package com.plm.plm_ai.change.service;

import com.plm.plm_ai.change.ECOStatus;
import com.plm.plm_ai.change.EngineeringChangeOrder;
import com.plm.plm_ai.change.EngineeringChangeRequest;
import com.plm.plm_ai.change.ECRStatus;
import com.plm.plm_ai.change.repository.EngineeringChangeOrderRepository;
import com.plm.plm_ai.change.repository.EngineeringChangeRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EngineeringChangeOrderService {

    private final EngineeringChangeOrderRepository ecoRepository;
    private final EngineeringChangeRequestRepository ecrRepository;
    private final ChangeHistoryService changeHistoryService;

    public EngineeringChangeOrderService(
            EngineeringChangeOrderRepository ecoRepository,
            EngineeringChangeRequestRepository ecrRepository,
            ChangeHistoryService changeHistoryService) {

        this.ecoRepository = ecoRepository;
        this.ecrRepository = ecrRepository;
        this.changeHistoryService = changeHistoryService;
    }

    // ============================================================
    // CREATE ECO FROM APPROVED ECR
    // ============================================================

    public EngineeringChangeOrder createECO(
            Long ecrId,
            String createdBy) {

        EngineeringChangeRequest ecr =
                ecrRepository.findById(ecrId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "ECR not found with id: " + ecrId
                                ));

        // ECO can only be created from APPROVED ECR
        if (ecr.getStatus() != ECRStatus.APPROVED) {

            throw new RuntimeException(
                    "ECO can only be created from an APPROVED ECR"
            );
        }

        // Prevent multiple ECOs for the same ECR
        if (ecoRepository.existsBySourceEcrId(ecrId)) {

            throw new RuntimeException(
                    "An ECO already exists for this ECR"
            );
        }

        EngineeringChangeOrder eco =
                new EngineeringChangeOrder();

        eco.setEcoNumber(generateEcoNumber());

        eco.setTitle(ecr.getTitle());

        eco.setDescription(ecr.getDescription());

        eco.setStatus(ECOStatus.DRAFT);

        eco.setSourceEcr(ecr);

        eco.setCreatedBy(createdBy);

        EngineeringChangeOrder savedECO =
                ecoRepository.save(eco);

        // ========================================================
        // AUDIT: ECO CREATED
        // ========================================================

        changeHistoryService.record(
                "ECO",
                savedECO.getId(),
                "CREATED",
                "ECO "
                        + savedECO.getEcoNumber()
                        + " created from ECR "
                        + savedECO.getSourceEcr().getChangeNumber(),
                createdBy,
                savedECO.getSourceEcr(),
                savedECO
        );

        return savedECO;
    }

    // ============================================================
    // GENERATE ECO NUMBER
    // ============================================================

    private String generateEcoNumber() {

        Optional<EngineeringChangeOrder> latest =
                ecoRepository.findTopByOrderByIdDesc();

        if (latest.isEmpty()) {

            return "ECO-00001";
        }

        String latestNumber =
                latest.get().getEcoNumber();

        int number =
                Integer.parseInt(
                        latestNumber.substring(4)
                );

        return String.format(
                "ECO-%05d",
                number + 1
        );
    }

    // ============================================================
    // GET ALL ECOs
    // ============================================================

    public List<EngineeringChangeOrder> getAllECOs() {

        return ecoRepository.findAll();
    }

    // ============================================================
    // GET ECO BY ID
    // ============================================================

    public EngineeringChangeOrder getECOById(
            Long ecoId) {

        return ecoRepository.findById(ecoId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "ECO not found with id: " + ecoId
                        ));
    }

    // ============================================================
    // UPDATE ECO STATUS
    // ============================================================

    public EngineeringChangeOrder updateStatus(
            Long ecoId,
            ECOStatus newStatus) {

        EngineeringChangeOrder eco =
                getECOById(ecoId);

        ECOStatus oldStatus =
                eco.getStatus();

        boolean validTransition = false;

        // ========================================================
        // ALLOWED STATUS TRANSITIONS
        // ========================================================

        // DRAFT → OPEN
        if (oldStatus == ECOStatus.DRAFT
                && newStatus == ECOStatus.OPEN) {

            validTransition = true;
        }

        // DRAFT → CANCELLED
        else if (oldStatus == ECOStatus.DRAFT
                && newStatus == ECOStatus.CANCELLED) {

            validTransition = true;
        }

        // OPEN → IMPLEMENTATION
        else if (oldStatus == ECOStatus.OPEN
                && newStatus == ECOStatus.IMPLEMENTATION) {

            validTransition = true;
        }

        // OPEN → CANCELLED
        else if (oldStatus == ECOStatus.OPEN
                && newStatus == ECOStatus.CANCELLED) {

            validTransition = true;
        }

        // IMPLEMENTATION → VERIFICATION
        else if (oldStatus == ECOStatus.IMPLEMENTATION
                && newStatus == ECOStatus.VERIFICATION) {

            validTransition = true;
        }

        // VERIFICATION → COMPLETED
        else if (oldStatus == ECOStatus.VERIFICATION
                && newStatus == ECOStatus.COMPLETED) {

            validTransition = true;
        }

        // ========================================================
        // INVALID TRANSITION
        // ========================================================

        if (!validTransition) {

            throw new RuntimeException(
                    "Invalid ECO status transition from "
                            + oldStatus
                            + " to "
                            + newStatus
            );
        }

        eco.setStatus(newStatus);

        EngineeringChangeOrder updatedECO =
                ecoRepository.save(eco);

        // ========================================================
        // AUDIT: ECO STATUS CHANGE
        // ========================================================

        changeHistoryService.record(
                "ECO",
                updatedECO.getId(),
                "STATUS_CHANGED",
                "ECO status changed from "
                        + oldStatus
                        + " to "
                        + newStatus,
                "SYSTEM",
                updatedECO.getSourceEcr(),
                updatedECO
        );

        return updatedECO;
    }
}