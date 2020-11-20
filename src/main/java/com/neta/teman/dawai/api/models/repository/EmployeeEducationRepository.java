package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.EmployeeEducation;
import com.neta.teman.dawai.api.models.dao.EmployeeJabatan;
import com.neta.teman.dawai.api.models.dao.Jabatan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeEducationRepository extends JpaRepository<EmployeeEducation, Long>, JpaSpecificationExecutor<EmployeeEducation> {

}
