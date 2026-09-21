package com.plm.plm_ai.document.controller;

import com.plm.plm_ai.document.DocumentRevisionLink;
import com.plm.plm_ai.document.service.DocumentRevisionLinkService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DocumentRevisionLinkController {

    private final DocumentRevisionLinkService linkService;

    public DocumentRevisionLinkController(
            DocumentRevisionLinkService linkService) {

        this.linkService =
                linkService;
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'CHANGE_MANAGER')"
    )
    @PostMapping(
            "/document-versions/{versionId}/revisions/{revisionId}"
    )
    public ResponseEntity<DocumentRevisionLink>
    linkDocument(
            @PathVariable Long versionId,
            @PathVariable Long revisionId) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        linkService.linkDocument(
                                versionId,
                                revisionId
                        )
                );
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @GetMapping(
            "/revisions/{revisionId}/documents"
    )
    public ResponseEntity<List<DocumentRevisionLink>>
    getDocumentsForRevision(
            @PathVariable Long revisionId) {

        return ResponseEntity.ok(
                linkService
                        .getDocumentsForRevision(
                                revisionId
                        )
        );
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @GetMapping(
            "/document-versions/{versionId}/revisions"
    )
    public ResponseEntity<List<DocumentRevisionLink>>
    getRevisionsForDocument(
            @PathVariable Long versionId) {

        return ResponseEntity.ok(
                linkService
                        .getRevisionsForDocumentVersion(
                                versionId
                        )
        );
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'CHANGE_MANAGER')"
    )
    @DeleteMapping(
            "/document-revision-links/{linkId}"
    )
    public ResponseEntity<Void>
    unlinkDocument(
            @PathVariable Long linkId) {

        linkService.unlinkDocument(linkId);

        return ResponseEntity.noContent().build();
    }
}