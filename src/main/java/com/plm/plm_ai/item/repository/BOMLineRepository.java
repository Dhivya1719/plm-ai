package com.plm.plm_ai.item.repository;

import com.plm.plm_ai.item.BOMLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BOMLineRepository extends JpaRepository<BOMLine, Long> {

    // Get all components of a parent revision
    List<BOMLine> findByParentRevisionId(Long parentRevisionId);

    // Check whether a parent-child relationship already exists
    boolean existsByParentRevisionIdAndChildRevisionId(
            Long parentRevisionId,
            Long childRevisionId
    );

    // WHERE-USED:
    // Find all BOM lines where the given revision is the child
    List<BOMLine> findByChildRevisionId(Long childRevisionId);
}