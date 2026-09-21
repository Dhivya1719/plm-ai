package com.plm.plm_ai.ai.rag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentChunkRepository
        extends JpaRepository<DocumentChunk, Long> {

    List<DocumentChunk>
    findByDocumentVersionIdOrderByChunkIndexAsc(
            Long documentVersionId);

    void deleteByDocumentVersionId(
            Long documentVersionId);
}