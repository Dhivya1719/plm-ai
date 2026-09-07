package com.plm.plm_ai.change.service;

import com.plm.plm_ai.change.*;
import com.plm.plm_ai.change.repository.*;
import com.plm.plm_ai.item.ItemRevision;
import com.plm.plm_ai.item.repository.ItemRevisionRepository;
import com.plm.plm_ai.item.service.ItemRevisionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.plm.plm_ai.item.LifecycleStatus;

import java.util.List;

@Service
public class ECOImplementationService {

    private final EngineeringChangeOrderRepository ecoRepository;
    private final EngineeringChangeRequestRepository ecrRepository;
    private final ECRAffectedRevisionRepository affectedRepository;
    private final ECOImplementationRepository implementationRepository;
    private final ItemRevisionRepository revisionRepository;
    private final ItemRevisionService revisionService;

    public ECOImplementationService(
            EngineeringChangeOrderRepository ecoRepository,
            EngineeringChangeRequestRepository ecrRepository,
            ECRAffectedRevisionRepository affectedRepository,
            ECOImplementationRepository implementationRepository,
            ItemRevisionRepository revisionRepository,
            ItemRevisionService revisionService) {

        this.ecoRepository = ecoRepository;
        this.ecrRepository = ecrRepository;
        this.affectedRepository = affectedRepository;
        this.implementationRepository = implementationRepository;
        this.revisionRepository = revisionRepository;
        this.revisionService = revisionService;
    }

    @Transactional
    public ECOImplementation implementRevisionChange(
            Long ecoId,
            Long sourceRevisionId,
            String description) {

        EngineeringChangeOrder eco =
                ecoRepository.findById(ecoId)
                        .orElseThrow(() ->
                                new RuntimeException("ECO not found"));

        if (eco.getStatus() != ECOStatus.IMPLEMENTATION) {
            throw new RuntimeException(
                    "ECO must be in IMPLEMENTATION status"
            );
        }

        EngineeringChangeRequest ecr =
                eco.getSourceEcr();

        boolean affected =
                affectedRepository
                        .existsByEcrIdAndItemRevisionId(
                                ecr.getId(),
                                sourceRevisionId
                        );

        if (!affected) {
            throw new RuntimeException(
                    "Revision is not affected by the source ECR"
            );
        }

        if (implementationRepository
                .existsByEcoIdAndSourceRevisionId(
                        ecoId,
                        sourceRevisionId)) {

            throw new RuntimeException(
                    "Revision is already implemented by this ECO"
            );
        }

        ItemRevision sourceRevision =
                revisionRepository.findById(sourceRevisionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Source revision not found"
                                ));

        if (sourceRevision.getStatus()
                != LifecycleStatus.RELEASED) {

            throw new RuntimeException(
                    "Source revision must be RELEASED before ECO implementation"
            );
        }

        ItemRevision newRevision =
                revisionService.createRevisionFromExisting(
                        sourceRevisionId
                );

        ECOImplementation implementation =
                new ECOImplementation();

        implementation.setEco(eco);
        implementation.setSourceRevision(sourceRevision);
        implementation.setNewRevision(newRevision);
        implementation.setImplementationDescription(description);

        return implementationRepository.save(implementation);
    }

    public List<ECOImplementation> getImplementations(
            Long ecoId) {

        return implementationRepository.findByEcoId(ecoId);
    }
}