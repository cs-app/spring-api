package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.dao.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String>, JpaSpecificationExecutor<Employee> {

}
