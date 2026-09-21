package com.plm.plm_ai.ai.rag;

import com.plm.plm_ai.ai.service.LLMService;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PLMRAGService {

    private final VectorStoreService vectorStoreService;

    private final LLMService llmService;

    public PLMRAGService(
            VectorStoreService vectorStoreService,
            LLMService llmService) {

        this.vectorStoreService =
                vectorStoreService;

        this.llmService =
                llmService;
    }

    public PLMRAGResponse answer(
            String question) {

        List<VectorStoreService.SearchResult>
                results =
                vectorStoreService.search(
                        question,
                        5
                );

        if (results.isEmpty()) {

            throw new RuntimeException(
                    "No relevant PLM documents were found."
            );
        }

        StringBuilder context =
                new StringBuilder();

        for (VectorStoreService.SearchResult
                result : results) {

            var chunk =
                    result.chunk();

            var version =
                    chunk.getDocumentVersion();

            var document =
                    version.getDocument();

            context.append(
                    "DOCUMENT: "
                            + document.getDocumentNumber()
                            + "\n"
            );

            context.append(
                    "NAME: "
                            + document.getName()
                            + "\n"
            );

            context.append(
                    "VERSION: "
                            + version.getVersionCode()
                            + "\n"
            );

            context.append(
                    "CONTENT:\n"
                            + chunk.getContent()
                            + "\n\n"
            );
        }

        String prompt =
                buildPrompt(
                        question,
                        context.toString()
                );

        String answer =
                llmService.generateResponse(
                        prompt
                );

        PLMRAGResponse response =
                new PLMRAGResponse();

        response.setQuestion(question);
        response.setAnswer(answer);

        List<PLMRAGResponse.Source>
                sources =
                new ArrayList<>();

        for (VectorStoreService.SearchResult
                result : results) {

            var chunk =
                    result.chunk();

            var version =
                    chunk.getDocumentVersion();

            var document =
                    version.getDocument();

            PLMRAGResponse.Source source =
                    new PLMRAGResponse.Source();

            source.setDocumentVersionId(
                    version.getId()
            );

            source.setDocumentNumber(
                    document.getDocumentNumber()
            );

            source.setDocumentName(
                    document.getName()
            );

            source.setVersionCode(
                    version.getVersionCode()
            );

            source.setContent(
                    chunk.getContent()
            );

            source.setSimilarity(
                    result.score()
            );

            sources.add(source);
        }

        response.setSources(sources);

        return response;
    }

    private String buildPrompt(
            String question,
            String context) {

        return """
                You are a PLM engineering knowledge assistant.

                Answer the user's question using ONLY the supplied
                document context.

                Do not invent engineering facts.

                If the context does not contain enough information,
                explicitly say that the available PLM documents
                do not contain enough information.

                Keep the answer concise and factual.

                ============================
                RETRIEVED PLM DOCUMENT CONTEXT
                ============================

                %s

                ============================
                USER QUESTION
                ============================

                %s

                ============================
                INSTRUCTIONS
                ============================

                Answer using the retrieved PLM document context.
                Do not use outside knowledge.
                """.formatted(
                context,
                question
        );
    }
}