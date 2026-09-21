package com.plm.plm_ai.ai.rag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai/rag")
public class DocumentIndexController {

    private final VectorStoreService vectorStoreService;

    public DocumentIndexController(
            VectorStoreService vectorStoreService) {

        this.vectorStoreService =
                vectorStoreService;
    }

    @PreAuthorize(
            "hasAnyRole('ENGINEER', 'CHANGE_MANAGER')"
    )
    @PostMapping("/index/{documentVersionId}")
    public ResponseEntity<Map<String, Object>>
    indexDocument(
            @PathVariable Long documentVersionId) {

        int chunks =
                vectorStoreService
                        .indexDocumentVersion(
                                documentVersionId
                        );

        return ResponseEntity.ok(
                Map.of(
                        "documentVersionId",
                        documentVersionId,
                        "chunksIndexed",
                        chunks,
                        "status",
                        "INDEXED"
                )
        );
    }
}