package com.neta.teman.dawai.api.modules.duk.controller;

import com.neta.teman.dawai.api.applications.base.BaseRestController;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.request.FilterDukRequest;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.response.DUKFIlterResponse;
import com.neta.teman.dawai.api.models.payload.response.PageResponse;
import com.neta.teman.dawai.api.services.DUKService;
import com.neta.teman.dawai.api.services.EmployeeService;
import com.neta.teman.dawai.api.services.ReportService;
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
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/duk")
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public class DUKController extends BaseRestController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    ReportService reportService;

    @Autowired
    DUKService dukService;

    @GetMapping(value = "/all")
    public ResponseEntity<List<Employee>> all() {
        ServiceResolver<List<Employee>> page = employeeService.loadAll();
        return response(page);
    }

    @PostMapping(value = "/page")
    public ResponseEntity<PageResponse> typePage(@RequestBody FilterDukRequest request) {
        ServiceResolver<Page<Employee>> page = employeeService.loadPage(request);
        return response(new PageResponse(page));
    }

    @GetMapping(value = "/filter/param")
    public ResponseEntity<DUKFIlterResponse> filterParam() {
        ServiceResolver<DUKFIlterResponse> page = dukService.filterParams();
        return response(page);
    }

    @GetMapping(value = "/download")
    public ResponseEntity<StreamingResponseBody> downloadDUK() {
        ServiceResolver<List<Employee>> resolver = employeeService.loadAll();
        if (resolver.isError()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String fileName = "duk.pdf";
        StreamingResponseBody stream = out -> reportService.printDUK(resolver.getResult(), out);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"")
                .body(stream);
    }

}
