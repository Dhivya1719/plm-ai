package com.plm.plm_ai.item.controller;

import com.plm.plm_ai.item.BOMLine;
import com.plm.plm_ai.item.service.BOMLineService;
import com.plm.plm_ai.change.dto.ImpactAnalysisResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/revisions")
public class BOMLineController {

    private final BOMLineService bomLineService;

    public BOMLineController(BOMLineService bomLineService) {
        this.bomLineService = bomLineService;
    }

    // ============================================================
    // ADD BOM COMPONENT
    // ENGINEER + CHANGE_MANAGER
    // ============================================================

    @PreAuthorize("hasAnyRole('ENGINEER', 'CHANGE_MANAGER')")
    @PostMapping("/{parentRevisionId}/bom")
    public ResponseEntity<BOMLine> addComponent(
            @PathVariable Long parentRevisionId,
            @RequestParam Long childRevisionId,
            @RequestParam Integer quantity) {

        return ResponseEntity.status(201).body(
                bomLineService.addComponent(
                        parentRevisionId,
                        childRevisionId,
                        quantity
                )
        );
    }

    // ============================================================
    // VIEW BOM
    // ALL ROLES
    // ============================================================

    @PreAuthorize("hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')")
    @GetMapping("/{parentRevisionId}/bom")
    public ResponseEntity<List<BOMLine>> getBOM(
            @PathVariable Long parentRevisionId) {

        return ResponseEntity.ok(
                bomLineService.getBOM(parentRevisionId)
        );
    }

    // ============================================================
    // UPDATE BOM QUANTITY
    // ENGINEER + CHANGE_MANAGER
    // ============================================================

    @PreAuthorize("hasAnyRole('ENGINEER', 'CHANGE_MANAGER')")
    @PutMapping("/bom/{bomLineId}/quantity")
    public ResponseEntity<BOMLine> updateQuantity(
            @PathVariable Long bomLineId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                bomLineService.updateQuantity(
                        bomLineId,
                        quantity
                )
        );
    }

    // ============================================================
    // REPLACE BOM COMPONENT
    // ENGINEER + CHANGE_MANAGER
    // ============================================================

    @PreAuthorize("hasAnyRole('ENGINEER', 'CHANGE_MANAGER')")
    @PutMapping("/bom/{bomLineId}/replace")
    public ResponseEntity<BOMLine> replaceComponent(
            @PathVariable Long bomLineId,
            @RequestParam Long newChildRevisionId) {

        return ResponseEntity.ok(
                bomLineService.replaceComponent(
                        bomLineId,
                        newChildRevisionId
                )
        );
    }

    // ============================================================
    // DELETE BOM COMPONENT
    // ENGINEER + CHANGE_MANAGER
    // ============================================================

    @PreAuthorize("hasAnyRole('ENGINEER', 'CHANGE_MANAGER')")
    @DeleteMapping("/bom/{bomLineId}")
    public ResponseEntity<Void> deleteBOMLine(
            @PathVariable Long bomLineId) {

        bomLineService.deleteBOMLine(bomLineId);

        return ResponseEntity.noContent().build();
    }

    // ============================================================
    // IMPACT ANALYSIS
    // ALL ROLES
    // ============================================================

    @PreAuthorize("hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')")
    @GetMapping("/{revisionId}/impact-analysis")
    public ResponseEntity<ImpactAnalysisResponse> analyzeImpact(
            @PathVariable Long revisionId) {

        return ResponseEntity.ok(
                bomLineService.analyzeImpact(revisionId)
        );
    }
}