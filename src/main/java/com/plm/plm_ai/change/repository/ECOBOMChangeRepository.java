package com.plm.plm_ai.change.repository;

import com.plm.plm_ai.change.ECOBOMChange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ECOBOMChangeRepository
        extends JpaRepository<ECOBOMChange, Long> {

    List<ECOBOMChange> findByEcoId(Long ecoId);

    List<ECOBOMChange> findByParentRevisionId(Long parentRevisionId);
}