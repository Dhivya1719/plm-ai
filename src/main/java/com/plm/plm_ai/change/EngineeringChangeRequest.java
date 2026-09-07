package com.plm.plm_ai.change;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "engineering_change_requests")
public class EngineeringChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String changeNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 2000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ECRStatus status;

    @Column(nullable = false)
    private String requestedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public EngineeringChangeRequest() {
    }

    public Long getId() {
        return id;
    }

    public String getChangeNumber() {
        return changeNumber;
    }

    public void setChangeNumber(String changeNumber) {
        this.changeNumber = changeNumber;
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

    public String getReason() {

        return reason;
    }

    public void setReason(String reason) {

        this.reason = reason;
    }

    public ECRStatus getStatus() {

        return status;
    }

    public void setStatus(ECRStatus status) {

        this.status = status;
    }

    public String getRequestedBy() {

        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {

        this.requestedBy = requestedBy;
    }

    public LocalDateTime getCreatedAt() {

        return createdAt;
    }

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
