package com.plm.plm_ai.change.service;

import com.plm.plm_ai.change.ECRStatus;
import com.plm.plm_ai.change.ECRAffectedRevision;
import com.plm.plm_ai.change.EngineeringChangeRequest;
import com.plm.plm_ai.change.repository.ECRAffectedRevisionRepository;
import com.plm.plm_ai.change.repository.EngineeringChangeRequestRepository;
import com.plm.plm_ai.change.dto.ImpactAnalysisResponse;
import com.plm.plm_ai.item.service.BOMLineService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EngineeringChangeRequestService {

    private final EngineeringChangeRequestRepository ecrRepository;
    private final ECRAffectedRevisionRepository affectedRevisionRepository;
    private final BOMLineService bomLineService;
    private final ChangeHistoryService changeHistoryService;

    public EngineeringChangeRequestService(
            EngineeringChangeRequestRepository ecrRepository,
            ECRAffectedRevisionRepository affectedRevisionRepository,
            BOMLineService bomLineService,
            ChangeHistoryService changeHistoryService) {

        this.ecrRepository = ecrRepository;
        this.affectedRevisionRepository = affectedRevisionRepository;
        this.bomLineService = bomLineService;
        this.changeHistoryService = changeHistoryService;
    }

    // ============================================================
    // CREATE ECR
    // ============================================================

    public EngineeringChangeRequest createECR(
            EngineeringChangeRequest ecr) {

        String changeNumber = generateChangeNumber();

        ecr.setChangeNumber(changeNumber);

        // Every new ECR starts as DRAFT
        ecr.setStatus(ECRStatus.DRAFT);

        EngineeringChangeRequest savedECR =
                ecrRepository.save(ecr);

        // Record ECR creation in audit history
        changeHistoryService.record(
                "ECR",
                savedECR.getId(),
                "CREATED",
                "ECR "
                        + savedECR.getChangeNumber()
                        + " created",
                savedECR.getRequestedBy(),
                savedECR,
                null
        );

        return savedECR;
    }

    // ============================================================
    // GENERATE ECR NUMBER
    // ============================================================

    private String generateChangeNumber() {

        Optional<EngineeringChangeRequest> latest =
                ecrRepository.findTopByOrderByIdDesc();

        if (latest.isEmpty()) {
            return "ECR-00001";
        }

        String latestNumber =
                latest.get().getChangeNumber();

        int number = Integer.parseInt(
                latestNumber.substring(4)
        );

        return String.format(
                "ECR-%05d",
                number + 1
        );
    }

    // ============================================================
    // GET ALL ECRs
    // ============================================================

    public List<EngineeringChangeRequest> getAllECRs() {

        return ecrRepository.findAll();
    }

    // ============================================================
    // GET ECR BY ID
    // ============================================================

    public EngineeringChangeRequest getECRById(
            Long ecrId) {

        return ecrRepository.findById(ecrId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Engineering Change Request not found"
                        ));
    }

    // ============================================================
    // UPDATE ECR STATUS
    // ============================================================

    public EngineeringChangeRequest updateStatus(
            Long ecrId,
            ECRStatus newStatus) {

        EngineeringChangeRequest ecr =
                getECRById(ecrId);

        ECRStatus currentStatus =
                ecr.getStatus();

        boolean validTransition = false;

        // DRAFT → SUBMITTED
        if (currentStatus == ECRStatus.DRAFT
                && newStatus == ECRStatus.SUBMITTED) {

            validTransition = true;
        }

        // DRAFT → CANCELLED
        else if (currentStatus == ECRStatus.DRAFT
                && newStatus == ECRStatus.CANCELLED) {

            validTransition = true;
        }

        // SUBMITTED → UNDER_REVIEW
        else if (currentStatus == ECRStatus.SUBMITTED
                && newStatus == ECRStatus.UNDER_REVIEW) {

            validTransition = true;
        }

        // SUBMITTED → CANCELLED
        else if (currentStatus == ECRStatus.SUBMITTED
                && newStatus == ECRStatus.CANCELLED) {

            validTransition = true;
        }

        // UNDER_REVIEW → APPROVED
        else if (currentStatus == ECRStatus.UNDER_REVIEW
                && newStatus == ECRStatus.APPROVED) {

            validTransition = true;
        }

        // UNDER_REVIEW → REJECTED
        else if (currentStatus == ECRStatus.UNDER_REVIEW
                && newStatus == ECRStatus.REJECTED) {

            validTransition = true;
        }

        if (!validTransition) {

            throw new RuntimeException(
                    "Invalid ECR status transition from "
                            + currentStatus
                            + " to "
                            + newStatus
            );
        }

        ecr.setStatus(newStatus);

        EngineeringChangeRequest updatedECR =
                ecrRepository.save(ecr);

        // Record status change in audit history
        changeHistoryService.record(
                "ECR",
                updatedECR.getId(),
                "STATUS_CHANGED",
                "ECR status changed from "
                        + currentStatus
                        + " to "
                        + newStatus,
                updatedECR.getRequestedBy(),
                updatedECR,
                null
        );

        return updatedECR;
    }

    // ============================================================
    // IMPACT ANALYSIS
    // ============================================================

    public List<ImpactAnalysisResponse> analyzeECRImpact(
            Long ecrId) {

        // Verify ECR exists
        EngineeringChangeRequest ecr =
                getECRById(ecrId);

        // Find all revisions affected by this ECR
        List<ECRAffectedRevision> affectedRevisions =
                affectedRevisionRepository
                        .findByEcrId(ecrId);

        // ECR must have at least one affected revision
        if (affectedRevisions.isEmpty()) {

            throw new RuntimeException(
                    "No affected revisions found for ECR "
                            + ecr.getChangeNumber()
            );
        }

        List<ImpactAnalysisResponse> reports =
                new ArrayList<>();

        // Analyze every affected revision
        for (ECRAffectedRevision affectedRevision :
                affectedRevisions) {

            Long revisionId =
                    affectedRevision
                            .getItemRevision()
                            .getId();

            ImpactAnalysisResponse report =
                    bomLineService.analyzeImpact(
                            revisionId
                    );

            reports.add(report);
        }

        return reports;
    }
}