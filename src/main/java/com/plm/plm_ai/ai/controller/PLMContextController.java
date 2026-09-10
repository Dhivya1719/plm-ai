package com.plm.plm_ai.ai.controller;

import com.plm.plm_ai.ai.dto.PLMContextResponse;
import com.plm.plm_ai.ai.service.PLMContextBuilderService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class PLMContextController {

    private final PLMContextBuilderService contextBuilderService;

    public PLMContextController(
            PLMContextBuilderService contextBuilderService) {

        this.contextBuilderService =
                contextBuilderService;
    }

    @PreAuthorize("hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')")
    @GetMapping("/change-impact/{ecrId}/context")
    public ResponseEntity<PLMContextResponse> getPLMContext(
            @PathVariable Long ecrId) {

        return ResponseEntity.ok(
                contextBuilderService.buildContext(ecrId)
        );
    }
}