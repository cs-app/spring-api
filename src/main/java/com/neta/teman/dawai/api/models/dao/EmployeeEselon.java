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
@Table(name = "app_employee_eselon")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EmployeeEselon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String eselonId;
    String eselon;
    Integer tingkatan;
    String keterangan;
    String golAwal;
    String golAkhir;
}
