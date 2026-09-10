package com.plm.plm_ai.change.service;

import com.plm.plm_ai.change.ApprovalRecord;
import com.plm.plm_ai.change.ECRStatus;
import com.plm.plm_ai.change.EngineeringChangeRequest;
import com.plm.plm_ai.change.repository.ApprovalRecordRepository;
import com.plm.plm_ai.change.repository.EngineeringChangeRequestRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApprovalRecordService {

    private final ApprovalRecordRepository approvalRepository;
    private final EngineeringChangeRequestRepository ecrRepository;

    public ApprovalRecordService(
            ApprovalRecordRepository approvalRepository,
            EngineeringChangeRequestRepository ecrRepository) {

        this.approvalRepository = approvalRepository;
        this.ecrRepository = ecrRepository;
    }

    @Transactional
    public ApprovalRecord recordApproval(
            Long ecrId,
            String approver,
            ECRStatus decision,
            String comment) {

        EngineeringChangeRequest ecr =
                ecrRepository.findById(ecrId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "ECR not found"));

        if (decision != ECRStatus.APPROVED &&
                decision != ECRStatus.REJECTED) {

            throw new RuntimeException(
                    "Decision must be APPROVED or REJECTED");
        }

        ApprovalRecord record = new ApprovalRecord();

        record.setEcr(ecr);
        record.setApprover(approver);
        record.setDecision(decision);
        record.setComment(comment);

        return approvalRepository.save(record);
    }

    public List<ApprovalRecord> getApprovalHistory(Long ecrId) {

        return approvalRepository
                .findByEcrIdOrderByCreatedAtAsc(ecrId);
    }
}