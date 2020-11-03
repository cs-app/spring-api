package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.CutiSummaryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CutiSummaryHistoryRepository extends JpaRepository<CutiSummaryHistory, Long>, JpaSpecificationExecutor<CutiSummaryHistory> {
    
}
