package com.neta.teman.dawai.api;

import com.neta.teman.dawai.api.applications.commons.DateTimeUtils;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.request.CutiRequest;
import com.neta.teman.dawai.api.models.repository.CutiRepository;
import com.neta.teman.dawai.api.models.repository.HolidayRepository;
import com.neta.teman.dawai.api.models.repository.RoleRepository;
import com.neta.teman.dawai.api.models.repository.UserRepository;
import com.neta.teman.dawai.api.services.CutiServices;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:test.properties")
class CutiTests {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CutiRepository cutiRepository;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    CutiServices cutiServices;


    private Cuti cutiIfnotExist(User user, Date start) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        calendar.add(Calendar.DATE, 3);
        Cuti cuti = new Cuti();
        cuti.setUser(user);
        cuti.setStartDate(DateTimeUtils.startOfDay(start));
        cuti.setFinishDate(DateTimeUtils.endOfDay(calendar.getTime()));
        cuti.setCutiStatus(AppConstants.Cuti.CREATED);
        // cuti.setTotalDays(new Long(TimeUnit.MILLISECONDS.toDays(Math.abs(cuti.getFinishDate().getTime() - cuti.getStartDate().getTime()))).intValue());
        Cuti existCuti = cutiRepository.findByUserAndStartDateAndFinishDateAndCutiStatusGreaterThan(user, cuti.getStartDate(), cuti.getFinishDate(), AppConstants.Cuti.CREATED);
        if (Objects.nonNull(existCuti)) return existCuti;
        return cutiRepository.save(cuti);
    }

    /**
     * buat user dahulu jika belum ada
     * buat data cuti jika belum ada
     */
    @BeforeAll
    void preparedData() {
        cutiServices.initCutiPegawai();
        TestAdminConstant.roleAnduserIfNotExist(roleRepository, userRepository);
    }

    @Test
    void excludeWeekend() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 23);
        Calendar calendarFinish = Calendar.getInstance();
        calendarFinish.set(Calendar.DATE, 23);
        int workingDays = DateTimeUtils.excludeWeekendOnly(calendar.getTime(), calendarFinish.getTime());
        calendar.set(Calendar.MONTH, 11);
        calendarFinish.set(Calendar.MONTH, 11);
        Assertions.assertEquals(1, workingDays);
        log.info("date without weekend {}", workingDays);
    }

    @Test
    void validationHolidayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 10);
        calendar.set(Calendar.DATE, 3);

        Calendar calendarF = Calendar.getInstance();
        calendarF.set(Calendar.MONTH, 10);
        calendarF.set(Calendar.DATE, 6);
        int workingDays = DateTimeUtils.excludeWeekendOnly(calendar.getTime(), calendarF.getTime());
        Assertions.assertEquals(4, workingDays);

        int count = holidayRepository.countByDateBetweenAndDayNotIn(calendar.getTime(), calendarF.getTime(), Arrays.asList(1, 2));
        Assertions.assertEquals(1, count);

        Assertions.assertEquals(3, workingDays-count);
    }

    @Test
    void validationDate() {
        boolean isBeforeOrEqual = DateTimeUtils.isBeforeAndEqualNow(new Date());
        Assertions.assertTrue(isBeforeOrEqual);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        isBeforeOrEqual = DateTimeUtils.isBeforeAndEqualNow(calendar.getTime());
        Assertions.assertTrue(isBeforeOrEqual);
    }

    /**
     * hari sama
     */
    @Test
    void validationDateExist() {
        User user = TestAdminConstant.roleAnduserIfNotExist(roleRepository, userRepository);
        Date cutiDate = new Date();
        Cuti cuti = cutiIfnotExist(user, DateTimeUtils.startOfDay(cutiDate));
        CutiRequest request = new CutiRequest();
        request.setNip(TestAdminConstant.NIP);
        request.setStartDate(cuti.getStartDate());
        request.setFinishDate(cuti.getFinishDate());
        // same date
        boolean isAvailable = cutiServices.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);

        // start date before
        request.setStartDate(null);
        request.setFinishDate(null);
        isAvailable = cutiServices.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);

        // start date before
        request.setStartDate(cuti.getFinishDate());
        request.setFinishDate(cuti.getStartDate());
        isAvailable = cutiServices.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);

        // start date between
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        request.setStartDate(cuti.getStartDate());
        request.setFinishDate(cutiDate);
        isAvailable = cutiServices.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);

        // finish date between
        request.setStartDate(cutiDate);
        request.setFinishDate(cuti.getFinishDate());
        isAvailable = cutiServices.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);

        // start before finish after start, finish after in exist db
        Calendar calendarFinish = Calendar.getInstance();
        calendarFinish.add(Calendar.DATE, 5);
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        request.setStartDate(calendar.getTime());
        request.setFinishDate(cuti.getFinishDate());
        isAvailable = cutiServices.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);

        // start before, finish after in exist db
        calendar.add(Calendar.DATE, -10);
        request.setStartDate(calendar.getTime());
        request.setFinishDate(cuti.getFinishDate());
        isAvailable = cutiServices.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);


    }


}
