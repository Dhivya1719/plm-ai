package com.plm.plm_ai.ai.service;

import com.plm.plm_ai.ai.dto.AffectedRevisionContext;
import com.plm.plm_ai.ai.dto.BOMComponentContext;
import com.plm.plm_ai.ai.dto.ChangeImpactResponse;
import com.plm.plm_ai.ai.dto.PLMContextResponse;
import com.plm.plm_ai.ai.dto.WhereUsedContext;

import org.springframework.stereotype.Service;

@Service
public class AIReasoningPromptBuilderService {

    public String buildPrompt(
            PLMContextResponse context,
            ChangeImpactResponse impact) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
                You are an engineering change impact analysis assistant
                working inside an enterprise Product Lifecycle Management
                (PLM) system.

                Your task is to reason about an Engineering Change Request
                (ECR) using ONLY the PLM information provided below.

                Do not invent components, revisions, relationships,
                quantities, risks, or technical facts that are not present
                in the supplied data.

                Distinguish clearly between:
                1. Facts directly provided by the PLM system.
                2. Engineering reasoning or potential consequences.

                Analyze:
                - Direct impact
                - Indirect impact
                - BOM/component impact
                - Where-used impact
                - Risk significance
                - Engineering verification concerns

                If information is missing, explicitly say that additional
                engineering information is required.

                Provide a concise but useful engineering assessment.

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

        prompt.append("Requested By: ")
                .append(safe(context.getRequestedBy()))
                .append("\n\n");

        prompt.append("""
                ============================
                AFFECTED REVISIONS
                ============================

                """);

        if (context.getAffectedRevisions() == null
                || context.getAffectedRevisions().isEmpty()) {

            prompt.append("No affected revisions were identified.\n");

        } else {

            for (AffectedRevisionContext revision
                    : context.getAffectedRevisions()) {

                prompt.append("Revision ID: ")
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

                prompt.append("\nWHERE-USED RELATIONSHIPS:\n");

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

                prompt.append("\n");
            }
        }

        prompt.append("""
                ============================
                DETERMINISTIC IMPACT ANALYSIS
                ============================

                """);

        prompt.append("Risk Level: ")
                .append(safe(impact.getRiskLevel()))
                .append("\n");

        prompt.append("Risk Reason: ")
                .append(safe(impact.getRiskReason()))
                .append("\n\n");

        prompt.append("Direct Impacts:\n");

        appendList(
                prompt,
                impact.getDirectImpacts()
        );

        prompt.append("\nIndirect Impacts:\n");

        appendList(
                prompt,
                impact.getIndirectImpacts()
        );

        prompt.append("\nAffected Assemblies:\n");

        appendList(
                prompt,
                impact.getAffectedAssemblies()
        );

        prompt.append("\nPotential Component Impacts:\n");

        appendList(
                prompt,
                impact.getComponentImpacts()
        );

        prompt.append("\nDeterministic Recommendations:\n");

        appendList(
                prompt,
                impact.getRecommendedActions()
        );

        prompt.append("""
                
                ============================
                REQUIRED AI ANALYSIS
                ============================

                Based strictly on the information above, provide:

                1. Change Summary
                Explain what the engineering change appears to affect.

                2. Direct Impact Reasoning
                Explain the directly affected revision(s).

                3. Component Impact Reasoning
                Explain why identified BOM components may require review.

                4. Indirect Impact Reasoning
                Explain downstream or parent assembly effects if
                where-used information exists.

                5. Risk Interpretation
                Explain the significance of the deterministic risk level.

                6. Engineering Verification
                Identify what engineers should verify before implementation.

                7. Release Considerations
                Explain what should be reviewed before releasing the
                changed revision.

                Do not claim that a component, assembly, measurement,
                specification, failure mode, or dependency exists unless
                it is present in the supplied PLM context.

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

        return value == null ? "Not provided" : value;
    }
}