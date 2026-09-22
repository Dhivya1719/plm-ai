package com.plm.plm_ai.ai.assistant;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/assistant")
public class PLMAssistantController {

    private final PLMAssistantService assistantService;

    public PLMAssistantController(
            PLMAssistantService assistantService) {

        this.assistantService =
                assistantService;
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @PostMapping("/ask")
    public ResponseEntity<PLMAssistantResponse>
    ask(
            @RequestBody PLMAssistantRequest request) {

        return ResponseEntity.ok(
                assistantService.ask(
                        request.getQuestion()
                )
        );
    }
}