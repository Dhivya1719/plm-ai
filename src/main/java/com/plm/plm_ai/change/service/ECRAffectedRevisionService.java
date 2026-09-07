package com.plm.plm_ai.change.service;

import com.plm.plm_ai.change.ECRAffectedRevision;
import com.plm.plm_ai.change.EngineeringChangeRequest;
import com.plm.plm_ai.change.repository.ECRAffectedRevisionRepository;
import com.plm.plm_ai.change.repository.EngineeringChangeRequestRepository;
import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.repository.ItemRevisionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ECRAffectedRevisionService {

    private final ECRAffectedRevisionRepository affectedRevisionRepository;
    private final EngineeringChangeRequestRepository ecrRepository;
    private final ItemRevisionRepository revisionRepository;

    public ECRAffectedRevisionService(
            ECRAffectedRevisionRepository affectedRevisionRepository,
            EngineeringChangeRequestRepository ecrRepository,
            ItemRevisionRepository revisionRepository) {

        this.affectedRevisionRepository = affectedRevisionRepository;
        this.ecrRepository = ecrRepository;
        this.revisionRepository = revisionRepository;
    }

    public ECRAffectedRevision addAffectedRevision(
            Long ecrId,
            Long revisionId,
            String impactDescription) {

        EngineeringChangeRequest ecr =
                ecrRepository.findById(ecrId)
                        .orElseThrow(() ->
                                new RuntimeException("ECR not found"));

        ItemRevision revision =
                revisionRepository.findById(revisionId)
                        .orElseThrow(() ->
                                new RuntimeException("Item Revision not found"));

        if (affectedRevisionRepository
                .existsByEcrIdAndItemRevisionId(ecrId, revisionId)) {

            throw new RuntimeException(
                    "Revision is already affected by this ECR"
            );
        }

        ECRAffectedRevision affected =
                new ECRAffectedRevision();

        affected.setEcr(ecr);
        affected.setItemRevision(revision);
        affected.setImpactDescription(impactDescription);

        return affectedRevisionRepository.save(affected);
    }

    public List<ECRAffectedRevision> getAffectedRevisions(Long ecrId) {

        return affectedRevisionRepository.findByEcrId(ecrId);
    }
}