package com.neta.teman.dawai.api.models.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.base.BaseEntity;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.models.dao.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserResponse {

    String role;

    String nip;

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
    
    String noAkses;

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

    Date tmtGol;

    String noSkGol;

    Date tglSkGol;

    EmployeeJabatan jabatanDetail;

    EmployeeEselon eselonDetail;

    EmployeePangkat pangkatDetail;

    EmployeeGolongan golonganDetail;

    List<EmployeeContact> contacts;

    List<EmployeeFamily> families;

    List<EmployeeEducation> educations;

    List<EmployeeMutasi> mutasis;

    List<EmployeePangkatHis> pangkats;

    List<EmployeeUnit> units;

    List<EmployeeDocument> documents;

    public UserResponse(User user) {
        Role role = user.getRole();
        Employee employee = user.getEmployee();
        BeanCopy.copy(this, user, employee);
        this.jabatanDetail = BeanCopy.copy(employee.getJabatanDetail(), EmployeeJabatan.class);
        this.eselonDetail = BeanCopy.copy(employee.getEselonDetail(), EmployeeEselon.class);
        this.pangkatDetail = BeanCopy.copy(employee.getPangkatDetail(), EmployeePangkat.class);
        this.golonganDetail = BeanCopy.copy(employee.getGolonganDetail(), EmployeeGolongan.class);

        this.contacts = BeanCopy.copyCollection(employee.getContacts(), EmployeeContact.class);
        this.educations = BeanCopy.copyCollection(employee.getEducations(), EmployeeEducation.class);
        this.families = BeanCopy.copyCollection(employee.getFamilies(), EmployeeFamily.class);
        this.mutasis = BeanCopy.copyCollection(employee.getMutasis(), EmployeeMutasi.class);
        this.pangkats = BeanCopy.copyCollection(employee.getPangkats(), EmployeePangkatHis.class);
        this.units = BeanCopy.copyCollection(employee.getUnits(), EmployeeUnit.class);
        this.documents = BeanCopy.copyCollection(employee.getDocuments(), EmployeeDocument.class);

        this.role = role.getName();
        this.nip = employee.getNip();
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class EmployeeJabatan extends BaseEntity {

        Long id;
        Integer jabatanId;
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
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class EmployeeEselon extends BaseEntity {

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

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class EmployeePangkat extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        Integer pangkatId;
        String gol;

        String pangkat;

        String refId;

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class EmployeeGolongan extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        String gol;

        Integer tingkatan;

        String keterangan;

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class EmployeeUnit extends BaseEntity {

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




}
