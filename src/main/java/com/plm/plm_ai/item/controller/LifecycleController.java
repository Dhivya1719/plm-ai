package com.plm.plm_ai.item.controller;

import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.service.LifecycleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/revisions")
public class LifecycleController {

    private final LifecycleService lifecycleService;

    public LifecycleController(LifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    @PutMapping("/{revisionId}/submit-review")
    public ResponseEntity<ItemRevision> submitForReview(
            @PathVariable Long revisionId) {

        return ResponseEntity.ok(
                lifecycleService.moveToInReview(revisionId)
        );
    }

    @PutMapping("/{revisionId}/release")
    public ResponseEntity<ItemRevision> releaseRevision(
            @PathVariable Long revisionId) {

        return ResponseEntity.ok(
                lifecycleService.releaseRevision(revisionId)
        );
    }
}