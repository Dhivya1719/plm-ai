package com.plm.plm_ai.change.repository;

import com.plm.plm_ai.change.EngineeringChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EngineeringChangeRequestRepository
        extends JpaRepository<EngineeringChangeRequest, Long> {

    Optional<EngineeringChangeRequest> findByChangeNumber(
            String changeNumber
    );

    Optional<EngineeringChangeRequest> findTopByOrderByIdDesc();

    boolean existsByChangeNumber(String changeNumber);
}