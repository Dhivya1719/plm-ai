package com.plm.plm_ai.change;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ecr_approvals")
public class ECRApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ecr_id", nullable = false)
    private EngineeringChangeRequest ecr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ECRApprovalDecision decision;

    @Column(nullable = false)
    private String reviewedBy;

    @Column(length = 2000)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime reviewedAt;

    public ECRApproval() {
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

    public ECRApprovalDecision getDecision() {
        return decision;
    }

    public void setDecision(ECRApprovalDecision decision) {
        this.decision = decision;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    @PrePersist
    protected void onCreate() {
        reviewedAt = LocalDateTime.now();
    }
}