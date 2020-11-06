package com.neta.teman.dawai.api.models.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.base.BaseEntity;
import com.neta.teman.dawai.api.models.converter.JsonDateConverter;
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

    String nama;

    String presensiId;

    String photo;

    String tempatLahir;

    Date tanggalLahir;

    String usia;

    String kelamin;

    String statusDiri;

    String agama;

    String alamat;

    String kelurahan;

    String kelurahanData;

    String kecamatan;

    String kecamatanData;

    String kebupaten;

    String kebupatenData;

    String provinsi;

    String provinsiData;

    @JsonProperty("rt_rw")
    String rtRW;

    String kodePos;

    String npwp;

    String noTaspen;

    String docKTP;

    String docBirth;

    String docKK;

    String docMarriage;

    // data kepegawaian
    String statusPeg;
    String jenisPeg;
    String mks;
    String mkg;
    String noKarpeg;
    String noKarisSu;
    @JsonProperty("PMK")
    String pmk;
    String statusAktif;
    String perkiraanPensiun;

    @JsonProperty("tgl_mulai_cpns")
    Date tglMulaiCPNS;

    @JsonProperty("no_sk_cpns")
    String noSKCPNS;

    @JsonProperty("tgl_sk_cpns")
    Date tglSKCPNS;

    @JsonProperty("tgl_mulai_pns")
    Date tglMulaiPNS;

    @JsonProperty("no_sk_pns")
    String noSKPNS;

    @JsonProperty("tgl_sk_pns")
    Date tglSKPNS;
    String jabatan;
    String eselon;
    Date tmtJabatan;
    String noSkJabatan;
    Date tglSkJabatan;

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
