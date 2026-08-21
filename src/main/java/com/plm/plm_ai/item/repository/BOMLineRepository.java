package com.plm.plm_ai.item.repository;

import com.plm.plm_ai.item.BOMLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BOMLineRepository extends JpaRepository<BOMLine, Long> {

    List<BOMLine> findByParentRevisionId(Long parentRevisionId);

    boolean existsByParentRevisionIdAndChildRevisionId(
            Long parentRevisionId,
            Long childRevisionId
    );
}