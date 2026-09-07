package com.plm.plm_ai.change.repository;

import com.plm.plm_ai.change.ECOImplementation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ECOImplementationRepository
        extends JpaRepository<ECOImplementation, Long> {

    List<ECOImplementation> findByEcoId(Long ecoId);

    boolean existsByEcoIdAndSourceRevisionId(
            Long ecoId,
            Long sourceRevisionId
    );
}