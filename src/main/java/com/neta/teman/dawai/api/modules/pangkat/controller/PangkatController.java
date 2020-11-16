package com.neta.teman.dawai.api.modules.pangkat.controller;

import com.neta.teman.dawai.api.applications.base.BaseRestController;
import com.neta.teman.dawai.api.applications.base.Response;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.dao.PangkatGolongan;
import com.neta.teman.dawai.api.models.payload.request.DocumentRequest;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.request.PangkatGolonganRequest;
import com.neta.teman.dawai.api.models.payload.response.PageResponse;
import com.neta.teman.dawai.api.services.PangkatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/pangkat")
@SuppressWarnings({"unchecked"})
public class PangkatController extends BaseRestController {

    @Autowired
    PangkatService pangkatService;

    @PostMapping(value = "/page")
    public ResponseEntity<PageResponse> typePage(@RequestBody FilterRequest request) {
        ServiceResolver<Page<PangkatGolongan>> page = pangkatService.loadPage(request);
        return response(new PageResponse(page));
    }

    @PostMapping(value = "/document/merge")
    public ResponseEntity<Response> typeMerge(@RequestBody PangkatGolonganRequest request) {
        if (isNull(request.getId()) || request.getDocumentPangkat().size() == 0) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        return response(pangkatService.merge(request));
    }

}
