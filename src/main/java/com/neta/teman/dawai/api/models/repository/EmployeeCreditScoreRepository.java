package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.EmployeeCreditScore;
import com.neta.teman.dawai.api.models.dao.EmployeeSKP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeCreditScoreRepository extends JpaRepository<EmployeeCreditScore, Long>, JpaSpecificationExecutor<EmployeeCreditScore> {


}
