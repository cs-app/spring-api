package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.Role;
import com.neta.teman.dawai.api.models.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CutiRepository extends JpaRepository<Cuti, Long>, JpaSpecificationExecutor<Cuti> {

    Cuti findByUserAndStartDateAndFinishDateAndCutiStatusGreaterThan(User user, Date start, Date time, Integer status);

    List<Cuti> findAllByUser(User user);

    List<Cuti> findAllByUserAndStartDateBetweenAndCutiStatusGreaterThan(User user, Date startDate, Date finishDate, Integer status);

    long countByUserAndStartDateBetweenAndCutiStatusGreaterThan(User user, Date startDate, Date finishDate, Integer status);

    long countByUserAndFinishDateBetweenAndCutiStatusGreaterThan(User user, Date startDate, Date finishDate, Integer status);
}
