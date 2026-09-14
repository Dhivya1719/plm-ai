package com.plm.plm_ai.ai.dto;

import java.util.List;

public class FinalChangeImpactReport {

    private Long ecrId;
    private String changeNumber;
    private String title;

    private String riskLevel;
    private String riskReason;

    private List<String> directImpacts;
    private List<String> indirectImpacts;
    private List<String> affectedAssemblies;

    private List<String> componentImpacts;
    private List<String> componentRecommendations;

    private List<String> recommendedActions;

    private String aiReasoning;

    public FinalChangeImpactReport() {
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

    public String getRiskReason() {
        return riskReason;
    }

    public void setRiskReason(String riskReason) {
        this.riskReason = riskReason;
    }

    public List<String> getDirectImpacts() {
        return directImpacts;
    }

    public void setDirectImpacts(List<String> directImpacts) {
        this.directImpacts = directImpacts;
    }

    public List<String> getIndirectImpacts() {
        return indirectImpacts;
    }

    public void setIndirectImpacts(List<String> indirectImpacts) {
        this.indirectImpacts = indirectImpacts;
    }

    public List<String> getAffectedAssemblies() {
        return affectedAssemblies;
    }

    public void setAffectedAssemblies(List<String> affectedAssemblies) {
        this.affectedAssemblies = affectedAssemblies;
    }

    public List<String> getComponentImpacts() {
        return componentImpacts;
    }

    public void setComponentImpacts(List<String> componentImpacts) {
        this.componentImpacts = componentImpacts;
    }

    public List<String> getComponentRecommendations() {
        return componentRecommendations;
    }

    public void setComponentRecommendations(
            List<String> componentRecommendations) {

        this.componentRecommendations =
                componentRecommendations;
    }

    public List<String> getRecommendedActions() {
        return recommendedActions;
    }

    public void setRecommendedActions(
            List<String> recommendedActions) {

        this.recommendedActions =
                recommendedActions;
    }

    public String getAiReasoning() {
        return aiReasoning;
    }

    public void setAiReasoning(String aiReasoning) {
        this.aiReasoning = aiReasoning;
    }
}