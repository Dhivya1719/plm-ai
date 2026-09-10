package com.plm.plm_ai.ai.dto;

public class LLMResponse {

    private String response;

    public LLMResponse() {
    }

    public LLMResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}