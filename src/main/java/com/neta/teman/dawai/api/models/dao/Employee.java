package com.neta.teman.dawai.api.models.dao;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
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

    private Date perkiraanPensiun;

    private String picture;

    private String docKTP;

    private String docBirth;

    private String docKK;

    private String docMarriage;

    private String noKarpeg;

    private String noAkses;

    private String npwp;

    private String noTaspen;

    private String noKarisSu;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<EmployeeContact> contacts;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<EmployeeFamily> families;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<EmployeeEducation> educations;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<EmployeeMutasi> mutasis;
//
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<EmployeePangkat> pangkats;

}
