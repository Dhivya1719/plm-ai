package com.plm.plm_ai.change.controller;

import com.plm.plm_ai.change.ApprovalRecord;
import com.plm.plm_ai.change.ECRStatus;
import com.plm.plm_ai.change.service.ApprovalRecordService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecr")
public class ApprovalRecordController {

    private final ApprovalRecordService approvalService;

    public ApprovalRecordController(
            ApprovalRecordService approvalService) {

        this.approvalService = approvalService;
    }

    @PreAuthorize("hasAnyRole('REVIEWER', 'CHANGE_MANAGER')")
    @PostMapping("/{ecrId}/approval")
    public ResponseEntity<ApprovalRecord> recordApproval(
            @PathVariable Long ecrId,
            @RequestParam String approver,
            @RequestParam ECRStatus decision,
            @RequestParam String comment) {

        return ResponseEntity.ok(
                approvalService.recordApproval(
                        ecrId,
                        approver,
                        decision,
                        comment
                )
        );
    }

    @PreAuthorize("hasAnyRole('REVIEWER', 'CHANGE_MANAGER')")
    @GetMapping("/{ecrId}/approval-history")
    public ResponseEntity<List<ApprovalRecord>> getHistory(
            @PathVariable Long ecrId) {

        return ResponseEntity.ok(
                approvalService.getApprovalHistory(ecrId)
        );
    }
}