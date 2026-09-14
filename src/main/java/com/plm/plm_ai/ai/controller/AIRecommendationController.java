package com.plm.plm_ai.ai.controller;

import com.plm.plm_ai.ai.dto.AIRecommendationResponse;
import com.plm.plm_ai.ai.service.AIRecommendationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/change-impact")
public class AIRecommendationController {

    private final AIRecommendationService
            recommendationService;

    public AIRecommendationController(
            AIRecommendationService recommendationService) {

        this.recommendationService =
                recommendationService;
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @PostMapping("/{ecrId}/recommendations")
    public ResponseEntity<AIRecommendationResponse>
    generateRecommendations(
            @PathVariable Long ecrId) {

        return ResponseEntity.ok(
                recommendationService
                        .generateRecommendations(ecrId)
        );
    }
}