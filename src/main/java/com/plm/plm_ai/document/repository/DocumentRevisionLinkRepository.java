package com.plm.plm_ai.document.repository;

import com.plm.plm_ai.document.DocumentRevisionLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRevisionLinkRepository
        extends JpaRepository<DocumentRevisionLink, Long> {

    List<DocumentRevisionLink>
    findByItemRevisionId(Long itemRevisionId);

    List<DocumentRevisionLink>
    findByDocumentVersionId(Long documentVersionId);

    boolean existsByDocumentVersionIdAndItemRevisionId(
            Long documentVersionId,
            Long itemRevisionId);
}