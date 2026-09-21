package com.plm.plm_ai.document.controller;

import com.plm.plm_ai.document.Document;
import com.plm.plm_ai.document.service.DocumentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(
            DocumentService documentService) {

        this.documentService =
                documentService;
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'CHANGE_MANAGER')"
    )
    @PostMapping
    public ResponseEntity<Document> createDocument(
            @RequestBody Document document) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        documentService
                                .createDocument(document)
                );
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @GetMapping
    public ResponseEntity<List<Document>>
    getAllDocuments() {

        return ResponseEntity.ok(
                documentService.getAllDocuments()
        );
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @GetMapping("/{documentId}")
    public ResponseEntity<Document>
    getDocument(
            @PathVariable Long documentId) {

        return ResponseEntity.ok(
                documentService
                        .getDocumentById(documentId)
        );
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'CHANGE_MANAGER')"
    )
    @PutMapping("/{documentId}")
    public ResponseEntity<Document>
    updateDocument(
            @PathVariable Long documentId,
            @RequestBody Document document) {

        return ResponseEntity.ok(
                documentService.updateDocument(
                        documentId,
                        document
                )
        );
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'REVIEWER', 'CHANGE_MANAGER')"
    )
    @GetMapping("/search")
    public ResponseEntity<List<Document>>
    searchDocuments(
            @RequestParam String name) {

        return ResponseEntity.ok(
                documentService
                        .searchDocuments(name)
        );
    }
}