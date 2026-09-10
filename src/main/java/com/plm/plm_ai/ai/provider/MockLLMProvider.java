package com.plm.plm_ai.ai.provider;

import org.springframework.stereotype.Component;

@Component
public class MockLLMProvider implements LLMProvider {

    @Override
    public String generateResponse(String prompt) {

        return "Mock AI response generated successfully.";
    }
}