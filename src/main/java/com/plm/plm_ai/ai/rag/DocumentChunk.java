package com.plm.plm_ai.ai.rag;

import com.plm.plm_ai.document.DocumentVersion;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_chunks")
public class DocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_version_id", nullable = false)
    private DocumentVersion documentVersion;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String embedding;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public DocumentChunk() {
    }

    public Long getId() {
        return id;
    }

    public DocumentVersion getDocumentVersion() {
        return documentVersion;
    }

    public void setDocumentVersion(DocumentVersion documentVersion) {
        this.documentVersion = documentVersion;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEmbedding() {
        return embedding;
    }

    public void setEmbedding(String embedding) {
        this.embedding = embedding;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}