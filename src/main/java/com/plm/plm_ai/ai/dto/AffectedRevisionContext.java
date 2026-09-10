package com.plm.plm_ai.ai.dto;

import java.util.List;

public class AffectedRevisionContext {

    private Long revisionId;
    private String revisionCode;
    private String description;
    private String lifecycleStatus;
    private String impactDescription;

    private List<BOMComponentContext> bomComponents;
    private List<WhereUsedContext> whereUsedBy;

    public AffectedRevisionContext() {
    }

    public Long getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(Long revisionId) {
        this.revisionId = revisionId;
    }

    public String getRevisionCode() {
        return revisionCode;
    }

    public void setRevisionCode(String revisionCode) {
        this.revisionCode = revisionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLifecycleStatus() {
        return lifecycleStatus;
    }

    public void setLifecycleStatus(String lifecycleStatus) {
        this.lifecycleStatus = lifecycleStatus;
    }

    public String getImpactDescription() {
        return impactDescription;
    }

    public void setImpactDescription(String impactDescription) {
        this.impactDescription = impactDescription;
    }

    public List<BOMComponentContext> getBomComponents() {
        return bomComponents;
    }

    public void setBomComponents(
            List<BOMComponentContext> bomComponents) {
        this.bomComponents = bomComponents;
    }

    public List<WhereUsedContext> getWhereUsedBy() {
        return whereUsedBy;
    }

    public void setWhereUsedBy(
            List<WhereUsedContext> whereUsedBy) {
        this.whereUsedBy = whereUsedBy;
    }
}