package com.plm.plm_ai.ai.assistant;

import com.plm.plm_ai.ai.dto.ChangeImpactResponse;
import com.plm.plm_ai.ai.rag.VectorStoreService;
import com.plm.plm_ai.ai.service.ChangeImpactAnalyzerService;
import com.plm.plm_ai.ai.service.LLMService;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PLMAssistantService {

    private final PLMQuestionRouter questionRouter;

    private final PLMQueryService plmQueryService;

    private final VectorStoreService vectorStoreService;

    private final ChangeImpactAnalyzerService
            changeImpactAnalyzerService;

    private final LLMService llmService;

    public PLMAssistantService(
            PLMQuestionRouter questionRouter,
            PLMQueryService plmQueryService,
            VectorStoreService vectorStoreService,
            ChangeImpactAnalyzerService
                    changeImpactAnalyzerService,
            LLMService llmService) {

        this.questionRouter =
                questionRouter;

        this.plmQueryService =
                plmQueryService;

        this.vectorStoreService =
                vectorStoreService;

        this.changeImpactAnalyzerService =
                changeImpactAnalyzerService;

        this.llmService =
                llmService;
    }

    public PLMAssistantResponse ask(
            String question) {

        if (question == null
                || question.isBlank()) {

            throw new RuntimeException(
                    "Question cannot be empty."
            );
        }

        PLMQuestionRouter.QuestionType type =
                questionRouter.route(question);

        String context;

        List<PLMAssistantResponse.Source>
                sources =
                new ArrayList<>();

        switch (type) {

            case DOCUMENT_SEARCH -> {

                List<VectorStoreService.SearchResult>
                        results =
                        vectorStoreService.search(
                                question,
                                5
                        );

                context =
                        buildDocumentContext(
                                results
                        );

                for (VectorStoreService.SearchResult
                        result : results) {

                    var version =
                            result.chunk()
                                    .getDocumentVersion();

                    var document =
                            version.getDocument();

                    sources.add(
                            new PLMAssistantResponse.Source(
                                    "DOCUMENT",
                                    document
                                            .getDocumentNumber(),
                                    document
                                            .getName()
                                            + " Version "
                                            + version
                                            .getVersionCode()
                            )
                    );
                }
            }

            case CHANGE_IMPACT -> {

                Long ecrId =
                        extractEcrId(question);

                ChangeImpactResponse impact =
                        changeImpactAnalyzerService
                                .analyze(ecrId);

                context =
                        buildImpactContext(
                                impact
                        );

                sources.add(
                        new PLMAssistantResponse.Source(
                                "ECR",
                                impact.getChangeNumber(),
                                impact.getTitle()
                        )
                );
            }

            case PLM_DATA -> {

                String identifier =
                        extractIdentifier(question);

                context =
                        findPLMData(identifier);

                sources.add(
                        new PLMAssistantResponse.Source(
                                "PLM",
                                identifier,
                                "Structured PLM data"
                        )
                );
            }

            default -> {

                context =
                        """
                        No specific PLM source was identified.

                        The assistant should explain that it can answer
                        questions about PLM items, revisions, BOMs,
                        engineering changes and engineering documents.
                        """;
            }
        }

        String prompt =
                buildPrompt(
                        question,
                        context
                );

        String answer =
                llmService.generateResponse(
                        prompt
                );

        PLMAssistantResponse response =
                new PLMAssistantResponse();

        response.setQuestion(question);
        response.setAnswer(answer);
        response.setSourceType(type.name());
        response.setSources(sources);

        return response;
    }

    private String buildDocumentContext(
            List<VectorStoreService.SearchResult>
                    results) {

        StringBuilder context =
                new StringBuilder();

        for (VectorStoreService.SearchResult
                result : results) {

            var version =
                    result.chunk()
                            .getDocumentVersion();

            var document =
                    version.getDocument();

            context.append(
                    "Document: "
                            + document
                            .getDocumentNumber()
                            + "\n"
            );

            context.append(
                    "Name: "
                            + document.getName()
                            + "\n"
            );

            context.append(
                    "Version: "
                            + version
                            .getVersionCode()
                            + "\n"
            );

            context.append(
                    "Content:\n"
                            + result.chunk()
                            .getContent()
                            + "\n\n"
            );
        }

        return context.toString();
    }

    private String buildImpactContext(
            ChangeImpactResponse impact) {

        StringBuilder context =
                new StringBuilder();

        context.append(
                "ECR: "
                        + impact.getChangeNumber()
                        + "\n"
        );

        context.append(
                "Title: "
                        + impact.getTitle()
                        + "\n"
        );

        context.append(
                "Risk: "
                        + impact.getRiskLevel()
                        + "\n"
        );

        context.append(
                "Risk Reason: "
                        + impact.getRiskReason()
                        + "\n"
        );

        context.append(
                "Direct Impacts:\n"
        );

        appendList(
                context,
                impact.getDirectImpacts()
        );

        context.append(
                "Indirect Impacts:\n"
        );

        appendList(
                context,
                impact.getIndirectImpacts()
        );

        context.append(
                "Affected Assemblies:\n"
        );

        appendList(
                context,
                impact.getAffectedAssemblies()
        );

        context.append(
                "Component Impacts:\n"
        );

        appendList(
                context,
                impact.getComponentImpacts()
        );

        return context.toString();
    }

    private String findPLMData(
            String identifier) {

        if (identifier.matches("\\d+")) {

            try {

                return plmQueryService
                        .findRevisionInformation(
                                Long.parseLong(
                                        identifier
                                )
                        );

            } catch (Exception ignored) {
                // Try item number below.
            }
        }

        return plmQueryService
                .findItemInformation(identifier);
    }

    private String buildPrompt(
            String question,
            String context) {

        return """
                You are an AI assistant operating inside an
                enterprise Product Lifecycle Management system.

                Answer the user's question using ONLY the supplied
                PLM context.

                Do not invent:
                - items
                - revisions
                - BOM relationships
                - quantities
                - engineering documents
                - ECRs
                - risks
                - technical specifications

                If the supplied context does not contain enough
                information, clearly state that the available
                PLM information is insufficient.

                Keep the answer concise and useful.

                ============================
                PLM CONTEXT
                ============================

                %s

                ============================
                USER QUESTION
                ============================

                %s
                """.formatted(
                context,
                question
        );
    }

    private Long extractEcrId(
            String question) {

        java.util.regex.Matcher matcher =
                java.util.regex.Pattern
                        .compile(
                                "(?i)ecr[- ]?(\\d+)"
                        )
                        .matcher(question);

        if (!matcher.find()) {

            throw new RuntimeException(
                    "Please provide an ECR number, "
                            + "for example ECR-00005."
            );
        }

        return Long.parseLong(
                matcher.group(1)
        );
    }

    private String extractIdentifier(
            String question) {

        java.util.regex.Matcher itemMatcher =
                java.util.regex.Pattern
                        .compile(
                                "(?i)\\b([A-Z]{3}-\\d{5})\\b"
                        )
                        .matcher(question);

        if (itemMatcher.find()) {

            return itemMatcher
                    .group(1)
                    .toUpperCase();
        }

        java.util.regex.Matcher idMatcher =
                java.util.regex.Pattern
                        .compile(
                                "(?i)(?:revision|id)\\s*(\\d+)"
                        )
                        .matcher(question);

        if (idMatcher.find()) {
            return idMatcher.group(1);
        }

        throw new RuntimeException(
                "Please provide a PLM item number or revision ID."
        );
    }

    private void appendList(
            StringBuilder builder,
            List<String> values) {

        if (values == null
                || values.isEmpty()) {

            builder.append("- None\n");
            return;
        }

        for (String value : values) {

            builder.append(
                    "- "
                            + value
                            + "\n"
            );
        }
    }
}