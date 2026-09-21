package com.plm.plm_ai.document.repository;

import com.plm.plm_ai.document.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentVersionRepository
        extends JpaRepository<DocumentVersion, Long> {

    List<DocumentVersion> findByDocumentId(
            Long documentId);

    Optional<DocumentVersion>
    findTopByDocumentIdOrderByIdDesc(
            Long documentId);

    boolean existsByDocumentIdAndVersionCode(
            Long documentId,
            String versionCode);
}