package com.plm.plm_ai.ai.rag;

import java.util.List;

public class PLMRAGResponse {

    private String question;

    private String answer;

    private List<Source> sources;

    public PLMRAGResponse() {
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

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public static class Source {

        private Long documentVersionId;
        private String documentNumber;
        private String documentName;
        private String versionCode;
        private String content;
        private double similarity;

        public Source() {
        }

        public Long getDocumentVersionId() {
            return documentVersionId;
        }

        public void setDocumentVersionId(
                Long documentVersionId) {

            this.documentVersionId =
                    documentVersionId;
        }

        public String getDocumentNumber() {
            return documentNumber;
        }

        public void setDocumentNumber(
                String documentNumber) {

            this.documentNumber =
                    documentNumber;
        }

        public String getDocumentName() {
            return documentName;
        }

        public void setDocumentName(
                String documentName) {

            this.documentName =
                    documentName;
        }

        public String getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(
                String versionCode) {

            this.versionCode =
                    versionCode;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public double getSimilarity() {
            return similarity;
        }

        public void setSimilarity(
                double similarity) {

            this.similarity = similarity;
        }
    }
}