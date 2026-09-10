package com.plm.plm_ai.ai.service;

import com.plm.plm_ai.ai.provider.LLMProvider;
import org.springframework.stereotype.Service;

@Service
public class LLMService {

    private final LLMProvider llmProvider;

    public LLMService(LLMProvider llmProvider) {
        this.llmProvider = llmProvider;
    }

    public String generateResponse(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "Prompt cannot be empty"
            );
        }

        return llmProvider.generateResponse(prompt);
    }
}