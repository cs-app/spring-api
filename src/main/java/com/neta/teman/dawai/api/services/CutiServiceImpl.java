package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.applications.commons.DTFomat;
import com.neta.teman.dawai.api.applications.commons.DateTimeUtils;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.payload.request.CutiRequest;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.request.HolidayRequest;
import com.neta.teman.dawai.api.models.payload.response.UserCutiSummary;
import com.neta.teman.dawai.api.models.repository.*;
import com.neta.teman.dawai.api.models.spech.UserSpecs;
import com.neta.teman.dawai.api.plugins.simpeg.services.SimpegServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class CutiServiceImpl extends SimpegServiceImpl implements CutiService {

    @Autowired
    CutiRepository cutiRepository;

    @Autowired
    CutiSummaryRepository cutiSummaryRepository;

    @Autowired
    CutiSummaryHistoryRepository cutiSummaryHistoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    HolidayRepository holidayRepository;

    @Autowired
    UserSpecs userSpecs;

    @Override
    public ServiceResolver<Boolean> submitCuti(CutiRequest request) {
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) {
            return error(403, USER_NOT_EXIST);
        }
        if (!availableCutiDate(request)) {
            return error(404, "Already exist date");
        }
        if (DateTimeUtils.isBeforeAndEqualNow(request.getStartDate())) {
            return error(405, "Start date past!");
        }
        LocalDate today = LocalDate.now();
        CutiSummary cutiSummary = cutiSummaryRepository.findByUserAndTahun(user, today.getYear());
        Cuti cuti = new Cuti();
        BeanCopy.copy(cuti, request);
        cuti.setUser(user);
        cuti.setCutiStatus(AppConstants.Cuti.CREATED);
        cuti.setTotalDays(DateTimeUtils.excludeWeekendOnly(request.getStartDate(), request.getFinishDate()) - holidayRepository.countByDateBetweenAndDayNotIn(request.getStartDate(), request.getFinishDate(), Arrays.asList(1, 7)));
        int maxCuti = (cutiSummary.getKuotaCuti() + cutiSummary.getKuotaPastCuti() + cutiSummary.getKuotaPastTwoCuti());
        if (maxCuti <= 0) {
            return error(406, "Anda tidak memili sisa cuti");
        }
        if (maxCuti > 18) {
            return error(406, "Jumlah maksimal cuti anda 18 hari");
        }
        if (maxCuti < cuti.getTotalDays()) {
            return error(406, "Jumlah maksimal cuti anda " + (maxCuti) + " hari");
        }
        if (0 >= cuti.getTotalDays()) {
            return error(406, "Jumlah hari cuti kurang dari 1");
        }
        cutiRepository.save(signature(cuti, user));
        log.info("cuti user {}, jenis cuti yang di ambil {}", user.getEmployee().getNama(), cuti.getJenisCuti());
        if (request.getJenisCuti() <= 3) {
            updateCutiUser(user, cuti.getTotalDays());
        }
        return success(true);
    }

    @Override
    public ServiceResolver<Integer> daysCuti(CutiRequest request) {
        int days = DateTimeUtils.excludeWeekendOnly(request.getStartDate(), request.getFinishDate()) - holidayRepository.countByDateBetweenAndDayNotIn(request.getStartDate(), request.getFinishDate(), Arrays.asList(1, 7));
        return success(days);
    }

    @Override
    public ServiceResolver<Boolean> cancelCuti(CutiRequest request) {
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) {
            return error(403, USER_NOT_EXIST);
        }
        Cuti cuti = cutiRepository.findById(request.getId()).orElse(null);
        if (Objects.isNull(cuti)) {
            return success(false);
        }
        cuti.setCutiStatus(AppConstants.Cuti.CANCEL);
        cutiRepository.save(signature(cuti, user));
        if (cuti.getJenisCuti() <= 3) addCutiUser(user, cuti.getTotalDays());
        return success(true);
    }

    @Override
    public ServiceResolver<Boolean> approveAtasan(CutiRequest request) {
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) {
            return error(403, USER_NOT_EXIST);
        }
        Cuti cuti = cutiRepository.findById(request.getId()).orElse(null);
        if (Objects.isNull(cuti)) {
            return success(false);
        }
        cuti.setCutiStatus(request.getApproveStatus());
        cuti.setAtasan(user);
        cuti.setApproveAtasanDate(new Date());
        cuti.setDescriptionAtasan(request.getDescriptionApprove());
        cutiRepository.save(signature(cuti, user));
        if (request.getApproveStatus() <= 2 && cuti.getJenisCuti() <= 3) {
            addCutiUser(user, cuti.getTotalDays());
        }
        return success(true);
    }

    @Override
    public ServiceResolver<Boolean> approvePejabat(CutiRequest request) {
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) {
            return error(403, USER_NOT_EXIST);
        }
        Cuti cuti = cutiRepository.findById(request.getId()).orElse(null);
        if (Objects.isNull(cuti)) {
            return success(false);
        }
        cuti.setCutiStatus(request.getApproveStatus());
        cuti.setPejabat(user);
        cuti.setApprovePejabatDate(new Date());
        cuti.setDescriptionPejabat(request.getDescriptionApprove());
        cutiRepository.save(signature(cuti, user));
        if (request.getApproveStatus() <= 2 && cuti.getJenisCuti() <= 3) {
            addCutiUser(user, cuti.getTotalDays());
        }
        return success(true);
    }

    @Override
    public ServiceResolver<List<Cuti>> userCuti(CutiRequest request) {
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) {
            return error(403, USER_NOT_EXIST);
        }
        List<Cuti> cutis = cutiRepository.findAllByUser(user);
        cutis.sort(Comparator.comparing(Cuti::getStartDate));
        return success(cutis);
    }

    @Override
    public ServiceResolver<List<Cuti>> cutiUserApproval(CutiRequest request) {
        List<Cuti> cutis = cutiRepository.findAll();
        cutis.sort(Comparator.comparing(Cuti::getCreatedDate));
        return success(cutis);
    }

    @Override
    public ServiceResolver addHolidayDate(HolidayRequest request) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(request.getDate());
        Holiday holiday = holidayRepository.findByDate(request.getDate());
        if (Objects.nonNull(holiday)) {
            holiday.setDescription(request.getDescription());
            holiday.setDay(calendar.get(Calendar.DAY_OF_WEEK));
            holidayRepository.save(holiday);
        } else {
            holiday = BeanCopy.copy(request, Holiday.class);
            holiday.setDay(calendar.get(Calendar.DAY_OF_WEEK));
            holidayRepository.save(holiday);
        }
        return success();
    }

    @Override
    public ServiceResolver removeHolidayDate(HolidayRequest request) {
        Holiday holiday = holidayRepository.findById(request.getId()).orElse(null);
        if (Objects.nonNull(holiday)) {
            holidayRepository.delete(holiday);
        }
        return success();
    }

    @Override
    public ServiceResolver<List<Holiday>> existHolidayDate() {
        return success(holidayRepository.findAll());
    }

    @Override
    public ServiceResolver<List<Holiday>> existHolidayDateFuture() {
        return success(holidayRepository.findAllByDateGreaterThan(new Date()));
    }

    @Override
    public boolean availableCutiDate(CutiRequest request) {
        if (isNull(request.getStartDate(), request.getFinishDate())) return false;
        if (request.getFinishDate().before(request.getStartDate())) return false;
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) return false;
        Cuti cuti = cutiRepository.findByUserAndStartDateAndFinishDateAndCutiStatusGreaterThan(user, request.getStartDate(), request.getFinishDate(), AppConstants.Cuti.REJECTED);
        if (Objects.nonNull(cuti)) return false;
        long countStart = cutiRepository.countByUserAndStartDateBetweenAndCutiStatusGreaterThan(user, request.getStartDate(), request.getFinishDate(), AppConstants.Cuti.REJECTED);
        if (0 < countStart) return false;
        long countFinish = cutiRepository.countByUserAndFinishDateBetweenAndCutiStatusGreaterThan(user, request.getStartDate(), request.getFinishDate(), AppConstants.Cuti.REJECTED);
        return 0 == countFinish;
    }

    @Transactional
    @Override
    public void initCutiPegawai() {
        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();
        for (User o : users) {
            CutiSummary cutiSummaryExist = cutiSummaryRepository.findByUserAndTahun(o, today.getYear());
            // kalo kosong migrasiin
            if (Objects.nonNull(cutiSummaryExist)) {
                // clone
                if (cutiSummaryExist.getTahun() < today.getYear()) {
                    CutiSummary cloneCuti = BeanCopy.copy(cutiSummaryExist, CutiSummary.class);
                    int lastYear = cloneCuti.getKuotaCuti();
                    int last1Year = cloneCuti.getKuotaPastCuti();
                    cutiSummaryExist.setTahun(today.getYear());
                    cutiSummaryExist.setKuotaCuti(12);
                    cutiSummaryExist.setKuotaPastCuti(Math.min(lastYear, 6));
                    cutiSummaryExist.setKuotaPastTwoCuti(Math.min(last1Year, 6));
                    cutiSummaryRepository.save(signature(cutiSummaryExist));
                    CutiSummaryHistory history = BeanCopy.copy(cutiSummaryExist, CutiSummaryHistory.class);
                    history.setId(null);
                    history.setUser(o);
                    cutiSummaryHistoryRepository.save(signature(history));
                }
                continue;
            }
            CutiSummary cutiSummary = new CutiSummary();
            cutiSummary.setTahun(today.getYear());
            cutiSummary.setUser(o);
            cutiSummary.setKuotaCuti(12);
            cutiSummary.setKuotaPastCuti(0);
            cutiSummary.setKuotaPastTwoCuti(0);
            cutiSummaryRepository.save(signature(cutiSummary));
            CutiSummaryHistory history = BeanCopy.copy(cutiSummary, CutiSummaryHistory.class);
            history.setId(null);
            history.setUser(o);
            cutiSummaryHistoryRepository.save(signature(history));
            log.debug("create cuti for user {}", o.getEmployee().getNip());
        }
        log.info("finish initialize cuti user");
    }

    @Override
    public void updateCutiUser(User user, Integer countCuti) {
        if (countCuti <= 0) return;
        LocalDate today = LocalDate.now();
        CutiSummary cutiSummary = cutiSummaryRepository.findByUserAndTahun(user, today.getYear());
        if (Objects.isNull(cutiSummary)) return;
        int year2Ago = cutiSummary.getKuotaPastTwoCuti();
        int year1Ago = cutiSummary.getKuotaPastCuti();
        int yearActual = cutiSummary.getKuotaCuti();
        int year2AgoMinus = 0;
        int year1AgoMinus = 0;
        int yearActualMinus = 0;
        // 2 tahun
        for (int i = 0; i < year2Ago; i++) {
            --countCuti;
            year2AgoMinus++;
            if (countCuti <= 0) break;
        }
        // 1 tahun
        if (countCuti > 0) {
            for (int i = 0; i < year1Ago; i++) {
                --countCuti;
                year1AgoMinus++;
                if (countCuti <= 0) break;
            }
        }
        // 0 tahun
        if (countCuti > 0) {
            for (int i = 0; i < yearActual; i++) {
                --countCuti;
                yearActualMinus++;
                if (countCuti <= 0) break;
            }
        }
        cutiSummary.setKuotaPastTwoCuti(year2Ago - year2AgoMinus);
        cutiSummary.setKuotaPastCuti(year1Ago - year1AgoMinus);
        cutiSummary.setKuotaCuti(yearActual - yearActualMinus);
        CutiSummaryHistory history = BeanCopy.copy(cutiSummary, CutiSummaryHistory.class);
        history.setId(null);
        history.setUser(user);
        cutiSummaryRepository.save(cutiSummary);
        cutiSummaryHistoryRepository.save(signature(history));
    }

    @Override
    public void addCutiUser(User user, Integer countCuti) {
        if (countCuti <= 0) return;
        LocalDate today = LocalDate.now();
        CutiSummary cutiSummary = cutiSummaryRepository.findByUserAndTahun(user, today.getYear());
        if (Objects.isNull(cutiSummary)) return;
        int yearActual = cutiSummary.getKuotaCuti();
        int year1Ago = cutiSummary.getKuotaPastCuti();
        int year2Ago = cutiSummary.getKuotaPastTwoCuti();
        for (int i = 0; i < countCuti; i++) {
            if (yearActual < 12) {
                yearActual++;
                continue;
            }
            if (year1Ago < 6) {
                year1Ago++;
                continue;
            }
            if (year2Ago < 6) {
                year2Ago++;
            }
        }
        cutiSummary.setKuotaCuti(yearActual);
        cutiSummary.setKuotaPastCuti(year1Ago);
        cutiSummary.setKuotaPastTwoCuti(year2Ago);
        cutiSummaryRepository.save(cutiSummary);
        CutiSummaryHistory history = BeanCopy.copy(cutiSummary, CutiSummaryHistory.class);
        history.setId(null);
        history.setUser(user);
        cutiSummaryRepository.save(cutiSummary);
        cutiSummaryHistoryRepository.save(signature(history));
//        LocalDate today = LocalDate.now();
//        CutiSummary cutiSummary = cutiSummaryRepository.findByUserAndTahun(user, today.getYear());
//        CutiSummary cutiSummaryClone = BeanCopy.copy(cutiSummary, CutiSummary.class);
//        // rollback to kuota
//        if (cutiSummary.getKuotaCuti() + countCuti <= 12) {
//            cutiSummary.setKuotaCuti(cutiSummaryClone.getKuotaCuti() + countCuti);
//            cutiSummaryRepository.save(cutiSummary);
//            CutiSummaryHistory history = BeanCopy.copy(cutiSummary, CutiSummaryHistory.class);
//            history.setId(null);
//            history.setUser(user);
//            cutiSummaryHistoryRepository.save(signature(history));
//        }
//        if (cutiSummary.getKuotaCuti() + countCuti > 12) {
//            int sisaQuotaBerjalan = 12 - cutiSummary.getKuotaCuti();
//            cutiSummary.setKuotaCuti(12);
//            // check tahun lalu
//            if (cutiSummary.getKuotaPastCuti() + sisaQuotaBerjalan <= 6) {
//                int sisaQuota1Berjalan = 6 - cutiSummary.getKuotaPastCuti();
//                cutiSummary.setKuotaPastCuti(cutiSummaryClone.getKuotaPastCuti() + sisaQuota1Berjalan);
//                cutiSummaryRepository.save(cutiSummary);
//                CutiSummaryHistory history = BeanCopy.copy(cutiSummary, CutiSummaryHistory.class);
//                history.setId(null);
//                history.setUser(user);
//                cutiSummaryHistoryRepository.save(signature(history));
//                return;
//            }
//            if (cutiSummary.getKuotaPastCuti() + sisaQuotaBerjalan > 6) {
//                // sisa maksimal
//                int sisaQuota1Berjalan = 6 - cutiSummary.getKuotaPastCuti();
//                int sisaQuota2Berjalan = sisaQuotaBerjalan - sisaQuota1Berjalan;
//                cutiSummary.setKuotaPastCuti(6);
//                cutiSummary.setKuotaPastTwoCuti(Math.min(sisaQuota2Berjalan, 6));
//                cutiSummaryRepository.save(cutiSummary);
//                CutiSummaryHistory history = BeanCopy.copy(cutiSummary, CutiSummaryHistory.class);
//                history.setId(null);
//                history.setUser(user);
//                cutiSummaryHistoryRepository.save(signature(history));
//            }
//        }

    }

    @Override
    public ServiceResolver<Cuti> findByCutiUserAndId(User user, Long cutiId) {
        Cuti cuti = cutiRepository.findByUserAndId(user, cutiId);
        if (Objects.isNull(cuti)) return error(404, "cuti not found");
        return success(cuti);
    }

    @Override
    public ServiceResolver<Page<UserCutiSummary>> userCutiSummary(FilterRequest request) {
        LocalDate today = LocalDate.now();
        List<UserCutiSummary> summaries = new ArrayList<>();
        Page<User> users = userRepository.findAll(request.pageRequest());
        for (User user : users) {
            CutiSummary cutiSummary = cutiSummaryRepository.findByUserAndTahun(user, today.getYear());
            UserCutiSummary userCutiSummary = new UserCutiSummary();
            userCutiSummary.setUser(user);
            userCutiSummary.setCutiSummary(cutiSummary);
            // counter cuti by month
            List<UserCutiSummary.HisCount> hisCounts = new ArrayList<>();
            Calendar start = Calendar.getInstance();
            Calendar finish = Calendar.getInstance();
            finish.clear(Calendar.MILLISECOND);
            start.clear(Calendar.MILLISECOND);
            start.set(Calendar.DAY_OF_MONTH, 1);
            for (int i = 0; i < 12; i++) {
                if (i == 0) {
                    start.set(Calendar.MONTH, 0);
                    finish.set(Calendar.MONTH, 0);
                } else {
                    start.add(Calendar.MONTH, 1);
                    finish.add(Calendar.MONTH, 1);
                }
                finish.set(Calendar.DAY_OF_MONTH, start.getActualMaximum(Calendar.DAY_OF_MONTH));
                log.info("finish {}", DTFomat.format(finish.getTime()));
                UserCutiSummary.HisCount count = new UserCutiSummary.HisCount();
                count.setMonth(i);
                int countUserCuti = 0;
                List<Cuti> cutis = cutiRepository.findAllByUserAndStartDateBetween(user, start.getTime(), finish.getTime());
                for (Cuti cuti : cutis) {
                    if (cuti.getCutiStatus() >= 3) {
                        countUserCuti += cuti.getTotalDays();
                    }
                }
                count.setCount(countUserCuti);
                hisCounts.add(count);
            }
            userCutiSummary.setHisCount(hisCounts);
            summaries.add(userCutiSummary);
        }
        return success(new PageImpl<>(summaries, users.getPageable(), users.getTotalElements()));
    }

    @Override
    public ServiceResolver<CutiSummary> quota(CutiRequest request) {
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) {
            return error(401, "user not exist");
        }
        Calendar calendar = Calendar.getInstance();
        CutiSummary cutiSummary = cutiSummaryRepository.findByUserAndTahun(user, calendar.get(Calendar.YEAR));
        return success(cutiSummary);
    }
}
