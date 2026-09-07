package com.plm.plm_ai.change;

import com.plm.plm_ai.item.BOMLine;
import com.plm.plm_ai.item.ItemRevision;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "eco_bom_changes")
public class ECOBOMChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eco_id", nullable = false)
    private EngineeringChangeOrder eco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ECOBOMChangeType changeType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parent_revision_id", nullable = false)
    private ItemRevision parentRevision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_child_revision_id")
    private ItemRevision oldChildRevision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_child_revision_id")
    private ItemRevision newChildRevision;

    @Column(nullable = false)
    private Integer oldQuantity;

    @Column(nullable = false)
    private Integer newQuantity;

    @Column(length = 1000)
    private String reason;

    @Column(nullable = false)
    private String changedBy;

    public ECOBOMChange() {
    }

    public Long getId() {
        return id;
    }

    public EngineeringChangeOrder getEco() {
        return eco;
    }

    public void setEco(EngineeringChangeOrder eco) {
        this.eco = eco;
    }

    public ECOBOMChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ECOBOMChangeType changeType) {
        this.changeType = changeType;
    }

    public ItemRevision getParentRevision() {
        return parentRevision;
    }

    public void setParentRevision(ItemRevision parentRevision) {
        this.parentRevision = parentRevision;
    }

    public ItemRevision getOldChildRevision() {
        return oldChildRevision;
    }

    public void setOldChildRevision(ItemRevision oldChildRevision) {
        this.oldChildRevision = oldChildRevision;
    }

    public ItemRevision getNewChildRevision() {
        return newChildRevision;
    }

    public void setNewChildRevision(ItemRevision newChildRevision) {
        this.newChildRevision = newChildRevision;
    }

    public Integer getOldQuantity() {
        return oldQuantity;
    }

    public void setOldQuantity(Integer oldQuantity) {
        this.oldQuantity = oldQuantity;
    }

    public Integer getNewQuantity() {
        return newQuantity;
    }

    public void setNewQuantity(Integer newQuantity) {
        this.newQuantity = newQuantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        changedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}