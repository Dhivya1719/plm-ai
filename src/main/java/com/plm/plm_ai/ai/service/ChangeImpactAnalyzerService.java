package com.plm.plm_ai.ai.service;

import com.plm.plm_ai.ai.dto.AffectedRevisionContext;
import com.plm.plm_ai.ai.dto.BOMComponentContext;
import com.plm.plm_ai.ai.dto.PLMContextResponse;
import com.plm.plm_ai.ai.dto.WhereUsedContext;
import com.plm.plm_ai.ai.dto.ChangeImpactResponse;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChangeImpactAnalyzerService {

    private final PLMContextBuilderService contextBuilderService;

    private final ChangeTermMatcherService changeTermMatcherService;

    public ChangeImpactAnalyzerService(
            PLMContextBuilderService contextBuilderService,
            ChangeTermMatcherService changeTermMatcherService) {

        this.contextBuilderService = contextBuilderService;
        this.changeTermMatcherService = changeTermMatcherService;
    }

    public ChangeImpactResponse analyze(Long ecrId) {

        /*
         * Step 1:
         * Build complete PLM context for the ECR.
         */
        PLMContextResponse context =
                contextBuilderService.buildContext(ecrId);

        ChangeImpactResponse response =
                new ChangeImpactResponse();

        response.setEcrId(context.getEcrId());
        response.setChangeNumber(context.getChangeNumber());
        response.setTitle(context.getTitle());

        /*
         * Impact collections
         */
        List<String> directImpacts = new ArrayList<>();
        List<String> indirectImpacts = new ArrayList<>();
        List<String> affectedAssemblies = new ArrayList<>();

        List<String> componentImpacts = new ArrayList<>();
        List<String> componentRecommendations = new ArrayList<>();

        List<String> recommendedActions = new ArrayList<>();

        int whereUsedCount = 0;
        int affectedRevisionCount = 0;

        /*
         * Step 2:
         * Analyze every affected revision.
         */
        for (AffectedRevisionContext revision
                : context.getAffectedRevisions()) {

            affectedRevisionCount++;

            /*
             * Direct impact
             */
            directImpacts.add(
                    "Revision "
                            + revision.getRevisionCode()
                            + " (ID: "
                            + revision.getRevisionId()
                            + ") is directly affected."
            );

            /*
             * Step 3:
             * Analyze where-used relationships.
             */
            if (revision.getWhereUsedBy() != null) {

                for (WhereUsedContext whereUsed
                        : revision.getWhereUsedBy()) {

                    whereUsedCount++;

                    String assembly =
                            "Revision "
                                    + whereUsed.getParentRevisionCode()
                                    + " (ID: "
                                    + whereUsed.getParentRevisionId()
                                    + ")";

                    indirectImpacts.add(
                            "Affected revision is used by "
                                    + assembly
                                    + " with quantity "
                                    + whereUsed.getQuantity()
                                    + "."
                    );

                    affectedAssemblies.add(assembly);
                }
            }

            /*
             * Step 4:
             * Perform component-level matching.
             *
             * Example:
             *
             * ECR:
             * "Replace existing bearing with improved bearing"
             *
             * BOM:
             * Bearing -> Industrial Motor Bearing
             *
             * The matcher identifies the bearing as a
             * potentially affected component.
             */
            if (revision.getBomComponents() != null
                    && !revision.getBomComponents().isEmpty()) {

                List<BOMComponentContext> matchingComponents =
                        changeTermMatcherService.findMatchingComponents(
                                context.getTitle(),
                                context.getDescription(),
                                context.getReason(),
                                revision.getBomComponents()
                        );

                for (BOMComponentContext component
                        : matchingComponents) {

                    String componentDescription =
                            "Potentially affected component: "
                                    + component.getChildItemNumber()
                                    + " - "
                                    + component.getChildItemName()
                                    + " (Revision "
                                    + component.getChildRevisionCode()
                                    + ", Quantity: "
                                    + component.getQuantity()
                                    + ")";

                    componentImpacts.add(
                            componentDescription
                    );

                    componentRecommendations.add(
                            "Review component "
                                    + component.getChildItemNumber()
                                    + " before implementing the change."
                    );
                }
            }
        }

        /*
         * Step 5:
         * Determine overall risk level.
         *
         * HIGH:
         *     3 or more where-used relationships
         *
         * MEDIUM:
         *     1 or more where-used relationships
         *     OR a specific BOM component was matched
         *
         * LOW:
         *     Only affected revisions exist
         *     with no additional impact detected
         */
        String riskLevel;
        String riskReason;

        if (whereUsedCount >= 3) {
            riskLevel = "HIGH";
            riskReason =
                    "The affected component is used by multiple assemblies.";
        }
        else if (whereUsedCount >= 1) {
            riskLevel = "MEDIUM";
            riskReason =
                    "The affected component is used by other assemblies.";
        }
        else if (!componentImpacts.isEmpty()) {
            riskLevel = "MEDIUM";
            riskReason =
                    "A BOM component was identified as potentially affected by the change description.";
        }
        else if (!context.getAffectedRevisions().isEmpty()) {
            riskLevel = "LOW";
            riskReason =
                    "The ECR contains affected revisions, but no specific component or where-used impact was detected.";
        }
        else {
            riskLevel = "LOW";
            riskReason =
                    "No affected revisions or component impacts were detected.";
        }

        /*
         * Step 6:
         * Generate recommendations based on where-used impact.
         */
        if (whereUsedCount > 0) {

            recommendedActions.add(
                    "Review all parent assemblies using the affected revision."
            );

            recommendedActions.add(
                    "Validate BOM relationships and quantities."
            );
        }

        /*
         * Step 7:
         * Add component-specific recommendations.
         */
        recommendedActions.addAll(
                componentRecommendations
        );

        /*
         * Step 8:
         * General engineering recommendations.
         */
        recommendedActions.add(
                "Perform engineering review before releasing the changed revision."
        );

        recommendedActions.add(
                "Create and verify the next revision before final release."
        );

        /*
         * Step 9:
         * Populate final response.
         */
        response.setRiskLevel(riskLevel);
        response.setRiskReason(riskReason);

        response.setDirectImpacts(
                directImpacts
        );

        response.setIndirectImpacts(
                indirectImpacts
        );

        response.setAffectedAssemblies(
                affectedAssemblies
        );

        response.setComponentImpacts(
                componentImpacts
        );

        response.setComponentRecommendations(
                componentRecommendations
        );

        response.setRecommendedActions(
                recommendedActions
        );

        return response;
    }
}