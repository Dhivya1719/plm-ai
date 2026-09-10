package com.plm.plm_ai.ai.service;

import com.plm.plm_ai.ai.dto.BOMComponentContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChangeTermMatcherService {

    public List<BOMComponentContext> findMatchingComponents(
            String title,
            String description,
            String reason,
            List<BOMComponentContext> components) {

        List<BOMComponentContext> matches =
                new ArrayList<>();

        String changeText =
                buildChangeText(
                        title,
                        description,
                        reason
                );

        for (BOMComponentContext component : components) {

            String componentText =
                    buildComponentText(component);

            if (containsRelevantTerm(
                    changeText,
                    componentText)) {

                matches.add(component);
            }
        }

        return matches;
    }

    private String buildChangeText(
            String title,
            String description,
            String reason) {

        return normalize(
                safe(title)
                        + " "
                        + safe(description)
                        + " "
                        + safe(reason)
        );
    }

    private String buildComponentText(
            BOMComponentContext component) {

        return normalize(
                safe(component.getChildItemNumber())
                        + " "
                        + safe(component.getChildItemName())
                        + " "
                        + safe(component.getChildItemType())
        );
    }

    private boolean containsRelevantTerm(
            String changeText,
            String componentText) {

        /*
         * Specific engineering component terms.
         *
         * Avoid generic terms such as:
         * motor
         * assembly
         * part
         *
         * because they can create false matches.
         */
        String[] keywords = {
                "bearing",
                "rotor",
                "stator",
                "bolt",
                "pump",
                "shaft",
                "seal",
                "gear",
                "coupling",
                "bearing housing",
                "impeller",
                "fastener",
                "washer",
                "nut",
                "spring",
                "valve",
                "bracket",
                "housing"
        };

        for (String keyword : keywords) {

            if (changeText.contains(keyword)
                    && componentText.contains(keyword)) {

                return true;
            }
        }

        return false;
    }

    private String normalize(String text) {

        if (text == null) {
            return "";
        }

        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String safe(String text) {

        return text == null ? "" : text;
    }
}