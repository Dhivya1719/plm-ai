package com.plm.plm_ai.item.controller;

import com.plm.plm_ai.item.BOMLine;
import com.plm.plm_ai.item.service.BOMLineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.plm.plm_ai.change.dto.ImpactAnalysisResponse;

import java.util.List;

@RestController
@RequestMapping("/api/revisions")
public class BOMLineController {

    private final BOMLineService bomLineService;

    public BOMLineController(BOMLineService bomLineService) {
        this.bomLineService = bomLineService;
    }

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

    @GetMapping("/{parentRevisionId}/bom")
    public ResponseEntity<List<BOMLine>> getBOM(
            @PathVariable Long parentRevisionId) {

        return ResponseEntity.ok(
                bomLineService.getBOM(parentRevisionId)
        );
    }

    @DeleteMapping("/bom/{bomLineId}")
    public ResponseEntity<Void> deleteBOMLine(
            @PathVariable Long bomLineId) {

        bomLineService.deleteBOMLine(bomLineId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{revisionId}/impact-analysis")
    public ResponseEntity<ImpactAnalysisResponse> analyzeImpact(
            @PathVariable Long revisionId) {

        return ResponseEntity.ok(
                bomLineService.analyzeImpact(revisionId)
        );
    }
}