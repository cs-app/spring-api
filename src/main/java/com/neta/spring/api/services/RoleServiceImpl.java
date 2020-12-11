package com.neta.spring.api.services;

import com.neta.spring.api.repositories.dao.Role;
import com.neta.spring.api.repositories.repository.RoleRepository;
import com.neta.spring.api.applications.base.BaseService;
import com.neta.spring.api.applications.base.ServiceResolver;
import com.neta.spring.api.repositories.payload.request.FilterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl extends BaseService implements RoleService {

    @Autowired
    protected RoleRepository repository;

    public ServiceResolver<Boolean> save(Role param) {
        Role o = repository.save(param);
        return success(true);
    }

    public ServiceResolver<Boolean> delete(Role o) {
        repository.delete(o);
        return success(true);
    }

    public ServiceResolver<List<Role>> list(FilterRequest request) {
        List<Role> list = repository.findAll();
        return success(list);
    }

    public ServiceResolver<Page<Role>> page(FilterRequest request) {
        Page<Role> pages = repository.findAll(request.pageRequest());
        return success(pages);
    }
}
