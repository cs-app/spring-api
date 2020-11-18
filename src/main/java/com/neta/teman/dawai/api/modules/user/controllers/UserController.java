package com.neta.teman.dawai.api.modules.user.controllers;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.neta.teman.dawai.api.applications.base.BaseRestController;
import com.neta.teman.dawai.api.applications.base.Response;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.DTFomat;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.request.LoginRequest;
import com.neta.teman.dawai.api.models.payload.request.UploadRequest;
import com.neta.teman.dawai.api.models.payload.request.UserPangkatRequest;
import com.neta.teman.dawai.api.models.payload.response.UserResponse;
import com.neta.teman.dawai.api.models.payload.response.PageResponse;
import com.neta.teman.dawai.api.services.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/user")
@SuppressWarnings({"unchecked"})
public class UserController extends BaseRestController {

    @Autowired
    UserService userService;

    @Autowired
    CutiService cutiService;

    @Autowired
    ReportService reportService;

    @Autowired
    FileService fileService;

    @Autowired
    DocumentService documentService;

    @PostMapping(value = "/login")
    public ResponseEntity<User> auth(@RequestBody LoginRequest request) {
        if (isNull(request.getUsername(), request.getPassword())) {
            return responseError(401, "Username or password is empty!");
        }
        ServiceResolver<User> resolver = userService.findByUsernameAndPasswordSimpeg(request.getUsername().trim(), request.getPassword().trim());
        if (resolver.isError()) return responseError(resolver);
        return response(new UserResponse(resolver.getResult()));
    }

    @PostMapping(value = "/page")
    public ResponseEntity<PageResponse> page(@RequestBody FilterRequest request) {
        ServiceResolver<Page<User>> resolver = userService.loadPage(request);
        // map to page
        List<UserResponse> userResponses = new ArrayList<>();
        Page page = resolver.getResult();
        List<User> users = page.getContent();
        for (User user : users) {
            userResponses.add(new UserResponse(user));
        }
        Page<UserResponse> responsePage = new PageImpl<>(userResponses);
        return response(new PageResponse(responsePage));
    }

    @PostMapping(value = "/list/golongan")
    public ResponseEntity<PageResponse> listByGolongan(@RequestBody FilterRequest request) {
        ServiceResolver<List<User>> resolver = userService.findAllByGolongan(request);
        // map to page
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : resolver.getResult()) {
            userResponses.add(new UserResponse(user));
        }
        return response(userResponses);
    }

    @GetMapping(value = "/history/pangkat/{nip}")
    public ResponseEntity<PageResponse> HistoryPangkat(@PathVariable String nip) {
        if (isNull(nip)) {
            return responseError(401, "empty nip");
        }
        ServiceResolver<User> resolver = userService.findByNip(nip);
        if (resolver.isError()) return responseError(resolver);
        if (resolver.isError()) return responseError(resolver);
        return response(resolver.getResult().getEmployee().getPangkats());
    }

    @PostMapping(value = "/document")
    public ResponseEntity<List<EmployeeDocument>> document(@RequestBody LoginRequest request) {
        if (isNull(request.getNip())) {
            return responseError(401, "Username or password is empty!");
        }
        ServiceResolver<List<EmployeeDocument>> resolver = userService.findByDocument(request.getNip().trim());
        return response(resolver);
    }

    @GetMapping(value = "/document/type")
    public ResponseEntity<List<Document>> document() {
        ServiceResolver<List<Document>> resolver = documentService.loadAll();
        return response(resolver);
    }

    @PostMapping(value = "/document/upload")
    public ResponseEntity<List<EmployeeDocument>> documentUpload(@RequestBody UploadRequest request) {
        if (isNull(request.getNip(), request.getFile(), request.getExt())) {
            return responseError(401, "Nip is empty!");
        }
        Document document = documentService.validType(request.getType());
        if (Objects.isNull(document)) {
            return responseError(403, "Invalid type");
        }
        request.setName(fileService.storeFile(request.getNip(), request.getFile(), request.getExt()));
        ServiceResolver<List<EmployeeDocument>> resolver = userService.documentUpload(request.getNip().trim(), request.getType(), request.getName(), document);
        return response(resolver);
    }

    @PostMapping(value = "/document/remove")
    public ResponseEntity<List<EmployeeDocument>> documentRemove(@RequestBody UploadRequest request) {
        if (isNull(request.getNip(), request.getDocumentId())) {
            return responseError(401, "Nip is empty!");
        }
        ServiceResolver<List<EmployeeDocument>> resolver = userService.documentRemove(request.getNip().trim(), request.getDocumentId());
        return response(resolver);
    }

    @PostMapping(value = "/pangkat/add")
    public ResponseEntity<Response> pangkatAdd(@RequestBody UserPangkatRequest request) {
        if (isNull(request.getPangkat())||isNull(request.getPangkat().getTmt())) {
            return responseError(401, "Mandatory field");
        }
        ServiceResolver resolver = userService.pangkatAdd(request);
        return response(resolver);
    }

    @PostMapping(value = "/pangkat/remove")
    public ResponseEntity<Response> pangkatRemove(@RequestBody UserPangkatRequest request) {
        ServiceResolver resolver = userService.pangkatRemove(request);
        return response(resolver);
    }

    @GetMapping(value = "/view/cv/{nip}")
    public void viewCV(@PathVariable String nip, HttpServletResponse response) throws IOException {
        reportService.printCV(nip, response.getOutputStream());
    }

    @GetMapping(value = "/download/cv/{nip}")
    public ResponseEntity<StreamingResponseBody> downloadCV(@PathVariable String nip) {
        ServiceResolver<User> resolver = userService.findByNip(nip);
        if (resolver.isError()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = resolver.getResult();
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String fileName = employee.getNip() + "-" + employee.getNama() + ".pdf";
        StreamingResponseBody stream = out -> reportService.printCV(nip, out);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"")
                .body(stream);
    }

    @GetMapping(value = "/download/cuti/{nip}/{cutiId}")
    public ResponseEntity<StreamingResponseBody> downloadCuti(@PathVariable String nip, @PathVariable Long cutiId) {
        ServiceResolver<User> resolver = userService.findByNip(nip);
        if (resolver.isError()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = resolver.getResult();
        if (Objects.isNull(user)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ServiceResolver<Cuti> cutiResolver = cutiService.findByCutiUserAndId(user, cutiId);
        if (cutiResolver.isError()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cuti cuti = cutiResolver.getResult();
        if (Objects.isNull(cuti)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        String fileName = employee.getNip() + "_cuti_" + DTFomat.format(cuti.getStartDate()) + "_" + employee.getNama() + ".pdf";
        StreamingResponseBody stream = out -> reportService.printCuti(user, cutiResolver.getResult(), out);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + fileName + "\"")
                .body(stream);
    }

    @GetMapping(value = "/download/digital/{nip}/{documentId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String nip, @PathVariable Long documentId, HttpServletRequest request) {
        ServiceResolver<User> resolver = userService.findByNip(nip);
        if (resolver.isError()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Employee employee = resolver.getResult().getEmployee();
        if (Objects.isNull(employee)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        boolean found = false;
        String fileName = null;
        String filePath = null;
        for (EmployeeDocument doc : employee.getDocuments()) {
            if (0 == doc.getId().compareTo(documentId)) {
                found = true;
                filePath = doc.getPath();
                fileName = nip + "_" + doc.getDocument().getName() + FilenameUtils.getExtension(filePath).replaceAll(" ", "_");
                break;
            }
        }
        if (!found) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Resource resource = fileService.loadFileAsResource(nip, filePath);
        if (Objects.isNull(resource)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String contentType = "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
