package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.Holiday;
import com.neta.teman.dawai.api.models.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long>, JpaSpecificationExecutor<Holiday> {

    Holiday findByDate(Date date);

    int countByDateBetweenAndDayNotIn(Date startDate, Date finishDate, List<Integer> asList);

    List<Holiday> findAllByDateGreaterThan(Date date);
}
