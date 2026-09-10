package com.plm.plm_ai.change;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_records")
public class ApprovalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ecr_id", nullable = false)
    private EngineeringChangeRequest ecr;

    @Column(nullable = false)
    private String approver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ECRStatus decision;

    @Column(length = 2000)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ApprovalRecord() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public EngineeringChangeRequest getEcr() {
        return ecr;
    }

    public void setEcr(EngineeringChangeRequest ecr) {
        this.ecr = ecr;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public ECRStatus getDecision() {
        return decision;
    }

    public void setDecision(ECRStatus decision) {
        this.decision = decision;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}