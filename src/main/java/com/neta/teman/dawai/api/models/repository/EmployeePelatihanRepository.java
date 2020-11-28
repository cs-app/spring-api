package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.EmployeeCreditScore;
import com.neta.teman.dawai.api.models.dao.EmployeePelatihan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeePelatihanRepository extends JpaRepository<EmployeePelatihan, Long>, JpaSpecificationExecutor<EmployeePelatihan> {

}
