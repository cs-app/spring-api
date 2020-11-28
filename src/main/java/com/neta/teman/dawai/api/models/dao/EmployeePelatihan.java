package com.neta.teman.dawai.api.models.dao;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "app_employee_pelatihan")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EmployeePelatihan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String path;

    private String type;

    @Temporal(TemporalType.DATE)
    private Date tahun;

    @Temporal(TemporalType.DATE)
    private Date tmt;

    private String diklat;
}
