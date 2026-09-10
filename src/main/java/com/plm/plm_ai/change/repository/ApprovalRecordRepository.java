package com.plm.plm_ai.change.repository;

import com.plm.plm_ai.change.ApprovalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRecordRepository
        extends JpaRepository<ApprovalRecord, Long> {

    List<ApprovalRecord> findByEcrIdOrderByCreatedAtAsc(Long ecrId);
}