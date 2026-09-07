package com.plm.plm_ai.change;

import com.plm.plm_ai.item.ItemRevision;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "eco_implementations",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "eco_id",
                                "source_revision_id"
                        }
                )
        }
)
public class ECOImplementation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eco_id", nullable = false)
    private EngineeringChangeOrder eco;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_revision_id", nullable = false)
    private ItemRevision sourceRevision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_revision_id")
    private ItemRevision newRevision;

    @Column(length = 2000)
    private String implementationDescription;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ECOImplementation() {}

    public Long getId() {
        return id;
    }

    public EngineeringChangeOrder getEco() {
        return eco;
    }

    public void setEco(EngineeringChangeOrder eco) {
        this.eco = eco;
    }

    public ItemRevision getSourceRevision() {
        return sourceRevision;
    }

    public void setSourceRevision(ItemRevision sourceRevision) {
        this.sourceRevision = sourceRevision;
    }

    public ItemRevision getNewRevision() {
        return newRevision;
    }

    public void setNewRevision(ItemRevision newRevision) {
        this.newRevision = newRevision;
    }

    public String getImplementationDescription() {
        return implementationDescription;
    }

    public void setImplementationDescription(
            String implementationDescription) {
        this.implementationDescription =
                implementationDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}