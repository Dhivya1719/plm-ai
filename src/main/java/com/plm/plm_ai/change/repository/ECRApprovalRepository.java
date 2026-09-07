package com.plm.plm_ai.change.repository;

import com.plm.plm_ai.change.ECRApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ECRApprovalRepository
        extends JpaRepository<ECRApproval, Long> {

    List<ECRApproval> findByEcrIdOrderByReviewedAtAsc(Long ecrId);
}