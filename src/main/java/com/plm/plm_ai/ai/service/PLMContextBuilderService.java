package com.plm.plm_ai.ai.service;

import com.plm.plm_ai.ai.dto.AffectedRevisionContext;
import com.plm.plm_ai.ai.dto.BOMComponentContext;
import com.plm.plm_ai.ai.dto.PLMContextResponse;
import com.plm.plm_ai.ai.dto.WhereUsedContext;
import com.plm.plm_ai.change.ECRAffectedRevision;
import com.plm.plm_ai.change.EngineeringChangeRequest;
import com.plm.plm_ai.change.repository.ECRAffectedRevisionRepository;
import com.plm.plm_ai.change.repository.EngineeringChangeRequestRepository;
import com.plm.plm_ai.item.BOMLine;
import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.repository.BOMLineRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PLMContextBuilderService {

    private final EngineeringChangeRequestRepository ecrRepository;
    private final ECRAffectedRevisionRepository affectedRevisionRepository;
    private final BOMLineRepository bomLineRepository;

    public PLMContextBuilderService(
            EngineeringChangeRequestRepository ecrRepository,
            ECRAffectedRevisionRepository affectedRevisionRepository,
            BOMLineRepository bomLineRepository) {

        this.ecrRepository = ecrRepository;
        this.affectedRevisionRepository = affectedRevisionRepository;
        this.bomLineRepository = bomLineRepository;
    }

    @Transactional(readOnly = true)
    public PLMContextResponse buildContext(Long ecrId) {

        EngineeringChangeRequest ecr =
                ecrRepository.findById(ecrId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "ECR not found: " + ecrId));

        List<ECRAffectedRevision> affectedRevisions =
                affectedRevisionRepository.findByEcrId(ecrId);

        PLMContextResponse response =
                new PLMContextResponse();

        response.setEcrId(ecr.getId());
        response.setChangeNumber(ecr.getChangeNumber());
        response.setTitle(ecr.getTitle());
        response.setDescription(ecr.getDescription());
        response.setReason(ecr.getReason());

        response.setStatus(
                ecr.getStatus() != null
                        ? ecr.getStatus().name()
                        : null
        );

        response.setRequestedBy(ecr.getRequestedBy());

        List<AffectedRevisionContext> revisionContexts =
                new ArrayList<>();

        for (ECRAffectedRevision affected : affectedRevisions) {

            ItemRevision revision =
                    affected.getItemRevision();

            AffectedRevisionContext revisionContext =
                    new AffectedRevisionContext();

            revisionContext.setRevisionId(
                    revision.getId());

            revisionContext.setRevisionCode(
                    revision.getRevisionCode());

            revisionContext.setDescription(
                    revision.getDescription());

            revisionContext.setLifecycleStatus(
                    revision.getStatus() != null
                            ? revision.getStatus().name()
                            : null
            );

            revisionContext.setImpactDescription(
                    affected.getImpactDescription());

            revisionContext.setBomComponents(
                    buildBOMContext(revision.getId()));

            revisionContext.setWhereUsedBy(
                    buildWhereUsedContext(revision.getId()));

            revisionContexts.add(revisionContext);
        }

        response.setAffectedRevisions(revisionContexts);

        return response;
    }

    private List<BOMComponentContext> buildBOMContext(
            Long revisionId) {

        List<BOMLine> bomLines =
                bomLineRepository
                        .findByParentRevisionId(revisionId);

        List<BOMComponentContext> result =
                new ArrayList<>();

        for (BOMLine bomLine : bomLines) {

            ItemRevision child =
                    bomLine.getChildRevision();

            BOMComponentContext context =
                    new BOMComponentContext();

            context.setBomLineId(
                    bomLine.getId());

            context.setChildRevisionId(
                    child.getId());

            context.setChildRevisionCode(
                    child.getRevisionCode());

            context.setQuantity(
                    bomLine.getQuantity());

            /*
             * Component Item information
             *
             * ItemRevision -> Item
             *
             * This allows the AI impact analyzer
             * to understand the actual component,
             * instead of only seeing revision "A".
             */

            context.setChildItemId(
                    child.getItem().getId());

            context.setChildItemNumber(
                    child.getItem().getItemNumber());

            context.setChildItemName(
                    child.getItem().getName());

            context.setChildItemType(
                    child.getItem().getItemType());

            result.add(context);
        }

        return result;
    }

    private List<WhereUsedContext> buildWhereUsedContext(
            Long revisionId) {

        List<BOMLine> bomLines =
                bomLineRepository
                        .findByChildRevisionId(revisionId);

        List<WhereUsedContext> result =
                new ArrayList<>();

        for (BOMLine bomLine : bomLines) {

            ItemRevision parent =
                    bomLine.getParentRevision();

            WhereUsedContext context =
                    new WhereUsedContext();

            context.setBomLineId(
                    bomLine.getId());

            context.setParentRevisionId(
                    parent.getId());

            context.setParentRevisionCode(
                    parent.getRevisionCode());

            context.setQuantity(
                    bomLine.getQuantity());

            result.add(context);
        }

        return result;
    }
}