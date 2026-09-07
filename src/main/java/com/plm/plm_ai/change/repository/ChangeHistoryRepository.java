package com.plm.plm_ai.change.repository;

import com.plm.plm_ai.change.ChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeHistoryRepository
        extends JpaRepository<ChangeHistory, Long> {

    List<ChangeHistory> findByEcrIdOrderByPerformedAtAsc(Long ecrId);

    List<ChangeHistory> findByEcoIdOrderByPerformedAtAsc(Long ecoId);

    List<ChangeHistory> findByEntityTypeAndEntityIdOrderByPerformedAtAsc(
            String entityType,
            Long entityId
    );
}