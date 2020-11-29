package com.neta.teman.dawai.api;

import com.neta.teman.dawai.api.applications.commons.DateTimeUtils;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.CutiSummary;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.request.CutiRequest;
import com.neta.teman.dawai.api.models.repository.*;
import com.neta.teman.dawai.api.services.CutiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
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
    CutiSummaryRepository cutiSummaryRepository;

    @Autowired
    CutiSummaryHistoryRepository cutiSummaryHistoryRepository;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    CutiService cutiService;


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
        cutiService.initCutiPegawai();
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

        Assertions.assertEquals(3, workingDays - count);
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
        boolean isAvailable = cutiService.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);

        // start date before
        request.setStartDate(null);
        request.setFinishDate(null);
        isAvailable = cutiService.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);

        // start date before
        request.setStartDate(cuti.getFinishDate());
        request.setFinishDate(cuti.getStartDate());
        isAvailable = cutiService.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);

        // start date between
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        request.setStartDate(cuti.getStartDate());
        request.setFinishDate(cutiDate);
        isAvailable = cutiService.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);

        // finish date between
        request.setStartDate(cutiDate);
        request.setFinishDate(cuti.getFinishDate());
        isAvailable = cutiService.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);

        // start before finish after start, finish after in exist db
        Calendar calendarFinish = Calendar.getInstance();
        calendarFinish.add(Calendar.DATE, 5);
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, 1);
        request.setStartDate(calendar.getTime());
        request.setFinishDate(cuti.getFinishDate());
        isAvailable = cutiService.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);

        // start before, finish after in exist db
        calendar.add(Calendar.DATE, -10);
        request.setStartDate(calendar.getTime());
        request.setFinishDate(cuti.getFinishDate());
        isAvailable = cutiService.availableCutiDate(request);
        Assertions.assertFalse(isAvailable);
    }

    @Test
    void hitungCuti() {
        int ambilCuti = 10;
        User user = TestAdminConstant.roleAnduserIfNotExist(roleRepository, userRepository);
        LocalDate localDate = LocalDate.now();
        CutiSummary summary = cutiSummaryRepository.findByUserAndTahun(user, localDate.getYear());
        if (Objects.isNull(summary)) {
            summary = new CutiSummary();
            summary.setTahun(localDate.getYear());
            summary.setUser(user);
            summary.setKuotaCuti(12);
            summary.setKuotaPastCuti(6);
            summary.setKuotaPastTwoCuti(6);
        } else {
            summary.setKuotaCuti(12);
            summary.setKuotaPastCuti(6);
            summary.setKuotaPastTwoCuti(6);
        }
        cutiSummaryRepository.save(summary);
        cutiService.updateCutiUser(user, ambilCuti);
        summary = cutiSummaryRepository.findByUserAndTahun(user, localDate.getYear());
        Assertions.assertEquals(0, summary.getKuotaPastTwoCuti());
        Assertions.assertEquals(2, summary.getKuotaPastCuti());
        Assertions.assertEquals(12, summary.getKuotaCuti());
        // reset cuti
        summary.setKuotaCuti(12);
        summary.setKuotaPastCuti(6);
        summary.setKuotaPastTwoCuti(6);
        cutiSummaryRepository.save(summary);

        cutiService.updateCutiUser(user, 12);
        summary = cutiSummaryRepository.findByUserAndTahun(user, localDate.getYear());
        Assertions.assertEquals(0, summary.getKuotaPastTwoCuti());
        Assertions.assertEquals(0, summary.getKuotaPastCuti());
        Assertions.assertEquals(12, summary.getKuotaCuti());

        summary.setKuotaCuti(12);
        summary.setKuotaPastCuti(6);
        summary.setKuotaPastTwoCuti(6);
        cutiSummaryRepository.save(summary);

        cutiService.updateCutiUser(user, 24);
        summary = cutiSummaryRepository.findByUserAndTahun(user, localDate.getYear());
        Assertions.assertEquals(0, summary.getKuotaPastTwoCuti());
        Assertions.assertEquals(0, summary.getKuotaPastCuti());
        Assertions.assertEquals(0, summary.getKuotaCuti());

        summary.setKuotaCuti(12);
        summary.setKuotaPastCuti(2);
        summary.setKuotaPastTwoCuti(0);
        cutiSummaryRepository.save(summary);

        cutiService.updateCutiUser(user, 3);
        summary = cutiSummaryRepository.findByUserAndTahun(user, localDate.getYear());
        Assertions.assertEquals(0, summary.getKuotaPastTwoCuti());
        Assertions.assertEquals(0, summary.getKuotaPastCuti());
        Assertions.assertEquals(11, summary.getKuotaCuti());

        summary.setKuotaCuti(12);
        summary.setKuotaPastCuti(6);
        summary.setKuotaPastTwoCuti(1);
        cutiSummaryRepository.save(summary);

        cutiService.updateCutiUser(user, 5);

        summary = cutiSummaryRepository.findByUserAndTahun(user, localDate.getYear());
        Assertions.assertEquals(0, summary.getKuotaPastTwoCuti());
        Assertions.assertEquals(2, summary.getKuotaPastCuti());
        Assertions.assertEquals(12, summary.getKuotaCuti());

        // INCREMENT CUTI
        cutiService.addCutiUser(user, 3);
        summary = cutiSummaryRepository.findByUserAndTahun(user, localDate.getYear());
        Assertions.assertEquals(0, summary.getKuotaPastTwoCuti());
        Assertions.assertEquals(5, summary.getKuotaPastCuti());
        Assertions.assertEquals(12, summary.getKuotaCuti());

        summary.setKuotaCuti(12);
        summary.setKuotaPastCuti(6);
        summary.setKuotaPastTwoCuti(6);
        cutiSummaryRepository.save(summary);

        cutiService.addCutiUser(user, 3);
        summary = cutiSummaryRepository.findByUserAndTahun(user, localDate.getYear());
        Assertions.assertEquals(6, summary.getKuotaPastTwoCuti());
        Assertions.assertEquals(6, summary.getKuotaPastCuti());
        Assertions.assertEquals(12, summary.getKuotaCuti());

        summary.setKuotaCuti(12);
        summary.setKuotaPastCuti(6);
        summary.setKuotaPastTwoCuti(1);
        cutiSummaryRepository.save(summary);

        cutiService.addCutiUser(user, 3);
        summary = cutiSummaryRepository.findByUserAndTahun(user, localDate.getYear());
        Assertions.assertEquals(4, summary.getKuotaPastTwoCuti());
        Assertions.assertEquals(6, summary.getKuotaPastCuti());
        Assertions.assertEquals(12, summary.getKuotaCuti());

        summary.setKuotaCuti(8);
        summary.setKuotaPastCuti(0);
        summary.setKuotaPastTwoCuti(0);
        cutiSummaryRepository.save(summary);

        cutiService.addCutiUser(user, 5);
        summary = cutiSummaryRepository.findByUserAndTahun(user, localDate.getYear());
        Assertions.assertEquals(0, summary.getKuotaPastTwoCuti());
        Assertions.assertEquals(1, summary.getKuotaPastCuti());
        Assertions.assertEquals(12, summary.getKuotaCuti());
    }

}
