package com.plm.plm_ai.document.repository;

import com.plm.plm_ai.document.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository
        extends JpaRepository<Document, Long> {

    Optional<Document> findTopByOrderByIdDesc();

    Optional<Document> findByDocumentNumber(
            String documentNumber);

    List<Document> findByNameContainingIgnoreCase(
            String name);
}