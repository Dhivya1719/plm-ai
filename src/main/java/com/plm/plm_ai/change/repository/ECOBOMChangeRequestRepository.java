package com.plm.plm_ai.change.repository;

import com.plm.plm_ai.change.ECOBOMChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ECOBOMChangeRequestRepository
        extends JpaRepository<ECOBOMChangeRequest, Long> {

    List<ECOBOMChangeRequest> findByEcoId(Long ecoId);
}