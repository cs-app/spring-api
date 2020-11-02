package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.applications.commons.DateTimeUtils;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.Holiday;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.request.CutiRequest;
import com.neta.teman.dawai.api.models.payload.request.HolidayRequest;
import com.neta.teman.dawai.api.models.repository.CutiRepository;
import com.neta.teman.dawai.api.models.repository.HolidayRepository;
import com.neta.teman.dawai.api.models.repository.UserRepository;
import com.neta.teman.dawai.api.models.spech.UserSpecs;
import com.neta.teman.dawai.api.plugins.simpeg.services.SimpegServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CutiServicesImpl extends SimpegServiceImpl implements CutiServices {

    @Autowired
    CutiRepository cutiRepository;

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
        request.setStartDate(DateTimeUtils.startOfDay(request.getStartDate()));
        request.setFinishDate(DateTimeUtils.endOfDay(request.getFinishDate()));
        Cuti cuti = new Cuti();
        BeanCopy.copy(cuti, request);
        cuti.setUser(user);
        cuti.setCutiStatus(AppConstants.Cuti.CREATED);
        cuti.setTotalDays(DateTimeUtils.excludeWeekendOnly(request.getStartDate(), request.getFinishDate()) - holidayRepository.countByDateBetweenAndDayNotIn(request.getStartDate(), request.getFinishDate(), Arrays.asList(1, 7)));
        if (0 >= cuti.getTotalDays()) {
            return error(406, "Jumlah hari cuti kurang dari 1");
        }
        cutiRepository.save(signature(cuti, user));
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
}
