package com.neta.teman.dawai.api.modules.cuti.controllers;

import com.neta.teman.dawai.api.applications.base.BaseRestController;
import com.neta.teman.dawai.api.applications.base.Response;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.Holiday;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.converter.CutiConverter;
import com.neta.teman.dawai.api.models.payload.converter.HolidayConverter;
import com.neta.teman.dawai.api.models.payload.request.CutiRequest;
import com.neta.teman.dawai.api.models.payload.request.HolidayRequest;
import com.neta.teman.dawai.api.models.payload.response.CutiResponse;
import com.neta.teman.dawai.api.models.payload.response.HolidayResponse;
import com.neta.teman.dawai.api.services.CutiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public class CutiController extends BaseRestController {

    @Autowired
    CutiServices cutiServices;

    @PostMapping(value = "/cuti")
    public ResponseEntity<Boolean> cuti(@RequestBody CutiRequest request) {
        if (isNull(request.getJenisCuti(), request.getNip(), request.getStartDate(), request.getFinishDate(), request.getDescription(),
                request.getTlpAddress(), request.getCutiAddress())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver<Boolean> resolver = cutiServices.submitCuti(request);
        return response(resolver);
    }

    @PostMapping(value = "/cuti/day")
    public ResponseEntity<Integer> cutiDays(@RequestBody CutiRequest request) {
        if (isNull(request.getStartDate(), request.getFinishDate())) {
            return response(0);
        }
        ServiceResolver<Integer> resolver = cutiServices.daysCuti(request);
        return response(resolver);
    }

    @PostMapping(value = "/cuti/cancel")
    public ResponseEntity<User> cutiCancel(@RequestBody CutiRequest request) {
        if (isNull(request.getId())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver<Boolean> resolver = cutiServices.cancelCuti(request);
        return response(resolver);
    }

    @PostMapping(value = "/cuti/approve/atasan")
    public ResponseEntity<User> cutiApproveAtasan(@RequestBody CutiRequest request) {
        if (isNull(request.getId())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver<Boolean> resolver = cutiServices.approveAtasan(request);
        return response(resolver);
    }

    @PostMapping(value = "/cuti/approve/pejabat")
    public ResponseEntity<User> cutiApprovePejabat(@RequestBody CutiRequest request) {
        if (isNull(request.getId())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver<Boolean> resolver = cutiServices.approvePejabat(request);
        return response(resolver);
    }

    @PostMapping(value = "/cuti/user")
    public ResponseEntity<List<CutiResponse>> cutiUser(@RequestBody CutiRequest request) {
        if (isNull(request.getNip())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver<List<Cuti>> resolver = cutiServices.userCuti(request);
        return response(resolver, CutiConverter.class);
    }

    @PostMapping(value = "/cuti/holiday/create")
    public ResponseEntity<Response> addHolidayDate(@RequestBody HolidayRequest request) {
        if (isNull(request.getDate(), request.getDescription())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver resolver = cutiServices.addHolidayDate(request);
        return response(resolver);
    }

    @PostMapping(value = "/cuti/holiday/remove")
    public ResponseEntity<Response> removeHolidayDate(@RequestBody HolidayRequest request) {
        if (isNull(request.getId())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver resolver = cutiServices.removeHolidayDate(request);
        return response(resolver);
    }

    @GetMapping(value = "/cuti/holiday/list")
    public ResponseEntity<List<HolidayResponse>> existHolidayDate() {
        ServiceResolver<List<Holiday>> resolver = cutiServices.existHolidayDate();
        return response(resolver, HolidayConverter.class);
    }

    @GetMapping(value = "/cuti/holiday/future")
    public ResponseEntity<List<HolidayResponse>> existHolidayDateFuture() {
        ServiceResolver<List<Holiday>> resolver = cutiServices.existHolidayDateFuture();
        return response(resolver, HolidayConverter.class);
    }
}
