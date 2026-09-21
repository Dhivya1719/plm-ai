package com.plm.plm_ai.ai.rag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class OllamaEmbeddingService {

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Value("${ollama.embedding-model}")
    private String embeddingModel;

    public List<Double> generateEmbedding(
            String text) {

        try {

            var requestNode =
                    objectMapper.createObjectNode();

            requestNode.put(
                    "model",
                    embeddingModel
            );

            requestNode.put(
                    "input",
                    text
            );

            requestNode.put(
                    "truncate",
                    true
            );

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            baseUrl
                                                    + "/api/embed"
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(
                                                    requestNode.toString()
                                            )
                            )
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {

                throw new RuntimeException(
                        "Ollama embedding API error. HTTP "
                                + response.statusCode()
                                + ": "
                                + response.body()
                );
            }

            JsonNode root =
                    objectMapper.readTree(
                            response.body()
                    );

            JsonNode embeddings =
                    root.get("embeddings");

            if (embeddings == null
                    || !embeddings.isArray()
                    || embeddings.isEmpty()) {

                throw new RuntimeException(
                        "Ollama returned no embeddings."
                );
            }

            JsonNode vector =
                    embeddings.get(0);

            List<Double> result =
                    new ArrayList<>();

            for (JsonNode value : vector) {
                result.add(value.asDouble());
            }

            return result;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate embedding: "
                            + e.getMessage(),
                    e
            );
        }
    }
}