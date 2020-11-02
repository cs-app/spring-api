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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.wildfly.common.Assert;

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
        TestAdminConstant.roleAnduserIfNotExist(roleRepository, userRepository);
    }

    @Test
    void excludeWeekend() {
        Calendar calendar = Calendar.getInstance();
        Calendar calendarFinish = Calendar.getInstance();
        calendarFinish.set(Calendar.DAY_OF_MONTH, calendarFinish.getActualMaximum(Calendar.DAY_OF_MONTH));
        int workingDays = DateTimeUtils.excludeWeekendOnly(calendar.getTime(), calendarFinish.getTime());
        log.info("date without weekend {}", workingDays);
    }

    @Test
    void validationHolidayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 4);
        int count = holidayRepository.countByDateBetweenAndDayNotIn(new Date(), calendar.getTime(), Arrays.asList(1, 2));
        log.info("total libur {}", count);
    }

    @Test
    void validationDate() {
        boolean isBeforeOrEqual = DateTimeUtils.isBeforeAndEqualNow(new Date());
        Assert.assertTrue(isBeforeOrEqual);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        isBeforeOrEqual = DateTimeUtils.isBeforeAndEqualNow(calendar.getTime());
        Assert.assertTrue(isBeforeOrEqual);
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
        Assert.assertFalse(isAvailable);

        // start date before
        request.setStartDate(null);
        request.setFinishDate(null);
        isAvailable = cutiServices.availableCutiDate(request);
        Assert.assertFalse(isAvailable);

        // start date before
        request.setStartDate(cuti.getFinishDate());
        request.setFinishDate(cuti.getStartDate());
        isAvailable = cutiServices.availableCutiDate(request);
        Assert.assertFalse(isAvailable);

        // start date between
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        request.setStartDate(cuti.getStartDate());
        request.setFinishDate(cutiDate);
        isAvailable = cutiServices.availableCutiDate(request);
        Assert.assertFalse(isAvailable);

        // finish date between
        request.setStartDate(cutiDate);
        request.setFinishDate(cuti.getFinishDate());
        isAvailable = cutiServices.availableCutiDate(request);
        Assert.assertFalse(isAvailable);

        // start before finish after start, finish after in exist db
        Calendar calendarFinish = Calendar.getInstance();
        calendarFinish.add(Calendar.DATE, 5);
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        request.setStartDate(calendar.getTime());
        request.setFinishDate(cuti.getFinishDate());
        isAvailable = cutiServices.availableCutiDate(request);
        Assert.assertFalse(isAvailable);

        // start before, finish after in exist db
        calendar.add(Calendar.DATE, -10);
        request.setStartDate(calendar.getTime());
        request.setFinishDate(cuti.getFinishDate());
        isAvailable = cutiServices.availableCutiDate(request);
        Assert.assertFalse(isAvailable);


    }


}
