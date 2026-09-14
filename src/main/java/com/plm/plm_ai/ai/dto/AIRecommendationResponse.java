package com.plm.plm_ai.ai.dto;

import java.util.List;

public class AIRecommendationResponse {

    private Long ecrId;
    private String changeNumber;
    private String title;
    private String riskLevel;

    private List<AIRecommendation> recommendations;

    public AIRecommendationResponse() {
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

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<AIRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(
            List<AIRecommendation> recommendations) {

        this.recommendations = recommendations;
    }
}