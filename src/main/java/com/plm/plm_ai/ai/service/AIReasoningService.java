package com.plm.plm_ai.ai.service;

import com.plm.plm_ai.ai.dto.AIReasoningResponse;
import com.plm.plm_ai.ai.dto.ChangeImpactResponse;
import com.plm.plm_ai.ai.dto.PLMContextResponse;

import org.springframework.stereotype.Service;

@Service
public class AIReasoningService {

    private final PLMContextBuilderService contextBuilderService;

    private final ChangeImpactAnalyzerService
            changeImpactAnalyzerService;

    private final AIReasoningPromptBuilderService
            promptBuilderService;

    private final LLMService llmService;

    public AIReasoningService(
            PLMContextBuilderService contextBuilderService,
            ChangeImpactAnalyzerService changeImpactAnalyzerService,
            AIReasoningPromptBuilderService promptBuilderService,
            LLMService llmService) {

        this.contextBuilderService =
                contextBuilderService;

        this.changeImpactAnalyzerService =
                changeImpactAnalyzerService;

        this.promptBuilderService =
                promptBuilderService;

        this.llmService =
                llmService;
    }

    public AIReasoningResponse analyze(Long ecrId) {

        PLMContextResponse context =
                contextBuilderService.buildContext(ecrId);

        ChangeImpactResponse deterministicImpact =
                changeImpactAnalyzerService.analyze(ecrId);

        String prompt =
                promptBuilderService.buildPrompt(
                        context,
                        deterministicImpact
                );

        String aiReasoning =
                llmService.generateResponse(prompt);

        AIReasoningResponse response =
                new AIReasoningResponse();

        response.setEcrId(
                context.getEcrId()
        );

        response.setChangeNumber(
                context.getChangeNumber()
        );

        response.setTitle(
                context.getTitle()
        );

        response.setRiskLevel(
                deterministicImpact.getRiskLevel()
        );

        response.setAiReasoning(
                aiReasoning
        );

        return response;
    }
}