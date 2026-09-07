package com.plm.plm_ai.change.service;

import com.plm.plm_ai.change.ChangeHistory;
import com.plm.plm_ai.change.EngineeringChangeOrder;
import com.plm.plm_ai.change.EngineeringChangeRequest;
import com.plm.plm_ai.change.repository.ChangeHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChangeHistoryService {

    private final ChangeHistoryRepository changeHistoryRepository;

    public ChangeHistoryService(
            ChangeHistoryRepository changeHistoryRepository) {

        this.changeHistoryRepository = changeHistoryRepository;
    }

    public ChangeHistory record(
            String entityType,
            Long entityId,
            String action,
            String description,
            String performedBy,
            EngineeringChangeRequest ecr,
            EngineeringChangeOrder eco) {

        ChangeHistory history = new ChangeHistory();

        history.setEntityType(entityType);
        history.setEntityId(entityId);
        history.setAction(action);
        history.setDescription(description);
        history.setPerformedBy(performedBy);
        history.setEcr(ecr);
        history.setEco(eco);

        return changeHistoryRepository.save(history);
    }

    public List<ChangeHistory> getECRHistory(Long ecrId) {
        return changeHistoryRepository
                .findByEcrIdOrderByPerformedAtAsc(ecrId);
    }

    public List<ChangeHistory> getECOHistory(Long ecoId) {
        return changeHistoryRepository
                .findByEcoIdOrderByPerformedAtAsc(ecoId);
    }

    public List<ChangeHistory> getEntityHistory(
            String entityType,
            Long entityId) {

        return changeHistoryRepository
                .findByEntityTypeAndEntityIdOrderByPerformedAtAsc(
                        entityType,
                        entityId
                );
    }
}