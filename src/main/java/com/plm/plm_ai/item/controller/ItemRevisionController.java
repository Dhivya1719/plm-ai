package com.plm.plm_ai.item.controller;

import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.service.ItemRevisionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ItemRevisionController {

    private final ItemRevisionService revisionService;

    public ItemRevisionController(ItemRevisionService revisionService) {
        this.revisionService = revisionService;
    }

    // CREATE REVISION
    @PostMapping("/items/{itemId}/revisions")
    public ResponseEntity<ItemRevision> createRevision(
            @PathVariable Long itemId,
            @RequestBody ItemRevision revision) {

        return new ResponseEntity<>(
                revisionService.createRevision(itemId, revision),
                HttpStatus.CREATED
        );
    }

    // GET ALL REVISIONS FOR AN ITEM
    @GetMapping("/items/{itemId}/revisions")
    public ResponseEntity<List<ItemRevision>> getRevisionsByItem(
            @PathVariable Long itemId) {

        return ResponseEntity.ok(
                revisionService.getRevisionsByItem(itemId)
        );
    }

    // GET ONE REVISION
    @GetMapping("/revisions/{revisionId}")
    public ResponseEntity<ItemRevision> getRevisionById(
            @PathVariable Long revisionId) {

        return ResponseEntity.ok(
                revisionService.getRevisionById(revisionId)
        );
    }

    // UPDATE REVISION
    @PutMapping("/revisions/{revisionId}")
    public ResponseEntity<ItemRevision> updateRevision(
            @PathVariable Long revisionId,
            @RequestBody ItemRevision revision) {

        return ResponseEntity.ok(
                revisionService.updateRevision(
                        revisionId,
                        revision
                )
        );
    }
}