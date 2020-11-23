package com.neta.teman.dawai.api.modules.document.controller;

import com.neta.teman.dawai.api.applications.base.BaseRestController;
import com.neta.teman.dawai.api.applications.base.Response;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.EmployeeDocument;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.converter.HolidayConverter;
import com.neta.teman.dawai.api.models.payload.converter.UserDocumentConverter;
import com.neta.teman.dawai.api.models.payload.request.DocumentApprovalRequest;
import com.neta.teman.dawai.api.models.payload.request.DocumentRequest;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.response.PageResponse;
import com.neta.teman.dawai.api.models.payload.response.UserDocumentResponse;
import com.neta.teman.dawai.api.services.DocumentService;
import com.neta.teman.dawai.api.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/document")
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public class DocumentController extends BaseRestController {

    @Autowired
    DocumentService documentService;

    @Autowired
    UserService userService;

    @GetMapping(value = "/type/all")
    public ResponseEntity<List<Document>> typeList() {
        ServiceResolver<List<Document>> page = documentService.loadAll();
        return response(page);
    }

    @PostMapping(value = "/type/page")
    public ResponseEntity<PageResponse> typePage(@RequestBody FilterRequest request) {
        ServiceResolver<Page<Document>> page = documentService.loadPage(request);
        return response(new PageResponse(page));
    }

    @PostMapping(value = "/type/merge")
    public ResponseEntity<?> typeMerge(@RequestBody DocumentRequest request) {
        if (isNull(request.getName())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        return response(documentService.merge(BeanCopy.copy(request, Document.class)));
    }

    @PostMapping(value = "/type/remove")
    public ResponseEntity<?> typeRemove(@RequestBody DocumentRequest request) {
        if (isNull(request.getId())) {
            return responseError(401, ERROR_MANDATORY_FIELD);
        }
        return response(documentService.remove(BeanCopy.copy(request, Document.class)));
    }

    @GetMapping(value = "/pending")
    public ResponseEntity documentPending() {
        ServiceResolver<List<User>> resolver = userService.findAll();
        return response(resolver, UserDocumentConverter.class);
    }

    @PostMapping(value = "/approval")
    public ResponseEntity<Response> documentApproval(@RequestBody DocumentApprovalRequest request) {
        if (isNull(request.getId(), request.getApproval())) {
            return responseError(401, "Field is empty!");
        }
        if (1 == request.getApproval() && (Objects.isNull(request.getMessage()) || request.getMessage().isEmpty())) {
            return responseError(403, "Field keterangan is empty!");
        }
        ServiceResolver resolver = documentService.documentApprove(request.getId(), request.getApproval(), request.getMessage());
        return response(resolver);
    }

}
