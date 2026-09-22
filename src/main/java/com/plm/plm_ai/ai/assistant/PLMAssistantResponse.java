package com.plm.plm_ai.ai.assistant;

import java.util.List;

public class PLMAssistantResponse {

    private String question;
    private String answer;
    private String sourceType;

    private List<Source> sources;

    public PLMAssistantResponse() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public static class Source {

        private String type;
        private String reference;
        private String description;

        public Source() {
        }

        public Source(
                String type,
                String reference,
                String description) {

            this.type = type;
            this.reference = reference;
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(
                String description) {

            this.description = description;
        }
    }
}