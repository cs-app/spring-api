package com.neta.spring.api.services;

import com.neta.spring.api.repositories.dao.Role;
import com.neta.spring.api.applications.base.ServiceResolver;
import com.neta.spring.api.repositories.payload.request.FilterRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoleService {

    ServiceResolver<Boolean> save(Role request);

    ServiceResolver<Boolean> delete(Role request);

    ServiceResolver<List<Role>> list(FilterRequest request);

    ServiceResolver<Page<Role>> page(FilterRequest request);

}
