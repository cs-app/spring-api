package com.neta.teman.dawai.api.modules.user.controllers;

import com.neta.teman.dawai.api.applications.base.BaseRestController;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.MediaTypeUtils;
import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.request.LoginRequest;
import com.neta.teman.dawai.api.models.payload.response.LoginResponse;
import com.neta.teman.dawai.api.services.ReportService;
import com.neta.teman.dawai.api.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import reactor.netty.http.server.HttpServerResponse;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@SuppressWarnings({"unchecked"})
public class UserController extends BaseRestController {

    @Autowired
    UserService userService;

    @Autowired
    ReportService reportService;

    @PostMapping(value = "/login")
    public ResponseEntity<User> auth(@RequestBody LoginRequest request) {
        if (isNull(request.getUsername(), request.getPassword())) {
            return responseError(401, "Username or password is empty!");
        }
        ServiceResolver<User> resolver = userService.findByUsernameAndPasswordSimpeg(request.getUsername().trim(), request.getPassword().trim());
        if (resolver.isError()) return responseError(resolver);
        return response(new LoginResponse(resolver.getResult()));
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .body(stream);
    }
}
