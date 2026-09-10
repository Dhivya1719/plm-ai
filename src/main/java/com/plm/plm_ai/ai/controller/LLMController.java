package com.plm.plm_ai.ai.controller;

import com.plm.plm_ai.ai.dto.LLMRequest;
import com.plm.plm_ai.ai.dto.LLMResponse;
import com.plm.plm_ai.ai.service.LLMService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/llm")
public class LLMController {

    private final LLMService llmService;

    public LLMController(LLMService llmService) {
        this.llmService = llmService;
    }

    @PreAuthorize("hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')")
    @PostMapping("/generate")
    public ResponseEntity<LLMResponse> generate(
            @RequestBody LLMRequest request) {

        String result =
                llmService.generateResponse(
                        request.getPrompt()
                );

        return ResponseEntity.ok(
                new LLMResponse(result)
        );
    }
}