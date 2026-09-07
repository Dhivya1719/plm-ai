package com.plm.plm_ai.change.service;

import com.plm.plm_ai.change.ECRApproval;
import com.plm.plm_ai.change.ECRApprovalDecision;
import com.plm.plm_ai.change.ECRStatus;
import com.plm.plm_ai.change.EngineeringChangeRequest;
import com.plm.plm_ai.change.repository.ECRApprovalRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.plm.plm_ai.change.repository.EngineeringChangeRequestRepository;

import java.util.List;

@Service
public class ECRApprovalService {

    private final EngineeringChangeRequestRepository ecrRepository;
    private final ECRApprovalRepository approvalRepository;
    private final ChangeHistoryService changeHistoryService;

    public ECRApprovalService(
            EngineeringChangeRequestRepository ecrRepository,
            ECRApprovalRepository approvalRepository,
            ChangeHistoryService changeHistoryService) {

        this.ecrRepository = ecrRepository;
        this.approvalRepository = approvalRepository;
        this.changeHistoryService = changeHistoryService;
    }

    @Transactional
    public ECRApproval reviewECR(
            Long ecrId,
            ECRApprovalDecision decision,
            String reviewedBy,
            String comment) {

        EngineeringChangeRequest ecr =
                ecrRepository.findById(ecrId)
                        .orElseThrow(() ->
                                new RuntimeException("ECR not found"));

        if (ecr.getStatus() != ECRStatus.UNDER_REVIEW) {
            throw new RuntimeException(
                    "ECR must be in UNDER_REVIEW status for approval/rejection");
        }

        if (reviewedBy == null || reviewedBy.isBlank()) {
            throw new RuntimeException(
                    "Reviewer name is required");
        }

        if (comment == null || comment.isBlank()) {
            throw new RuntimeException(
                    "Approval/rejection comment is required");
        }

        ECRApproval approval = new ECRApproval();

        approval.setEcr(ecr);
        approval.setDecision(decision);
        approval.setReviewedBy(reviewedBy);
        approval.setComment(comment);

        ECRApproval savedApproval =
                approvalRepository.save(approval);

        if (decision == ECRApprovalDecision.APPROVED) {
            ecr.setStatus(ECRStatus.APPROVED);
        } else if (decision == ECRApprovalDecision.REJECTED) {
            ecr.setStatus(ECRStatus.REJECTED);
        }

        ecrRepository.save(ecr);

        changeHistoryService.record(
                "ECR",
                ecr.getId(),
                decision == ECRApprovalDecision.APPROVED
                        ? "APPROVED"
                        : "REJECTED",
                "ECR "
                        + ecr.getChangeNumber()
                        + " was "
                        + decision
                        + " by "
                        + reviewedBy
                        + ". Comment: "
                        + comment,
                reviewedBy,
                ecr,
                null
        );

        return savedApproval;
    }

    public List<ECRApproval> getApprovalHistory(Long ecrId) {

        if (!ecrRepository.existsById(ecrId)) {
            throw new RuntimeException("ECR not found");
        }

        return approvalRepository
                .findByEcrIdOrderByReviewedAtAsc(ecrId);
    }
}