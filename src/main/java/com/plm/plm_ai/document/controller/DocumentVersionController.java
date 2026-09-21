package com.plm.plm_ai.document.controller;

import com.plm.plm_ai.document.DocumentStatus;
import com.plm.plm_ai.document.DocumentVersion;
import com.plm.plm_ai.document.service.DocumentVersionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentVersionController {

    private final DocumentVersionService versionService;

    public DocumentVersionController(
            DocumentVersionService versionService) {

        this.versionService =
                versionService;
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'CHANGE_MANAGER')"
    )
    @PostMapping("/{documentId}/versions")
    public ResponseEntity<DocumentVersion>
    createVersion(
            @PathVariable Long documentId,
            @RequestBody DocumentVersion version) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        versionService.createVersion(
                                documentId,
                                version
                        )
                );
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @GetMapping("/{documentId}/versions")
    public ResponseEntity<List<DocumentVersion>>
    getVersions(
            @PathVariable Long documentId) {

        return ResponseEntity.ok(
                versionService
                        .getVersions(documentId)
        );
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @GetMapping("/versions/{versionId}")
    public ResponseEntity<DocumentVersion>
    getVersion(
            @PathVariable Long versionId) {

        return ResponseEntity.ok(
                versionService
                        .getVersionById(versionId)
        );
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'CHANGE_MANAGER')"
    )
    @PutMapping("/versions/{versionId}")
    public ResponseEntity<DocumentVersion>
    updateVersion(
            @PathVariable Long versionId,
            @RequestBody DocumentVersion version) {

        return ResponseEntity.ok(
                versionService.updateVersion(
                        versionId,
                        version
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @PutMapping("/versions/{versionId}/status")
    public ResponseEntity<DocumentVersion>
    updateStatus(
            @PathVariable Long versionId,
            @RequestParam DocumentStatus status) {

        return ResponseEntity.ok(
                versionService.updateStatus(
                        versionId,
                        status
                )
        );
    }
}