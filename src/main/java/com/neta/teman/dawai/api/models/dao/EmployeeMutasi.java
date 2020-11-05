package com.neta.teman.dawai.api.models.dao;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.base.BaseEntity;
import com.neta.teman.dawai.api.models.converter.JsonDateConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "app_employee_mutasi_history")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EmployeeMutasi extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    Date tmt;

    Integer tahun;

    String pangkat;

}
