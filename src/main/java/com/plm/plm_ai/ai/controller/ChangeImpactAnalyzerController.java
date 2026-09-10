package com.plm.plm_ai.ai.controller;

import com.plm.plm_ai.ai.dto.ChangeImpactResponse;
import com.plm.plm_ai.ai.service.ChangeImpactAnalyzerService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class ChangeImpactAnalyzerController {

    private final ChangeImpactAnalyzerService analyzerService;

    public ChangeImpactAnalyzerController(
            ChangeImpactAnalyzerService analyzerService) {

        this.analyzerService = analyzerService;
    }

    @PreAuthorize("hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')")
    @PostMapping("/change-impact/{ecrId}")
    public ResponseEntity<ChangeImpactResponse> analyzeChangeImpact(
            @PathVariable Long ecrId) {

        return ResponseEntity.ok(
                analyzerService.analyze(ecrId)
        );
    }
}