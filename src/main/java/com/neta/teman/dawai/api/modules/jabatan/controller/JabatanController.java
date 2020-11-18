package com.neta.teman.dawai.api.modules.jabatan.controller;

import com.neta.teman.dawai.api.applications.base.BaseRestController;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.dao.Jabatan;
import com.neta.teman.dawai.api.models.payload.request.DocumentRequest;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.request.JabatanRequest;
import com.neta.teman.dawai.api.models.payload.response.JabatanMapResponse;
import com.neta.teman.dawai.api.models.payload.response.PageResponse;
import com.neta.teman.dawai.api.services.JabatanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/jabatan")
@SuppressWarnings({"unchecked"})
public class JabatanController extends BaseRestController {

    @Autowired
    JabatanService jabatanService;

    @GetMapping(value = "/all")
    public ResponseEntity<List<Jabatan>> all() {
        ServiceResolver<List<Jabatan>> page = jabatanService.loadAll();
        return response(page);
    }

    @GetMapping(value = "/all/map")
    public ResponseEntity<List<Jabatan>> allMap() {
        ServiceResolver<List<JabatanMapResponse>> page = jabatanService.loadAllMap();
        return response(page);
    }

    @PostMapping(value = "/page")
    public ResponseEntity<Jabatan> page(@RequestBody FilterRequest request) {
        ServiceResolver<Page<Jabatan>> page = jabatanService.loadPage(request);
        return response(new PageResponse(page));
    }

    @PostMapping(value = "/merge")
    public ResponseEntity<?> merge(@RequestBody JabatanRequest request) {
        if (isNull(request.getName())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        return response(jabatanService.merge(BeanCopy.copy(request, Jabatan.class)));
    }

    @PostMapping(value = "/remove")
    public ResponseEntity<?> remove(@RequestBody JabatanRequest request) {
        if (isNull(request.getId())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        return response(jabatanService.remove(BeanCopy.copy(request, Jabatan.class)));
    }

}
