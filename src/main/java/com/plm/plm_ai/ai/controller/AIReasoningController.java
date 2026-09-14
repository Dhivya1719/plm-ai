package com.plm.plm_ai.ai.controller;

import com.plm.plm_ai.ai.dto.AIReasoningResponse;
import com.plm.plm_ai.ai.service.AIReasoningService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/change-impact")
public class AIReasoningController {

    private final AIReasoningService aiReasoningService;

    public AIReasoningController(
            AIReasoningService aiReasoningService) {

        this.aiReasoningService =
                aiReasoningService;
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @PostMapping("/{ecrId}/ai-reasoning")
    public ResponseEntity<AIReasoningResponse> analyze(
            @PathVariable Long ecrId) {

        return ResponseEntity.ok(
                aiReasoningService.analyze(ecrId)
        );
    }
}