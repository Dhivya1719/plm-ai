package com.plm.plm_ai.document;

import com.plm.plm_ai.item.ItemRevision;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "document_revision_links",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "document_version_id",
                                "item_revision_id"
                        }
                )
        }
)
public class DocumentRevisionLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "document_version_id",
            nullable = false
    )
    private DocumentVersion documentVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "item_revision_id",
            nullable = false
    )
    private ItemRevision itemRevision;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public DocumentRevisionLink() {
    }

    public Long getId() {
        return id;
    }

    public DocumentVersion getDocumentVersion() {
        return documentVersion;
    }

    public void setDocumentVersion(
            DocumentVersion documentVersion) {
        this.documentVersion = documentVersion;
    }

    public ItemRevision getItemRevision() {
        return itemRevision;
    }

    public void setItemRevision(
            ItemRevision itemRevision) {
        this.itemRevision = itemRevision;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}