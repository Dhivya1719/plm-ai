package com.plm.plm_ai.document.service;

import com.plm.plm_ai.document.Document;
import com.plm.plm_ai.document.repository.DocumentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(
            DocumentRepository documentRepository) {

        this.documentRepository =
                documentRepository;
    }

    @Transactional
    public Document createDocument(
            Document document) {

        if (document.getName() == null
                || document.getName().isBlank()) {

            throw new RuntimeException(
                    "Document name is required");
        }

        if (document.getDocumentType() == null
                || document.getDocumentType().isBlank()) {

            throw new RuntimeException(
                    "Document type is required");
        }

        if (document.getCreatedBy() == null
                || document.getCreatedBy().isBlank()) {

            throw new RuntimeException(
                    "Created by is required");
        }

        document.setDocumentNumber(
                generateDocumentNumber()
        );

        return documentRepository.save(document);
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document getDocumentById(
            Long documentId) {

        return documentRepository
                .findById(documentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Document not found: "
                                        + documentId));
    }

    @Transactional
    public Document updateDocument(
            Long documentId,
            Document request) {

        Document existing =
                getDocumentById(documentId);

        if (request.getName() != null
                && !request.getName().isBlank()) {

            existing.setName(
                    request.getName()
            );
        }

        if (request.getDescription() != null) {

            existing.setDescription(
                    request.getDescription()
            );
        }

        if (request.getDocumentType() != null
                && !request.getDocumentType().isBlank()) {

            existing.setDocumentType(
                    request.getDocumentType()
            );
        }

        return documentRepository.save(existing);
    }

    public List<Document> searchDocuments(
            String name) {

        return documentRepository
                .findByNameContainingIgnoreCase(name);
    }

    private String generateDocumentNumber() {

        return documentRepository
                .findTopByOrderByIdDesc()
                .map(document -> {

                    String number =
                            document.getDocumentNumber();

                    String numericPart =
                            number.substring(
                                    number.lastIndexOf("-") + 1
                            );

                    long next =
                            Long.parseLong(numericPart) + 1;

                    return String.format(
                            "DOC-%05d",
                            next
                    );
                })
                .orElse("DOC-00001");
    }
}