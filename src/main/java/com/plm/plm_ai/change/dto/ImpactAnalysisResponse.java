package com.plm.plm_ai.change.dto;

import java.util.List;

public class ImpactAnalysisResponse {

    private Long affectedRevisionId;

    private String affectedItemNumber;

    private String affectedRevisionCode;

    private List<ImpactItem> directImpacts;

    private List<ImpactItem> indirectImpacts;

    private int bomImpactCount;

    private String risk;

    public ImpactAnalysisResponse() {
    }

    public ImpactAnalysisResponse(
            Long affectedRevisionId,
            String affectedItemNumber,
            String affectedRevisionCode,
            List<ImpactItem> directImpacts,
            List<ImpactItem> indirectImpacts,
            int bomImpactCount,
            String risk) {

        this.affectedRevisionId = affectedRevisionId;
        this.affectedItemNumber = affectedItemNumber;
        this.affectedRevisionCode = affectedRevisionCode;
        this.directImpacts = directImpacts;
        this.indirectImpacts = indirectImpacts;
        this.bomImpactCount = bomImpactCount;
        this.risk = risk;
    }

    public Long getAffectedRevisionId() {
        return affectedRevisionId;
    }

    public String getAffectedItemNumber() {
        return affectedItemNumber;
    }

    public String getAffectedRevisionCode() {
        return affectedRevisionCode;
    }

    public List<ImpactItem> getDirectImpacts() {
        return directImpacts;
    }

    public List<ImpactItem> getIndirectImpacts() {
        return indirectImpacts;
    }

    public int getBomImpactCount() {
        return bomImpactCount;
    }

    public String getRisk() {
        return risk;
    }

    public static class ImpactItem {

        private Long revisionId;
        private String itemNumber;
        private String revisionCode;

        public ImpactItem(
                Long revisionId,
                String itemNumber,
                String revisionCode) {

            this.revisionId = revisionId;
            this.itemNumber = itemNumber;
            this.revisionCode = revisionCode;
        }

        public Long getRevisionId() {
            return revisionId;
        }

        public String getItemNumber() {
            return itemNumber;
        }

        public String getRevisionCode() {
            return revisionCode;
        }
    }
}