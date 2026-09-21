package com.plm.plm_ai.ai.rag;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SemanticSearchService {

    private final VectorStoreService vectorStoreService;

    public SemanticSearchService(
            VectorStoreService vectorStoreService) {

        this.vectorStoreService =
                vectorStoreService;
    }

    public SemanticSearchResponse search(
            String query,
            int topK) {

        List<VectorStoreService.SearchResult>
                rawResults =
                vectorStoreService.search(
                        query,
                        topK
                );

        List<SemanticSearchResponse.SearchResult>
                results =
                new ArrayList<>();

        for (VectorStoreService.SearchResult
                raw : rawResults) {

            var chunk =
                    raw.chunk();

            var version =
                    chunk.getDocumentVersion();

            var document =
                    version.getDocument();

            SemanticSearchResponse.SearchResult
                    result =
                    new SemanticSearchResponse
                            .SearchResult();

            result.setChunkId(
                    chunk.getId()
            );

            result.setDocumentVersionId(
                    version.getId()
            );

            result.setDocumentNumber(
                    document.getDocumentNumber()
            );

            result.setDocumentName(
                    document.getName()
            );

            result.setVersionCode(
                    version.getVersionCode()
            );

            result.setContent(
                    chunk.getContent()
            );

            result.setSimilarity(
                    raw.score()
            );

            results.add(result);
        }

        return new SemanticSearchResponse(
                query,
                results
        );
    }
}