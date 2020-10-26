package com.neta.teman.dawai.api.models.dao;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.base.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "app_employee")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Employee extends BaseEntity {

    @Id
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

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<EmployeeContact> contacts;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<EmployeeFamily> families;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<EmployeeEducation> educations;

}
