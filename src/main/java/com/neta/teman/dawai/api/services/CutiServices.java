package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.Holiday;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.request.CutiRequest;
import com.neta.teman.dawai.api.models.payload.request.HolidayRequest;

import java.util.List;

@SuppressWarnings({"rawtypes"})
public interface CutiServices {

    ServiceResolver<Boolean> submitCuti(CutiRequest request);

    ServiceResolver<Integer> daysCuti(CutiRequest request);

    ServiceResolver<Boolean> cancelCuti(CutiRequest request);

    ServiceResolver<Boolean> approveAtasan(CutiRequest request);

    ServiceResolver<Boolean> approvePejabat(CutiRequest request);

    ServiceResolver<List<Cuti>> userCuti(CutiRequest request);

    ServiceResolver addHolidayDate(HolidayRequest request);

    ServiceResolver removeHolidayDate(HolidayRequest request);

    ServiceResolver<List<Holiday>> existHolidayDate();

    ServiceResolver<List<Holiday>> existHolidayDateFuture();

    boolean availableCutiDate(CutiRequest request);

    void initCutiPegawai();

    void updateCutiUser(User user, Integer countCuti);

    void addCutiUser(User user, Integer countCuti);

}
