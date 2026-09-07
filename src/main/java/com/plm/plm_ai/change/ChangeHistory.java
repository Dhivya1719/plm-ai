package com.plm.plm_ai.change;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "change_history")
public class ChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private String action;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String performedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ecr_id")
    private EngineeringChangeRequest ecr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eco_id")
    private EngineeringChangeOrder eco;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    public ChangeHistory() {
    }

    public Long getId() {
        return id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public EngineeringChangeRequest getEcr() {
        return ecr;
    }

    public void setEcr(EngineeringChangeRequest ecr) {
        this.ecr = ecr;
    }

    public EngineeringChangeOrder getEco() {
        return eco;
    }

    public void setEco(EngineeringChangeOrder eco) {
        this.eco = eco;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    @PrePersist
    protected void onCreate() {
        performedAt = LocalDateTime.now();
    }
}