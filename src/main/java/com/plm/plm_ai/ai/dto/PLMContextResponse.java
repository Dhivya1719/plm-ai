package com.plm.plm_ai.ai.dto;

import java.util.List;

public class PLMContextResponse {

    private Long ecrId;
    private String changeNumber;
    private String title;
    private String description;
    private String reason;
    private String status;
    private String requestedBy;

    private List<AffectedRevisionContext> affectedRevisions;

    public PLMContextResponse() {
    }

    public Long getEcrId() {
        return ecrId;
    }

    public void setEcrId(Long ecrId) {
        this.ecrId = ecrId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public List<AffectedRevisionContext> getAffectedRevisions() {
        return affectedRevisions;
    }

    public void setAffectedRevisions(
            List<AffectedRevisionContext> affectedRevisions) {
        this.affectedRevisions = affectedRevisions;
    }
}