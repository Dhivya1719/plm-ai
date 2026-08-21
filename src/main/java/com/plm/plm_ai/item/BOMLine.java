package com.plm.plm_ai.item;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "bom_lines",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "parent_revision_id",
                                "child_revision_id"
                        }
                )
        }
)
public class BOMLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_revision_id", nullable = false)
    private ItemRevision parentRevision;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "child_revision_id", nullable = false)
    private ItemRevision childRevision;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public BOMLine() {
    }

    public Long getId() {
        return id;
    }

    public ItemRevision getParentRevision() {
        return parentRevision;
    }

    public void setParentRevision(ItemRevision parentRevision) {
        this.parentRevision = parentRevision;
    }

    public ItemRevision getChildRevision() {
        return childRevision;
    }

    public void setChildRevision(ItemRevision childRevision) {
        this.childRevision = childRevision;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}