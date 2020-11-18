package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.dao.EmployeeDocument;
import com.neta.teman.dawai.api.models.dao.EmployeeJabatan;
import com.neta.teman.dawai.api.models.dao.Jabatan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeJabatanRepository extends JpaRepository<EmployeeJabatan, Long>, JpaSpecificationExecutor<EmployeeJabatan> {

    Page<EmployeeJabatan> findAllByJabatan(Jabatan document, Pageable pageable);
}
