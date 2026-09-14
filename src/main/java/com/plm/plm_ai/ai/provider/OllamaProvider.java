package com.plm.plm_ai.ai.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Profile("ollama")
public class OllamaProvider implements LLMProvider {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Value("${ollama.model}")
    private String model;

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    @Override
    public String generateResponse(String prompt) {

        try {

            String requestBody =
                    objectMapper.createObjectNode()
                            .put("model", model)
                            .put("prompt", prompt)
                            .put("stream", false)
                            .toString();

            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            baseUrl + "/api/generate"
                                    )
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(requestBody)
                            )
                            .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {

                throw new RuntimeException(
                        "Ollama API error. HTTP status: "
                                + response.statusCode()
                                + ", response: "
                                + response.body()
                );
            }

            JsonNode responseJson =
                    objectMapper.readTree(
                            response.body()
                    );

            JsonNode responseText =
                    responseJson.get("response");

            if (responseText == null) {

                throw new RuntimeException(
                        "Ollama response did not contain generated text."
                );
            }

            return responseText.asText();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to communicate with Ollama: "
                            + e.getMessage(),
                    e
            );
        }
    }
}