package com.plm.plm_ai.change;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "engineering_change_orders")
public class EngineeringChangeOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ecoNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ECOStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_ecr_id", nullable = false)
    private EngineeringChangeRequest sourceEcr;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public EngineeringChangeOrder() {
    }

    public Long getId() {
        return id;
    }

    public String getEcoNumber() {
        return ecoNumber;
    }

    public void setEcoNumber(String ecoNumber) {
        this.ecoNumber = ecoNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ECOStatus getStatus() {
        return status;
    }

    public void setStatus(ECOStatus status) {
        this.status = status;
    }

    public EngineeringChangeRequest getSourceEcr() {
        return sourceEcr;
    }

    public void setSourceEcr(EngineeringChangeRequest sourceEcr) {
        this.sourceEcr = sourceEcr;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}