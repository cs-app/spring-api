package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.CutiSummary;
import com.neta.teman.dawai.api.models.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CutiSummaryRepository extends JpaRepository<CutiSummary, Long>, JpaSpecificationExecutor<CutiSummary> {

    CutiSummary findByUser(User user);
}
