package com.plm.plm_ai.change.controller;

import com.plm.plm_ai.change.ECOImplementation;
import com.plm.plm_ai.change.service.ECOImplementationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/eco")
public class ECOImplementationController {

    private final ECOImplementationService service;

    public ECOImplementationController(
            ECOImplementationService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('CHANGE_MANAGER')")
    @PostMapping("/{ecoId}/implement/{revisionId}")
    public ResponseEntity<ECOImplementation> implementRevision(
            @PathVariable Long ecoId,
            @PathVariable Long revisionId,
            @RequestParam String description) {

        return ResponseEntity.status(201).body(
                service.implementRevisionChange(
                        ecoId,
                        revisionId,
                        description
                )
        );
    }

    @GetMapping("/{ecoId}/implementations")
    public ResponseEntity<List<ECOImplementation>>
    getImplementations(
            @PathVariable Long ecoId) {

        return ResponseEntity.ok(
                service.getImplementations(ecoId)
        );
    }
}