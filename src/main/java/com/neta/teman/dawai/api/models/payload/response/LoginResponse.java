package com.neta.teman.dawai.api.models.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.models.dao.*;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.beans.BeanUtils;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.Date;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoginResponse {

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


    private String noAkses;

    private String npwp;

    private String noTaspen;


    private String docKTP;

    private String docBirth;

    private String docKK;

    private String docMarriage;

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

    private List<EmployeeContact> contacts;

    private List<EmployeeFamily> families;

    private List<EmployeeEducation> educations;

    private List<EmployeeMutasi> mutasis;

    private List<EmployeePangkat> pangkats;

    public LoginResponse(User user) {
        Role role = user.getRole();
        Employee employee = user.getEmployee();
        BeanCopy.copy(this, user, employee);
        this.contacts = BeanCopy.copyCollection(employee.getContacts(), EmployeeContact.class);
        this.educations = BeanCopy.copyCollection(employee.getEducations(), EmployeeEducation.class);
        this.families = BeanCopy.copyCollection(employee.getFamilies(), EmployeeFamily.class);
        this.mutasis = BeanCopy.copyCollection(employee.getMutasis(), EmployeeMutasi.class);
        this.pangkats = BeanCopy.copyCollection(employee.getPangkats(), EmployeePangkat.class);
        this.role = role.getName();
        this.nip = employee.getNip();
    }

}
