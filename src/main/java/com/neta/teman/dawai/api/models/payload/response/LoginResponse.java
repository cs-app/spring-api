package com.neta.teman.dawai.api.models.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.models.dao.*;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.beans.BeanUtils;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.Date;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoginResponse {

    private String role;

    private String username;

    private String nip;

    private String nik;

    private String kk;

    private String name;

    private String pob;

    private Date dob;

    private String address;

    private String gender;

    private String maritalStatus;

    private Date maritalDate;

    private String picture;

    private String docKTP;

    private String docBirth;

    private String docKK;

    private String docMarriage;

    private List<EmployeeContact> contacts;

    private List<EmployeeFamily> families;

    private List<EmployeeEducation> educations;

    public LoginResponse(User user) {
        Role role = user.getRole();
        Employee employee = user.getEmployee();
        BeanCopy.copy(this, user, employee);
        this.contacts = BeanCopy.copyCollection(employee.getContacts(), EmployeeContact.class);
        this.educations = BeanCopy.copyCollection(employee.getEducations(), EmployeeEducation.class);
        this.families = BeanCopy.copyCollection(employee.getFamilies(), EmployeeFamily.class);
        this.role = role.getName();
    }

}
