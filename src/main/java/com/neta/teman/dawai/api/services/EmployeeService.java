package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeeService {

    ServiceResolver<List<Employee>> loadAll();

    ServiceResolver<Page<Employee>> loadPage(FilterRequest request);

}
