package com.plm.plm_ai.ai.rag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/rag")
public class SemanticSearchController {

    private final SemanticSearchService searchService;

    public SemanticSearchController(
            SemanticSearchService searchService) {

        this.searchService =
                searchService;
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @GetMapping("/search")
    public ResponseEntity<SemanticSearchResponse>
    search(
            @RequestParam String query,
            @RequestParam(defaultValue = "5")
            int topK) {

        return ResponseEntity.ok(
                searchService.search(
                        query,
                        Math.min(topK, 10)
                )
        );
    }
}