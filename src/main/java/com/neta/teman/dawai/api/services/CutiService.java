package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.CutiSummary;
import com.neta.teman.dawai.api.models.dao.Holiday;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.request.CutiRequest;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.request.HolidayRequest;
import com.neta.teman.dawai.api.models.payload.response.UserCutiSummary;
import org.springframework.data.domain.Page;

import java.util.List;

@SuppressWarnings({"rawtypes"})
public interface CutiService {

    ServiceResolver<Boolean> submitCuti(CutiRequest request);

    ServiceResolver<Integer> daysCuti(CutiRequest request);

    ServiceResolver<Boolean> cancelCuti(CutiRequest request);

    ServiceResolver<Boolean> approveAtasan(CutiRequest request);

    ServiceResolver<Boolean> approvePejabat(CutiRequest request);

    ServiceResolver<List<Cuti>> userCuti(CutiRequest request);

    ServiceResolver<List<Cuti>> cutiUserApproval(CutiRequest request);

    ServiceResolver addHolidayDate(HolidayRequest request);

    ServiceResolver removeHolidayDate(HolidayRequest request);

    ServiceResolver<List<Holiday>> existHolidayDate();

    ServiceResolver<List<Holiday>> existHolidayDateFuture();

    boolean availableCutiDate(CutiRequest request);

    void initCutiPegawai();

    void updateCutiUser(User user, Integer countCuti);

    void addCutiUser(User user, Integer countCuti);

    ServiceResolver<Cuti> findByCutiUserAndId(User user, Long cutiId);

    ServiceResolver<Page<UserCutiSummary>> userCutiSummary(FilterRequest request);

    ServiceResolver<CutiSummary> quota(CutiRequest request);

}
