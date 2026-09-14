package com.plm.plm_ai.ai.provider;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mock")
public class MockLLMProvider implements LLMProvider {

    @Override
    public String generateResponse(String prompt) {

        return "Mock AI response generated successfully.";
    }
}