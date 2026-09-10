package com.plm.plm_ai.ai.dto;

public class LLMRequest {

    private String prompt;

    public LLMRequest() {
    }

    public LLMRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}