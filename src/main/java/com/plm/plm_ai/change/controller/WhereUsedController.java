package com.plm.plm_ai.change.controller;

import com.plm.plm_ai.change.service.WhereUsedService;
import com.plm.plm_ai.item.BOMLine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/revisions")
public class WhereUsedController {

    private final WhereUsedService whereUsedService;

    public WhereUsedController(WhereUsedService whereUsedService) {
        this.whereUsedService = whereUsedService;
    }

    // FIND WHERE A REVISION IS USED
    @GetMapping("/{revisionId}/where-used")
    public ResponseEntity<List<BOMLine>> findWhereUsed(
            @PathVariable Long revisionId) {

        return ResponseEntity.ok(
                whereUsedService.findWhereUsed(revisionId)
        );
    }
}