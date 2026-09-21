package com.plm.plm_ai.document.service;

import com.plm.plm_ai.document.DocumentRevisionLink;
import com.plm.plm_ai.document.DocumentVersion;
import com.plm.plm_ai.document.repository.DocumentRevisionLinkRepository;
import com.plm.plm_ai.document.repository.DocumentVersionRepository;
import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.repository.ItemRevisionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentRevisionLinkService {

    private final DocumentRevisionLinkRepository linkRepository;

    private final DocumentVersionRepository versionRepository;

    private final ItemRevisionRepository revisionRepository;

    public DocumentRevisionLinkService(
            DocumentRevisionLinkRepository linkRepository,
            DocumentVersionRepository versionRepository,
            ItemRevisionRepository revisionRepository) {

        this.linkRepository =
                linkRepository;

        this.versionRepository =
                versionRepository;

        this.revisionRepository =
                revisionRepository;
    }

    @Transactional
    public DocumentRevisionLink linkDocument(
            Long versionId,
            Long revisionId) {

        DocumentVersion version =
                versionRepository
                        .findById(versionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document version not found"));

        ItemRevision revision =
                revisionRepository
                        .findById(revisionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Item revision not found"));

        if (linkRepository
                .existsByDocumentVersionIdAndItemRevisionId(
                        versionId,
                        revisionId)) {

            throw new RuntimeException(
                    "Document version is already linked "
                            + "to this revision");
        }

        DocumentRevisionLink link =
                new DocumentRevisionLink();

        link.setDocumentVersion(version);
        link.setItemRevision(revision);

        return linkRepository.save(link);
    }

    public List<DocumentRevisionLink>
    getDocumentsForRevision(
            Long revisionId) {

        return linkRepository
                .findByItemRevisionId(revisionId);
    }

    public List<DocumentRevisionLink>
    getRevisionsForDocumentVersion(
            Long versionId) {

        return linkRepository
                .findByDocumentVersionId(versionId);
    }

    @Transactional
    public void unlinkDocument(
            Long linkId) {

        if (!linkRepository.existsById(linkId)) {

            throw new RuntimeException(
                    "Document revision link not found");
        }

        linkRepository.deleteById(linkId);
    }
}