package com.plm.plm_ai.item;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(
        name = "item_revisions",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"item_id", "revision_code"}
                )
        }
)
public class ItemRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "revision_code", nullable = false)
    private String revisionCode;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LifecycleStatus status = LifecycleStatus.IN_WORK;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public ItemRevision() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getRevisionCode() {
        return revisionCode;
    }

    public void setRevisionCode(String revisionCode) {
        this.revisionCode = revisionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LifecycleStatus getStatus() {
        return status;
    }

    public void setStatus(LifecycleStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}