package com.plm.plm_ai.ai.service;

import com.plm.plm_ai.ai.dto.AIReasoningResponse;
import com.plm.plm_ai.ai.dto.ChangeImpactResponse;
import com.plm.plm_ai.ai.dto.FinalChangeImpactReport;

import org.springframework.stereotype.Service;

@Service
public class FinalChangeImpactReportService {

    private final ChangeImpactAnalyzerService
            changeImpactAnalyzerService;

    private final AIReasoningService
            aiReasoningService;

    public FinalChangeImpactReportService(
            ChangeImpactAnalyzerService changeImpactAnalyzerService,
            AIReasoningService aiReasoningService) {

        this.changeImpactAnalyzerService =
                changeImpactAnalyzerService;

        this.aiReasoningService =
                aiReasoningService;
    }

    public FinalChangeImpactReport generateReport(
            Long ecrId) {

        ChangeImpactResponse deterministicImpact =
                changeImpactAnalyzerService.analyze(ecrId);

        AIReasoningResponse aiReasoning =
                aiReasoningService.analyze(ecrId);

        FinalChangeImpactReport report =
                new FinalChangeImpactReport();

        /*
         * Basic ECR information
         */
        report.setEcrId(
                deterministicImpact.getEcrId()
        );

        report.setChangeNumber(
                deterministicImpact.getChangeNumber()
        );

        report.setTitle(
                deterministicImpact.getTitle()
        );

        /*
         * Deterministic risk assessment
         */
        report.setRiskLevel(
                deterministicImpact.getRiskLevel()
        );

        report.setRiskReason(
                deterministicImpact.getRiskReason()
        );

        /*
         * Direct and indirect impact
         */
        report.setDirectImpacts(
                deterministicImpact.getDirectImpacts()
        );

        report.setIndirectImpacts(
                deterministicImpact.getIndirectImpacts()
        );

        report.setAffectedAssemblies(
                deterministicImpact.getAffectedAssemblies()
        );

        /*
         * Component-level impact
         */
        report.setComponentImpacts(
                deterministicImpact.getComponentImpacts()
        );

        report.setComponentRecommendations(
                deterministicImpact
                        .getComponentRecommendations()
        );

        /*
         * Deterministic recommendations
         */
        report.setRecommendedActions(
                deterministicImpact.getRecommendedActions()
        );

        /*
         * AI engineering reasoning
         */
        report.setAiReasoning(
                aiReasoning.getAiReasoning()
        );

        return report;
    }
}