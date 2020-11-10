package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.PangkatGolongan;
import com.neta.teman.dawai.api.models.dao.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PangkatGolonganRepository extends JpaRepository<PangkatGolongan, Long>, JpaSpecificationExecutor<PangkatGolongan> {

}
