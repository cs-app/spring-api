package com.neta.teman.dawai.api.modules.login.controllers;

import com.neta.teman.dawai.api.applications.base.BaseRestControllers;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.request.LoginRequest;
import com.neta.teman.dawai.api.models.payload.response.LoginResponse;
import com.neta.teman.dawai.api.services.UserServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@SuppressWarnings({"unchecked"})
public class UserController extends BaseRestControllers {

    @Autowired
    UserServices userServices;

    @PostMapping(value = "/login")
    public ResponseEntity<User> auth(@RequestBody LoginRequest request) {
        if (isNull(request.getUsername(), request.getPassword())) {
            return responseError(401, "Username or password is empty!");
        }
        ServiceResolver<User> resolver = userServices.findByUsernameAndPasswordSimpeg(request.getUsername(), request.getPassword());
        if (resolver.isError()) return responseError(resolver);
        return response(new LoginResponse(resolver.getResult()));
    }
}
