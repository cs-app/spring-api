package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.applications.commons.DateTimeUtils;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.payload.request.CutiRequest;
import com.neta.teman.dawai.api.models.payload.request.HolidayRequest;
import com.neta.teman.dawai.api.models.repository.*;
import com.neta.teman.dawai.api.models.spech.UserSpecs;
import com.neta.teman.dawai.api.plugins.simpeg.services.SimpegServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class CutiServicesImpl extends SimpegServiceImpl implements CutiServices {

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

    @Override
    public ServiceResolver<Boolean> submitCuti(CutiRequest request) {
        User user = userRepository.findOne(UserSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) {
            return error(403, USER_NOT_EXIST);
        }
        if (!availableCutiDate(request)) {
            return error(404, "Already exist date");
        }
        if (DateTimeUtils.isBeforeAndEqualNow(request.getStartDate())) {
            return error(405, "Start date past!");
        }
        CutiSummary cutiSummary = cutiSummaryRepository.findByUser(user);
        if (cutiSummary.getKuotaCuti() == 0 && cutiSummary.getKuotaPastCuti() == 0) {
            return error(406, "Anda tidak memili sisa cuti");
        }
        Cuti cuti = new Cuti();
        BeanCopy.copy(cuti, request);
        cuti.setUser(user);
        cuti.setCutiStatus(AppConstants.Cuti.CREATED);
        cuti.setTotalDays(DateTimeUtils.excludeWeekendOnly(request.getStartDate(), request.getFinishDate()) - holidayRepository.countByDateBetweenAndDayNotIn(request.getStartDate(), request.getFinishDate(), Arrays.asList(1, 7)));
        if ((cutiSummary.getKuotaCuti() + cutiSummary.getKuotaPastCuti()) < cuti.getTotalDays()) {
            return error(406, "Jumlah maksimal cuti anda " + (cutiSummary.getKuotaCuti() + cutiSummary.getKuotaPastCuti()) + " hari");
        }
        if (0 >= cuti.getTotalDays()) {
            return error(406, "Jumlah hari cuti kurang dari 1");
        }
        cutiRepository.save(signature(cuti, user));
        updateCutiUser(user, cuti.getTotalDays());
        return success(true);
    }

    @Override
    public ServiceResolver<Integer> daysCuti(CutiRequest request) {
        int days = DateTimeUtils.excludeWeekendOnly(request.getStartDate(), request.getFinishDate()) - holidayRepository.countByDateBetweenAndDayNotIn(request.getStartDate(), request.getFinishDate(), Arrays.asList(1, 7));
        return success(days);
    }

    @Override
    public ServiceResolver<Boolean> cancelCuti(CutiRequest request) {
        User user = userRepository.findOne(UserSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) {
            return error(403, USER_NOT_EXIST);
        }
        Cuti cuti = cutiRepository.findById(request.getId()).orElse(null);
        if (Objects.isNull(cuti)) {
            return success(false);
        }
        cuti.setCutiStatus(AppConstants.Cuti.CANCEL);
        cutiRepository.save(signature(cuti, user));
        addCutiUser(user, cuti.getTotalDays());
        return success(true);
    }

    @Override
    public ServiceResolver<Boolean> approveAtasan(CutiRequest request) {
        User user = userRepository.findOne(UserSpecs.nip(request.getNip())).orElse(null);
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
        if (request.getApproveStatus() <= 2) {
            addCutiUser(user, cuti.getTotalDays());
        }
        return success(true);
    }

    @Override
    public ServiceResolver<Boolean> approvePejabat(CutiRequest request) {
        User user = userRepository.findOne(UserSpecs.nip(request.getNip())).orElse(null);
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
        if (request.getApproveStatus() <= 2) {
            addCutiUser(user, cuti.getTotalDays());
        }
        return success(true);
    }

    @Override
    public ServiceResolver<List<Cuti>> userCuti(CutiRequest request) {
        User user = userRepository.findOne(UserSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) {
            return error(403, USER_NOT_EXIST);
        }
        List<Cuti> cutis = cutiRepository.findAllByUser(user);
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
        User user = userRepository.findOne(UserSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) return false;
        Cuti cuti = cutiRepository.findByUserAndStartDateAndFinishDateAndCutiStatusGreaterThan(user, request.getStartDate(), request.getFinishDate(), AppConstants.Cuti.REJECTED);
        if (Objects.nonNull(cuti)) return false;
        long countStart = cutiRepository.countByUserAndStartDateBetweenAndCutiStatusGreaterThan(user, request.getStartDate(), request.getFinishDate(), AppConstants.Cuti.REJECTED);
        if (0 < countStart) return false;
        long countFinish = cutiRepository.countByUserAndFinishDateBetweenAndCutiStatusGreaterThan(user, request.getStartDate(), request.getFinishDate(), AppConstants.Cuti.REJECTED);
        return 0 == countFinish;
    }

    @Override
    public void initCutiPegawai() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        List<User> users = userRepository.findAll();
        for (User o : users) {
            CutiSummary cutiSummaryExist = cutiSummaryRepository.findByUser(o);
            if (Objects.nonNull(cutiSummaryExist)) {
                if (month > 5 && cutiSummaryExist.getSisaPastCuti() > 0) {
                    cutiSummaryExist.setKuotaPastCuti(0);
                    cutiSummaryExist.setSisaPastCuti(0);
                    cutiSummaryRepository.save(signature(cutiSummaryExist));
                    CutiSummaryHistory history = BeanCopy.copy(cutiSummaryExist, CutiSummaryHistory.class);
                    history.setId(null);
                    history.setUser(o);
                    cutiSummaryHistoryRepository.save(signature(history));
                    log.debug("create cuti for user {}", o.getEmployee().getNip());
                } else {
                    // migrasi data cuti, jan ke mar
                    if (cutiSummaryExist.getTahun() < year && cutiSummaryExist.getSisaPastCuti() > 0) {
                        cutiSummaryExist.setTahun(year);
                        int maxSisa = cutiSummaryExist.getSisaPastCuti() > 6 ? 6 : cutiSummaryExist.getSisaPastCuti();
                        cutiSummaryExist.setKuotaPastCuti(maxSisa);
                        cutiSummaryExist.setSisaPastCuti(0);
                        cutiSummaryRepository.save(signature(cutiSummaryExist));
                        CutiSummaryHistory history = BeanCopy.copy(cutiSummaryExist, CutiSummaryHistory.class);
                        history.setId(null);
                        history.setUser(o);
                        cutiSummaryHistoryRepository.save(signature(history));
                    }
                }
                continue;
            }
            CutiSummary cutiSummary = new CutiSummary();
            cutiSummary.setTahun(year);
            cutiSummary.setUser(o);
            cutiSummary.setKuotaCuti(12);
            cutiSummary.setKuotaPastCuti(0);
            cutiSummary.setSisaCuti(0);
            cutiSummary.setSisaPastCuti(0);
            cutiSummaryRepository.save(signature(cutiSummary));
            CutiSummaryHistory history = BeanCopy.copy(cutiSummary, CutiSummaryHistory.class);
            history.setId(null);
            history.setUser(o);
            cutiSummaryHistoryRepository.save(signature(history));
            log.debug("create cuti for user {}", o.getEmployee().getNip());
        }
    }

    @Override
    public void updateCutiUser(User user, Integer countCuti) {
        CutiSummary cutiSummary = cutiSummaryRepository.findByUser(user);
        // ambil dari sisa terlebih dahulu
        // int cuti yang diambil
        if (cutiSummary.getSisaPastCuti() >= countCuti) {
            // ambil dari sisa
            cutiSummary.setKuotaPastCuti(cutiSummary.getSisaPastCuti() - countCuti);
        } else {
            // sisa cuti kurang, ambil dari main cuti
            int newCutiCount = countCuti - cutiSummary.getSisaPastCuti();
            cutiSummary.setKuotaPastCuti(0);
            cutiSummary.setKuotaCuti(cutiSummary.getKuotaCuti() - newCutiCount);
        }
        cutiSummaryRepository.save(cutiSummary);
        CutiSummaryHistory history = BeanCopy.copy(cutiSummary, CutiSummaryHistory.class);
        history.setId(null);
        history.setUser(user);
        cutiSummaryHistoryRepository.save(signature(history));
    }

    @Override
    public void addCutiUser(User user, Integer countCuti) {
        Calendar calendar = Calendar.getInstance();

        int month = calendar.get(Calendar.MONTH);
        CutiSummary cutiSummary = cutiSummaryRepository.findByUser(user);
        if (month > 5) {
            cutiSummary.setKuotaCuti(cutiSummary.getKuotaCuti() + countCuti);
        } else {
            if (cutiSummary.getKuotaCuti() + countCuti <= 12) {
                cutiSummary.setKuotaCuti(cutiSummary.getKuotaCuti() + countCuti);
            } else {
                // penambahan du kuota cuti
                int maxKuotaCUti = 12 - cutiSummary.getKuotaCuti();
                cutiSummary.setKuotaCuti(12);
                cutiSummary.setKuotaPastCuti(countCuti - maxKuotaCUti);
            }
        }
        cutiSummaryRepository.save(cutiSummary);
        CutiSummaryHistory history = BeanCopy.copy(cutiSummary, CutiSummaryHistory.class);
        history.setId(null);
        history.setUser(user);
        cutiSummaryHistoryRepository.save(signature(history));
    }
}
