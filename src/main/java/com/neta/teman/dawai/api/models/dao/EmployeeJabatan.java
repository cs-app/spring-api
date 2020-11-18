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
@Table(name = "app_employee_jabatan")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EmployeeJabatan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String namaJabatan;
    String singkatan;
    String kategori;
    String subkategori;
    Integer tingkatan;
    String eselonIdAwal;
    String eselonIdAkhir;
    Integer kelasJabatan;
    Integer nilaiJabatan;
    Integer unitUtamaId;
    Integer kelJabatanId;
    String refId;
    String pensiun;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    Jabatan jabatan;
}
