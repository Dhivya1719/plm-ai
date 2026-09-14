package com.plm.plm_ai.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.plm.plm_ai.ai.dto.AIRecommendation;
import com.plm.plm_ai.ai.dto.AIRecommendationResponse;
import com.plm.plm_ai.ai.dto.ChangeImpactResponse;
import com.plm.plm_ai.ai.dto.PLMContextResponse;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AIRecommendationService {

    private final PLMContextBuilderService contextBuilderService;

    private final ChangeImpactAnalyzerService
            changeImpactAnalyzerService;

    private final AIRecommendationPromptBuilderService
            promptBuilderService;

    private final LLMService llmService;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public AIRecommendationService(
            PLMContextBuilderService contextBuilderService,
            ChangeImpactAnalyzerService changeImpactAnalyzerService,
            AIRecommendationPromptBuilderService promptBuilderService,
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

    public AIRecommendationResponse generateRecommendations(
            Long ecrId) {

        /*
         * Build PLM context
         */
        PLMContextResponse context =
                contextBuilderService.buildContext(ecrId);

        /*
         * Run deterministic impact analysis
         */
        ChangeImpactResponse impact =
                changeImpactAnalyzerService.analyze(ecrId);

        /*
         * Build grounded AI prompt
         */
        String prompt =
                promptBuilderService.buildPrompt(
                        context,
                        impact
                );

        /*
         * Ask configured LLM provider
         */
        String aiResponse =
                llmService.generateResponse(prompt);

        /*
         * Parse AI JSON
         */
        List<AIRecommendation> recommendations =
                parseRecommendations(aiResponse);

        /*
         * Build API response
         */
        AIRecommendationResponse response =
                new AIRecommendationResponse();

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
                impact.getRiskLevel()
        );

        response.setRecommendations(
                recommendations
        );

        return response;
    }

    private List<AIRecommendation> parseRecommendations(
            String aiResponse) {

        try {

            String json =
                    cleanJsonResponse(aiResponse);

            JsonNode root =
                    objectMapper.readTree(json);

            JsonNode recommendationsNode =
                    root.get("recommendations");

            if (recommendationsNode == null
                    || !recommendationsNode.isArray()) {

                throw new RuntimeException(
                        "AI response does not contain a valid recommendations array."
                );
            }

            List<AIRecommendation> recommendations =
                    new ArrayList<>();

            for (JsonNode node : recommendationsNode) {

                AIRecommendation recommendation =
                        new AIRecommendation();

                recommendation.setPriority(
                        safeText(
                                node,
                                "priority"
                        )
                );

                recommendation.setCategory(
                        safeText(
                                node,
                                "category"
                        )
                );

                recommendation.setAction(
                        safeText(
                                node,
                                "action"
                        )
                );

                recommendation.setReason(
                        safeText(
                                node,
                                "reason"
                        )
                );

                validateRecommendation(
                        recommendation
                );

                recommendations.add(
                        recommendation
                );
            }

            if (recommendations.isEmpty()) {

                throw new RuntimeException(
                        "AI returned no recommendations."
                );
            }

            return recommendations;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse AI recommendations: "
                            + e.getMessage(),
                    e
            );
        }
    }

    private String cleanJsonResponse(
            String response) {

        if (response == null
                || response.isBlank()) {

            throw new RuntimeException(
                    "AI returned an empty response."
            );
        }

        String cleaned =
                response.trim();

        /*
         * Remove markdown JSON fences if the model
         * ignores the prompt and adds them.
         */
        if (cleaned.startsWith("```json")) {

            cleaned =
                    cleaned.substring(7);

        } else if (cleaned.startsWith("```")) {

            cleaned =
                    cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {

            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 3
                    );
        }

        cleaned =
                cleaned.trim();

        /*
         * If the model added explanatory text around
         * the JSON, extract the JSON object.
         */
        int firstBrace =
                cleaned.indexOf("{");

        int lastBrace =
                cleaned.lastIndexOf("}");

        if (firstBrace >= 0
                && lastBrace > firstBrace) {

            cleaned =
                    cleaned.substring(
                            firstBrace,
                            lastBrace + 1
                    );
        }

        return cleaned;
    }

    private String safeText(
            JsonNode node,
            String field) {

        JsonNode value =
                node.get(field);

        if (value == null
                || value.isNull()) {

            return "";
        }

        return value.asText().trim();
    }

    private void validateRecommendation(
            AIRecommendation recommendation) {

        if (recommendation.getPriority() == null
                || recommendation.getPriority().isBlank()) {

            throw new RuntimeException(
                    "AI recommendation priority is missing."
            );
        }

        if (recommendation.getCategory() == null
                || recommendation.getCategory().isBlank()) {

            throw new RuntimeException(
                    "AI recommendation category is missing."
            );
        }

        if (recommendation.getAction() == null
                || recommendation.getAction().isBlank()) {

            throw new RuntimeException(
                    "AI recommendation action is missing."
            );
        }

        if (recommendation.getReason() == null
                || recommendation.getReason().isBlank()) {

            throw new RuntimeException(
                    "AI recommendation reason is missing."
            );
        }
    }
}