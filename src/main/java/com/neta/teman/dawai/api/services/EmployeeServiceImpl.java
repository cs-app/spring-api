package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.BaseService;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl extends BaseService implements EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Override
    public ServiceResolver<List<Employee>> loadAll() {
        return success(employeeRepository.findAll());
    }

    @Override
    public ServiceResolver<Page<Employee>> loadPage(FilterRequest request) {
        return success(employeeRepository.findAll(request.pageRequest()));
    }

}
