package com.plm.plm_ai.change.controller;

import com.plm.plm_ai.change.ECRAffectedRevision;
import com.plm.plm_ai.change.service.ECRAffectedRevisionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ecr")
public class ECRAffectedRevisionController {

    private final ECRAffectedRevisionService service;

    public ECRAffectedRevisionController(
            ECRAffectedRevisionService service) {
        this.service = service;
    }

    // ============================================================
    // ADD AFFECTED REVISION
    // ============================================================

    @PostMapping("/{ecrId}/affected-revisions/{revisionId}")
    public ResponseEntity<ECRAffectedRevision> addAffectedRevision(
            @PathVariable Long ecrId,
            @PathVariable Long revisionId,
            @RequestParam String impactDescription) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                service.addAffectedRevision(
                        ecrId,
                        revisionId,
                        impactDescription
                )
        );
    }

    // ============================================================
    // GET AFFECTED REVISIONS
    // ============================================================

    @GetMapping("/{ecrId}/affected-revisions")
    public ResponseEntity<List<ECRAffectedRevision>> getAffectedRevisions(
            @PathVariable Long ecrId) {

        return ResponseEntity.ok(
                service.getAffectedRevisions(ecrId)
        );
    }

    // ============================================================
    // REMOVE AFFECTED REVISION
    // ============================================================

    @DeleteMapping("/affected-revisions/{affectedRevisionId}")
    public ResponseEntity<Void> removeAffectedRevision(
            @PathVariable Long affectedRevisionId) {

        service.removeAffectedRevision(affectedRevisionId);

        return ResponseEntity.noContent().build();
    }
}