package com.plm.plm_ai.ai.controller;

import com.plm.plm_ai.ai.dto.FinalChangeImpactReport;
import com.plm.plm_ai.ai.service.FinalChangeImpactReportService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/change-impact")
public class FinalChangeImpactReportController {

    private final FinalChangeImpactReportService
            reportService;

    public FinalChangeImpactReportController(
            FinalChangeImpactReportService reportService) {

        this.reportService = reportService;
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @PostMapping("/{ecrId}/final-report")
    public ResponseEntity<FinalChangeImpactReport>
    generateFinalReport(
            @PathVariable Long ecrId) {

        return ResponseEntity.ok(
                reportService.generateReport(ecrId)
        );
    }
}