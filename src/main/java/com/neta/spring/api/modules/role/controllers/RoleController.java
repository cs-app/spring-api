package com.neta.spring.api.modules.role.controllers;

import com.neta.spring.api.applications.base.BaseRestController;
import com.neta.spring.api.services.RoleService;
import com.neta.spring.api.applications.base.ServiceResolver;
import com.neta.spring.api.repositories.dao.Role;
import com.neta.spring.api.repositories.payload.request.FilterRequest;
import com.neta.spring.api.repositories.payload.response.PageResponse;
import com.neta.spring.api.repositories.payload.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/role")
@SuppressWarnings({"unchecked", "rawtypes"})
public class RoleController extends BaseRestController {

    @Autowired
    RoleService roleService;

    @PostMapping("/save")
    public ResponseEntity<Response> save(@RequestBody Role request) {
         ServiceResolver<Boolean> resolver = roleService.save(request);
         return response(resolver);
    }

    @PostMapping("/delete")
    public ResponseEntity<Response> delete(@RequestBody Role request) {
        ServiceResolver<Boolean> resolver = roleService.delete(request);
        return response(resolver);
    }

    @PostMapping("/list")
    public ResponseEntity<Response<List<Role>>> list(@RequestBody FilterRequest request) {
         ServiceResolver<List<Role>> resolver = roleService.list(request);
         return response(resolver);
    }

    @PostMapping("/page")
    public ResponseEntity<PageResponse> delete(@RequestBody FilterRequest o) {
        ServiceResolver<Page<Role>> resolver = roleService.page(o);
        return responsePage(resolver);
    }
}
