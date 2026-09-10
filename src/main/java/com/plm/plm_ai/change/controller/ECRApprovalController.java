package com.plm.plm_ai.change.controller;

import com.plm.plm_ai.change.ECRApproval;
import com.plm.plm_ai.change.ECRApprovalDecision;
import com.plm.plm_ai.change.service.ECRApprovalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ecr")
public class ECRApprovalController {

    private final ECRApprovalService approvalService;

    public ECRApprovalController(
            ECRApprovalService approvalService) {

        this.approvalService = approvalService;
    }

    @PostMapping("/{ecrId}/review")
    public ResponseEntity<ECRApproval> reviewECR(
            @PathVariable Long ecrId,
            @RequestParam ECRApprovalDecision decision,
            @RequestParam String reviewedBy,
            @RequestParam String comment) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        approvalService.reviewECR(
                                ecrId,
                                decision,
                                reviewedBy,
                                comment
                        )
                );
    }
}