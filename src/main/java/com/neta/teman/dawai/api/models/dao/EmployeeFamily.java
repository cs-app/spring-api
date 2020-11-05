package com.neta.teman.dawai.api.models.dao;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "app_employee_family")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EmployeeFamily extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer type;

    private String name;

    private String occupation;

    private String familyStatus;

}
