package com.plm.plm_ai.change.controller;

import com.plm.plm_ai.change.ChangeHistory;
import com.plm.plm_ai.change.service.ChangeHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/change-history")
public class ChangeHistoryController {

    private final ChangeHistoryService changeHistoryService;

    public ChangeHistoryController(
            ChangeHistoryService changeHistoryService) {

        this.changeHistoryService = changeHistoryService;
    }

    @GetMapping("/ecr/{ecrId}")
    public ResponseEntity<List<ChangeHistory>> getECRHistory(
            @PathVariable Long ecrId) {

        return ResponseEntity.ok(
                changeHistoryService.getECRHistory(ecrId)
        );
    }

    @GetMapping("/eco/{ecoId}")
    public ResponseEntity<List<ChangeHistory>> getECOHistory(
            @PathVariable Long ecoId) {

        return ResponseEntity.ok(
                changeHistoryService.getECOHistory(ecoId)
        );
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<List<ChangeHistory>> getEntityHistory(
            @PathVariable String entityType,
            @PathVariable Long entityId) {

        return ResponseEntity.ok(
                changeHistoryService.getEntityHistory(
                        entityType,
                        entityId
                )
        );
    }
}