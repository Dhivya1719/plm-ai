package com.plm.plm_ai.ai.assistant;

import org.springframework.stereotype.Service;

@Service
public class PLMQuestionRouter {

    public QuestionType route(String question) {

        if (question == null || question.isBlank()) {
            return QuestionType.GENERAL_PLM;
        }

        String text =
                question
                        .toLowerCase()
                        .trim();

        /*
         * ---------------------------------------------------------
         * 1. CHANGE / IMPACT QUESTIONS
         * ---------------------------------------------------------
         */

        if (text.contains("ecr")
                || text.contains("impact")
                || text.contains("change request")
                || text.contains("change impact")) {

            return QuestionType.CHANGE_IMPACT;
        }

        /*
         * ---------------------------------------------------------
         * 2. DOCUMENT / RAG QUESTIONS
         * ---------------------------------------------------------
         */

        if (text.contains("document")
                || text.contains("specification")
                || text.contains("report")
                || text.contains("drawing")
                || text.contains("according to")
                || text.contains("what does")) {

            return QuestionType.DOCUMENT_SEARCH;
        }

        /*
         * ---------------------------------------------------------
         * 3. EXPLICIT PLM QUESTIONS
         * ---------------------------------------------------------
         */

        if (text.contains("bom")
                || text.contains("bill of material")
                || text.contains("component")
                || text.contains("revision")
                || text.contains("item")
                || text.contains("where used")
                || text.contains("where-used")) {

            return QuestionType.PLM_DATA;
        }

        /*
         * ---------------------------------------------------------
         * 4. PLM ITEM NUMBER DETECTION
         *
         * Examples:
         * MTR-00001
         * PMP-00001
         * BRG-00001
         * ROT-00001
         * STA-00001
         * ---------------------------------------------------------
         */

        if (text.matches(
                ".*\\b[a-z]{3}-\\d{5}\\b.*")) {

            return QuestionType.PLM_DATA;
        }

        /*
         * ---------------------------------------------------------
         * 5. REVISION ID DETECTION
         *
         * Examples:
         * "revision 9"
         * "revision id 10"
         * ---------------------------------------------------------
         */

        if (text.matches(
                ".*\\b(revision|id)\\s+\\d+\\b.*")) {

            return QuestionType.PLM_DATA;
        }

        /*
         * ---------------------------------------------------------
         * 6. DEFAULT
         * ---------------------------------------------------------
         */

        return QuestionType.GENERAL_PLM;
    }

    public enum QuestionType {

        PLM_DATA,

        DOCUMENT_SEARCH,

        CHANGE_IMPACT,

        GENERAL_PLM
    }
}