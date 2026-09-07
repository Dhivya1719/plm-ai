package com.plm.plm_ai.change.repository;

import com.plm.plm_ai.change.ECRAffectedRevision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ECRAffectedRevisionRepository
        extends JpaRepository<ECRAffectedRevision, Long> {

    // Find all affected revisions for an ECR
    List<ECRAffectedRevision> findByEcrId(Long ecrId);

    // Prevent the same revision from being added twice to an ECR
    boolean existsByEcrIdAndItemRevisionId(
            Long ecrId,
            Long itemRevisionId
    );
}