package com.plm.plm_ai.ai.service;

import com.plm.plm_ai.ai.dto.AffectedRevisionContext;
import com.plm.plm_ai.ai.dto.BOMComponentContext;
import com.plm.plm_ai.ai.dto.ChangeImpactResponse;
import com.plm.plm_ai.ai.dto.PLMContextResponse;
import com.plm.plm_ai.ai.dto.WhereUsedContext;

import org.springframework.stereotype.Service;

@Service
public class AIRecommendationPromptBuilderService {

    public String buildPrompt(
            PLMContextResponse context,
            ChangeImpactResponse impact) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are an AI engineering recommendation assistant
                working inside an enterprise Product Lifecycle Management
                (PLM) system.

                Your task is to recommend engineering actions for an
                Engineering Change Request (ECR).

                Use ONLY the PLM information provided below.

                Do NOT invent:
                - component specifications
                - dimensions
                - measurements
                - suppliers
                - standards
                - failure modes
                - test results
                - technical dependencies
                - assemblies
                - relationships
                - engineering facts

                Recommendations must be advisory.
                Do not claim that an action has already been performed.

                If technical information is missing, recommend that the
                engineer verify or obtain that information.

                Return ONLY valid JSON.
                Do not use markdown.
                Do not wrap the JSON in ```json fences.

                The JSON format MUST be:

                {
                  "recommendations": [
                    {
                      "priority": "HIGH|MEDIUM|LOW",
                      "category": "ENGINEERING_REVIEW|BOM_VALIDATION|COMPATIBILITY|REVISION_CONTROL|TESTING|RELEASE",
                      "action": "specific recommended action",
                      "reason": "reason based only on supplied PLM information"
                    }
                  ]
                }

                ============================
                ECR INFORMATION
                ============================

                """);

        prompt.append("ECR ID: ")
                .append(context.getEcrId())
                .append("\n");

        prompt.append("Change Number: ")
                .append(safe(context.getChangeNumber()))
                .append("\n");

        prompt.append("Title: ")
                .append(safe(context.getTitle()))
                .append("\n");

        prompt.append("Description: ")
                .append(safe(context.getDescription()))
                .append("\n");

        prompt.append("Reason: ")
                .append(safe(context.getReason()))
                .append("\n");

        prompt.append("Status: ")
                .append(safe(context.getStatus()))
                .append("\n");

        prompt.append("\n============================\n");
        prompt.append("AFFECTED REVISIONS\n");
        prompt.append("============================\n");

        if (context.getAffectedRevisions() == null
                || context.getAffectedRevisions().isEmpty()) {

            prompt.append("No affected revisions.\n");

        } else {

            for (AffectedRevisionContext revision
                    : context.getAffectedRevisions()) {

                prompt.append("\nRevision ID: ")
                        .append(revision.getRevisionId())
                        .append("\n");

                prompt.append("Revision Code: ")
                        .append(safe(revision.getRevisionCode()))
                        .append("\n");

                prompt.append("Description: ")
                        .append(safe(revision.getDescription()))
                        .append("\n");

                prompt.append("Lifecycle Status: ")
                        .append(safe(revision.getLifecycleStatus()))
                        .append("\n");

                prompt.append("Impact Description: ")
                        .append(safe(revision.getImpactDescription()))
                        .append("\n");

                prompt.append("\nBOM COMPONENTS:\n");

                if (revision.getBomComponents() == null
                        || revision.getBomComponents().isEmpty()) {

                    prompt.append("No BOM components.\n");

                } else {

                    for (BOMComponentContext component
                            : revision.getBomComponents()) {

                        prompt.append("- Item Number: ")
                                .append(
                                        safe(
                                                component
                                                        .getChildItemNumber()
                                        )
                                )
                                .append("\n");

                        prompt.append("  Item Name: ")
                                .append(
                                        safe(
                                                component
                                                        .getChildItemName()
                                        )
                                )
                                .append("\n");

                        prompt.append("  Item Type: ")
                                .append(
                                        safe(
                                                component
                                                        .getChildItemType()
                                        )
                                )
                                .append("\n");

                        prompt.append("  Revision: ")
                                .append(
                                        safe(
                                                component
                                                        .getChildRevisionCode()
                                        )
                                )
                                .append("\n");

                        prompt.append("  Quantity: ")
                                .append(component.getQuantity())
                                .append("\n");
                    }
                }

                prompt.append("\nWHERE-USED:\n");

                if (revision.getWhereUsedBy() == null
                        || revision.getWhereUsedBy().isEmpty()) {

                    prompt.append("No where-used relationships.\n");

                } else {

                    for (WhereUsedContext whereUsed
                            : revision.getWhereUsedBy()) {

                        prompt.append("- Parent Revision: ")
                                .append(
                                        safe(
                                                whereUsed
                                                        .getParentRevisionCode()
                                        )
                                )
                                .append("\n");

                        prompt.append("  Parent Revision ID: ")
                                .append(
                                        whereUsed
                                                .getParentRevisionId()
                                )
                                .append("\n");

                        prompt.append("  Quantity: ")
                                .append(whereUsed.getQuantity())
                                .append("\n");
                    }
                }
            }
        }

        prompt.append("\n============================\n");
        prompt.append("DETERMINISTIC IMPACT ANALYSIS\n");
        prompt.append("============================\n");

        prompt.append("Risk Level: ")
                .append(safe(impact.getRiskLevel()))
                .append("\n");

        prompt.append("Risk Reason: ")
                .append(safe(impact.getRiskReason()))
                .append("\n");

        prompt.append("\nDirect Impacts:\n");
        appendList(prompt, impact.getDirectImpacts());

        prompt.append("\nIndirect Impacts:\n");
        appendList(prompt, impact.getIndirectImpacts());

        prompt.append("\nAffected Assemblies:\n");
        appendList(prompt, impact.getAffectedAssemblies());

        prompt.append("\nComponent Impacts:\n");
        appendList(prompt, impact.getComponentImpacts());

        prompt.append("\nExisting Recommendations:\n");
        appendList(prompt, impact.getRecommendedActions());

        prompt.append("""
                
                ============================
                RECOMMENDATION RULES
                ============================

                Generate practical engineering recommendations.

                Consider these categories when relevant:

                ENGINEERING_REVIEW
                BOM_VALIDATION
                COMPATIBILITY
                REVISION_CONTROL
                TESTING
                RELEASE

                Recommendations should focus on actions such as:
                - reviewing affected components
                - validating BOM relationships
                - checking compatibility
                - reviewing affected assemblies
                - creating or verifying the next revision
                - performing appropriate engineering validation
                - reviewing the changed revision before release

                Do not invent specific tests, measurements or specifications.

                Generate between 3 and 7 recommendations.

                Return ONLY the JSON object.
                """);

        return prompt.toString();
    }

    private void appendList(
            StringBuilder prompt,
            java.util.List<String> values) {

        if (values == null || values.isEmpty()) {
            prompt.append("- None\n");
            return;
        }

        for (String value : values) {

            prompt.append("- ")
                    .append(safe(value))
                    .append("\n");
        }
    }

    private String safe(String value) {

        return value == null
                ? "Not provided"
                : value;
    }
}