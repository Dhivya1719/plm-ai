package com.plm.plm_ai.change.controller;

import com.plm.plm_ai.change.EngineeringChangeRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.plm.plm_ai.change.ECRStatus;
import com.plm.plm_ai.change.dto.ImpactAnalysisResponse;
import com.plm.plm_ai.change.service.EngineeringChangeRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/ecr")
public class EngineeringChangeRequestController {

    private final EngineeringChangeRequestService ecrService;

    public EngineeringChangeRequestController(
            EngineeringChangeRequestService ecrService) {

        this.ecrService = ecrService;
    }

    // CREATE ECR
    @PostMapping
    public ResponseEntity<EngineeringChangeRequest> createECR(
            @RequestBody EngineeringChangeRequest ecr) {

        return new ResponseEntity<>(
                ecrService.createECR(ecr),
                HttpStatus.CREATED
        );
    }

    // GET ALL ECRs
    @GetMapping
    public ResponseEntity<List<EngineeringChangeRequest>> getAllECRs() {

        return ResponseEntity.ok(
                ecrService.getAllECRs()
        );
    }

    // GET ECR BY ID
    @GetMapping("/{ecrId}")
    public ResponseEntity<EngineeringChangeRequest> getECRById(
            @PathVariable Long ecrId) {

        return ResponseEntity.ok(
                ecrService.getECRById(ecrId)
        );
    }

    // UPDATE ECR STATUS
    @PutMapping("/{ecrId}/status")
    public ResponseEntity<EngineeringChangeRequest> updateStatus(
            @PathVariable Long ecrId,
            @RequestParam ECRStatus status) {

        return ResponseEntity.ok(
                ecrService.updateStatus(ecrId, status)
        );
    }
    // ============================================================
// ECR IMPACT ANALYSIS
// ============================================================

    @GetMapping("/{ecrId}/impact-analysis")
    public ResponseEntity<List<ImpactAnalysisResponse>> analyzeECRImpact(
            @PathVariable Long ecrId) {

        return ResponseEntity.ok(
                ecrService.analyzeECRImpact(ecrId)
        );
    }

}