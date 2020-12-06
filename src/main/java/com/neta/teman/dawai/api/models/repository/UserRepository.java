package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.Jabatan;
import com.neta.teman.dawai.api.models.dao.PangkatGolongan;
import com.neta.teman.dawai.api.models.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);

    // User findByEmployee_Pangkats_PangkatGolongan(PangkatGolongan pangkatGolongan);

//    User findByEmployee_Pangkats_PangkatGolongantContaining(PangkatGolongan pangkatGolongan);

    Long countByEmployee_JabatanDetail_Jabatan(Jabatan o);

    Long countByEmployee_JabatanDetail_JabatanAndEmployeeTanggalLahirGreaterThanEqual(Jabatan o, Date time);

}
