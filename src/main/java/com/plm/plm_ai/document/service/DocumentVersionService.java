package com.plm.plm_ai.document.service;

import com.plm.plm_ai.document.Document;
import com.plm.plm_ai.document.DocumentStatus;
import com.plm.plm_ai.document.DocumentVersion;
import com.plm.plm_ai.document.repository.DocumentRepository;
import com.plm.plm_ai.document.repository.DocumentVersionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentVersionService {

    private final DocumentRepository documentRepository;

    private final DocumentVersionRepository versionRepository;

    public DocumentVersionService(
            DocumentRepository documentRepository,
            DocumentVersionRepository versionRepository) {

        this.documentRepository =
                documentRepository;

        this.versionRepository =
                versionRepository;
    }

    @Transactional
    public DocumentVersion createVersion(
            Long documentId,
            DocumentVersion request) {

        Document document =
                documentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document not found: "
                                                + documentId));

        String nextVersion =
                generateNextVersion(documentId);

        DocumentVersion version =
                new DocumentVersion();

        version.setDocument(document);
        version.setVersionCode(nextVersion);
        version.setStatus(DocumentStatus.DRAFT);
        version.setFileName(
                request.getFileName());
        version.setContent(
                request.getContent());

        return versionRepository.save(version);
    }

    public List<DocumentVersion>
    getVersions(Long documentId) {

        return versionRepository
                .findByDocumentId(documentId);
    }

    public DocumentVersion
    getVersionById(Long versionId) {

        return versionRepository
                .findById(versionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Document version not found: "
                                        + versionId));
    }

    @Transactional
    public DocumentVersion updateVersion(
            Long versionId,
            DocumentVersion request) {

        DocumentVersion existing =
                getVersionById(versionId);

        if (existing.getStatus()
                == DocumentStatus.RELEASED) {

            throw new RuntimeException(
                    "Released document versions are immutable. "
                            + "Create a new version instead.");
        }

        if (request.getFileName() != null) {
            existing.setFileName(
                    request.getFileName());
        }

        if (request.getContent() != null) {
            existing.setContent(
                    request.getContent());
        }

        return versionRepository.save(existing);
    }

    @Transactional
    public DocumentVersion updateStatus(
            Long versionId,
            DocumentStatus newStatus) {

        DocumentVersion version =
                getVersionById(versionId);

        DocumentStatus currentStatus =
                version.getStatus();

        if (!isValidTransition(
                currentStatus,
                newStatus)) {

            throw new RuntimeException(
                    "Invalid document lifecycle transition: "
                            + currentStatus
                            + " -> "
                            + newStatus);
        }

        version.setStatus(newStatus);

        return versionRepository.save(version);
    }

    private boolean isValidTransition(
            DocumentStatus current,
            DocumentStatus next) {

        if (current == DocumentStatus.DRAFT
                && next == DocumentStatus.IN_REVIEW) {

            return true;
        }

        if (current == DocumentStatus.IN_REVIEW
                && next == DocumentStatus.RELEASED) {

            return true;
        }

        if (current == DocumentStatus.RELEASED
                && next == DocumentStatus.OBSOLETE) {

            return true;
        }

        if (current == DocumentStatus.DRAFT
                && next == DocumentStatus.OBSOLETE) {

            return true;
        }

        return false;
    }

    private String generateNextVersion(
            Long documentId) {

        return versionRepository
                .findTopByDocumentIdOrderByIdDesc(
                        documentId)
                .map(existing -> {

                    String code =
                            existing.getVersionCode();

                    char last =
                            code.charAt(
                                    code.length() - 1
                            );

                    char next =
                            (char) (last + 1);

                    return String.valueOf(next);
                })
                .orElse("A");
    }
}