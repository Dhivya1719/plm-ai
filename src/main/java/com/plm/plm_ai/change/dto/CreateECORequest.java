package com.plm.plm_ai.change.dto;

public class CreateECORequest {

    private Long sourceEcrId;

    private String title;

    private String description;

    private String createdBy;

    public CreateECORequest() {
    }

    public Long getSourceEcrId() {
        return sourceEcrId;
    }

    public void setSourceEcrId(Long sourceEcrId) {
        this.sourceEcrId = sourceEcrId;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}