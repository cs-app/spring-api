package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.Jabatan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JabatanRepository extends JpaRepository<Jabatan, Long>, JpaSpecificationExecutor<Jabatan> {

    Jabatan findByName(String namaJabatan);
}
