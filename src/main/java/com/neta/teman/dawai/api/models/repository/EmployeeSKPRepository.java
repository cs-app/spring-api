package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.EmployeeSKP;
import com.neta.teman.dawai.api.models.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EmployeeSKPRepository extends JpaRepository<EmployeeSKP, Long>, JpaSpecificationExecutor<EmployeeSKP> {


}
