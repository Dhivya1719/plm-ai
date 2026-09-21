package com.plm.plm_ai.ai.rag;

import java.util.List;

public class SemanticSearchResponse {

    private String query;

    private List<SearchResult> results;

    public SemanticSearchResponse() {
    }

    public SemanticSearchResponse(
            String query,
            List<SearchResult> results) {

        this.query = query;
        this.results = results;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<SearchResult> getResults() {
        return results;
    }

    public void setResults(
            List<SearchResult> results) {

        this.results = results;
    }

    public static class SearchResult {

        private Long chunkId;
        private Long documentVersionId;
        private String documentNumber;
        private String documentName;
        private String versionCode;
        private String content;
        private double similarity;

        public SearchResult() {
        }

        public Long getChunkId() {
            return chunkId;
        }

        public void setChunkId(Long chunkId) {
            this.chunkId = chunkId;
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