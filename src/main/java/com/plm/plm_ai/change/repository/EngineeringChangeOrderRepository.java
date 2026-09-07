package com.plm.plm_ai.change.repository;

import com.plm.plm_ai.change.EngineeringChangeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EngineeringChangeOrderRepository
        extends JpaRepository<EngineeringChangeOrder, Long> {

    Optional<EngineeringChangeOrder> findTopByOrderByIdDesc();

    boolean existsBySourceEcrId(Long ecrId);
}