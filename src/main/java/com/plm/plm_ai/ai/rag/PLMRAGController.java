package com.plm.plm_ai.ai.rag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/rag")
public class PLMRAGController {

    private final PLMRAGService ragService;

    public PLMRAGController(
            PLMRAGService ragService) {

        this.ragService =
                ragService;
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @GetMapping("/ask")
    public ResponseEntity<PLMRAGResponse>
    ask(
            @RequestParam String question) {

        return ResponseEntity.ok(
                ragService.answer(question)
        );
    }
}