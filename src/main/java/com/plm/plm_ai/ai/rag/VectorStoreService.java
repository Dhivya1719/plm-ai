package com.plm.plm_ai.ai.rag;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plm.plm_ai.document.DocumentVersion;
import com.plm.plm_ai.document.repository.DocumentVersionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class VectorStoreService {

    private final DocumentChunkRepository chunkRepository;

    private final DocumentVersionRepository versionRepository;

    private final OllamaEmbeddingService embeddingService;

    private final DocumentChunkingService chunkingService;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    public VectorStoreService(
            DocumentChunkRepository chunkRepository,
            DocumentVersionRepository versionRepository,
            OllamaEmbeddingService embeddingService,
            DocumentChunkingService chunkingService) {

        this.chunkRepository =
                chunkRepository;

        this.versionRepository =
                versionRepository;

        this.embeddingService =
                embeddingService;

        this.chunkingService =
                chunkingService;
    }

    @Transactional
    public int indexDocumentVersion(
            Long documentVersionId) {

        DocumentVersion version =
                versionRepository
                        .findById(documentVersionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Document version not found: "
                                                + documentVersionId
                                ));

        String content =
                version.getContent();

        if (content == null
                || content.isBlank()) {

            throw new RuntimeException(
                    "Document version has no content."
            );
        }

        chunkRepository
                .deleteByDocumentVersionId(
                        documentVersionId
                );

        List<String> chunks =
                chunkingService
                        .chunkText(content);

        for (int i = 0;
             i < chunks.size();
             i++) {

            String chunkText =
                    chunks.get(i);

            List<Double> embedding =
                    embeddingService
                            .generateEmbedding(
                                    chunkText
                            );

            DocumentChunk chunk =
                    new DocumentChunk();

            chunk.setDocumentVersion(version);
            chunk.setChunkIndex(i);
            chunk.setContent(chunkText);

            try {

                chunk.setEmbedding(
                        objectMapper.writeValueAsString(
                                embedding
                        )
                );

            } catch (Exception e) {

                throw new RuntimeException(
                        "Failed to serialize embedding",
                        e
                );
            }

            chunkRepository.save(chunk);
        }

        return chunks.size();
    }

    public List<SearchResult> search(
            String query,
            int topK) {

        if (query == null
                || query.isBlank()) {

            throw new RuntimeException(
                    "Search query cannot be empty."
            );
        }

        List<Double> queryEmbedding =
                embeddingService
                        .generateEmbedding(query);

        List<DocumentChunk> chunks =
                chunkRepository.findAll();

        List<SearchResult> results =
                new ArrayList<>();

        for (DocumentChunk chunk : chunks) {

            try {

                List<Double> embedding =
                        objectMapper.readValue(
                                chunk.getEmbedding(),
                                new TypeReference<
                                        List<Double>>() {}
                        );

                double score =
                        cosineSimilarity(
                                queryEmbedding,
                                embedding
                        );

                results.add(
                        new SearchResult(
                                chunk,
                                score
                        )
                );

            } catch (Exception e) {

                throw new RuntimeException(
                        "Failed to parse stored embedding.",
                        e
                );
            }
        }

        return results.stream()
                .sorted(
                        Comparator.comparingDouble(
                                SearchResult::score
                        ).reversed()
                )
                .limit(topK)
                .toList();
    }

    private double cosineSimilarity(
            List<Double> a,
            List<Double> b) {

        if (a.size() != b.size()) {

            throw new RuntimeException(
                    "Embedding dimensions do not match."
            );
        }

        double dot = 0;
        double normA = 0;
        double normB = 0;

        for (int i = 0; i < a.size(); i++) {

            double x = a.get(i);
            double y = b.get(i);

            dot += x * y;
            normA += x * x;
            normB += y * y;
        }

        if (normA == 0 || normB == 0) {
            return 0;
        }

        return dot /
                (Math.sqrt(normA)
                        * Math.sqrt(normB));
    }

    public record SearchResult(
            DocumentChunk chunk,
            double score) {
    }
}