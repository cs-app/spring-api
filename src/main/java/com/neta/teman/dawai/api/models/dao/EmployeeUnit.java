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
@Table(name = "app_employee_unit")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EmployeeUnit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Integer unit;
    String unitUtamaId;
    String namaUnitUtama;
    String singkatan;
    String unitAktif;
    String unitKerja2Id;
    String unitKerja3Id;
    String namaUnitKerja3;
    String ukGroupId;
    String namaUnitKerja2;
}
