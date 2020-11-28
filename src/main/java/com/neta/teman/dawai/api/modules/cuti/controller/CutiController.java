package com.neta.teman.dawai.api.modules.cuti.controller;

import com.neta.teman.dawai.api.applications.base.BaseRestController;
import com.neta.teman.dawai.api.applications.base.Response;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.payload.converter.CutiConverter;
import com.neta.teman.dawai.api.models.payload.converter.HolidayConverter;
import com.neta.teman.dawai.api.models.payload.request.CutiRequest;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.request.HolidayRequest;
import com.neta.teman.dawai.api.models.payload.response.CutiResponse;
import com.neta.teman.dawai.api.models.payload.response.HolidayResponse;
import com.neta.teman.dawai.api.models.payload.response.PageResponse;
import com.neta.teman.dawai.api.models.payload.response.UserCutiSummary;
import com.neta.teman.dawai.api.services.CutiService;
import com.neta.teman.dawai.api.services.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cuti")
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public class CutiController extends BaseRestController {

    @Autowired
    CutiService cutiService;

    @Autowired
    FileService fileService;

    @PostMapping(value = "/request")
    public ResponseEntity<Boolean> request(@RequestBody CutiRequest request) {
        if (isNull(request.getJenisCuti(), request.getNip(), request.getStartDate(), request.getFinishDate(), request.getDescription(),
                request.getTlpAddress(), request.getCutiAddress())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        request.setFileName(fileService.storeFile(request.getNip(), request.getFile(), request.getExt()));
        ServiceResolver<Boolean> resolver = cutiService.submitCuti(request);
        return response(resolver);
    }

    @GetMapping(value = "/download/lampiran/{id}")
    public ResponseEntity<StreamingResponseBody> downloadLampiran(@PathVariable Long id) {
//        ServiceResolver<Cuti> resolver = cutiService.findByCutiUserAndId(id);
//        if (resolver.isError()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//        String fileName = "duk.pdf";
//        StreamingResponseBody stream = out -> reportService.printDUK(resolver.getResult(), out);
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"")
//                .body(stream);
        return null;
    }

    @PostMapping(value = "/quota")
    public ResponseEntity<CutiSummary> quota(@RequestBody CutiRequest request) {
        if (isNull(request.getNip())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver<CutiSummary> resolver = cutiService.quota(request);
        return response(resolver);
    }

    @PostMapping(value = "/day")
    public ResponseEntity<Integer> cutiDays(@RequestBody CutiRequest request) {
        if (isNull(request.getStartDate(), request.getFinishDate())) {
            return response(0);
        }
        ServiceResolver<Integer> resolver = cutiService.daysCuti(request);
        return response(resolver);
    }

    @PostMapping(value = "/cancel")
    public ResponseEntity<User> cutiCancel(@RequestBody CutiRequest request) {
        if (isNull(request.getId())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver<Boolean> resolver = cutiService.cancelCuti(request);
        return response(resolver);
    }

    @PostMapping(value = "/approve/atasan")
    public ResponseEntity<User> cutiApproveAtasan(@RequestBody CutiRequest request) {
        if (isNull(request.getId())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver<Boolean> resolver = cutiService.approveAtasan(request);
        return response(resolver);
    }

    @PostMapping(value = "/approve/pejabat")
    public ResponseEntity<User> cutiApprovePejabat(@RequestBody CutiRequest request) {
        if (isNull(request.getId())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver<Boolean> resolver = cutiService.approvePejabat(request);
        return response(resolver);
    }

    @PostMapping(value = "/user")
    public ResponseEntity<List<CutiResponse>> cutiUser(@RequestBody CutiRequest request) {
        if (isNull(request.getNip())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver<List<Cuti>> resolver = cutiService.userCuti(request);
        return response(resolver, CutiConverter.class);
    }

    @PostMapping(value = "/user/approval")
    public ResponseEntity<List<CutiResponse>> cutiUserApproval(@RequestBody CutiRequest request) {
        if (isNull(request.getNip())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver<List<Cuti>> resolver = cutiService.cutiUserApproval(request);
        return response(resolver, CutiConverter.class);
    }

    @PostMapping(value = "/holiday/create")
    public ResponseEntity<Response> addHolidayDate(@RequestBody HolidayRequest request) {
        if (isNull(request.getDate(), request.getDescription())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver resolver = cutiService.addHolidayDate(request);
        return response(resolver);
    }

    @PostMapping(value = "/holiday/remove")
    public ResponseEntity<Response> removeHolidayDate(@RequestBody HolidayRequest request) {
        if (isNull(request.getId())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        ServiceResolver resolver = cutiService.removeHolidayDate(request);
        return response(resolver);
    }

    @GetMapping(value = "/holiday/list")
    public ResponseEntity<List<HolidayResponse>> existHolidayDate() {
        ServiceResolver<List<Holiday>> resolver = cutiService.existHolidayDate();
        return response(resolver, HolidayConverter.class);
    }

    @GetMapping(value = "/holiday/future")
    public ResponseEntity<List<HolidayResponse>> existHolidayDateFuture() {
        ServiceResolver<List<Holiday>> resolver = cutiService.existHolidayDateFuture();
        return response(resolver, HolidayConverter.class);
    }

    @PostMapping(value = "/page")
    public ResponseEntity<PageResponse> typePage(@RequestBody FilterRequest request) {
        ServiceResolver<Page<UserCutiSummary>> page = cutiService.userCutiSummary(request);
        return response(new PageResponse(page));
    }
}
