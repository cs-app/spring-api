package com.neta.teman.dawai.api.models.reports;

import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.applications.commons.DTFomat;
import com.neta.teman.dawai.api.models.dao.*;
import lombok.Data;

import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Data
public class ProfilePelatihan {

    Integer row;

    Date tmt;

    // tahun
    String golongan;

    // diklat
    String pangkat;

    String sisa;

    String ambil;

    public ProfilePelatihan(){}

    public ProfilePelatihan(int row, EmployeePelatihan o) {
        BeanCopy.copy(this, o);
        this.golongan = Objects.isNull(o) ? "" : String.valueOf(new Date(o.getTahun().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
        this.pangkat = o.getDiklat();
        this.row = row;
    }


    public ProfilePelatihan(int row, EmployeeSKP o) {
        BeanCopy.copy(this, o);
        this.golongan = Objects.isNull(o) ? "" : String.valueOf(new Date(o.getTahun().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
        this.pangkat = o.getNilaiRata();
        this.row = row;
    }

    public ProfilePelatihan(int incrementAndGet, EmployeeCreditScore o) {
        BeanCopy.copy(this, o);
        this.golongan = Objects.isNull(o) ? "" : String.valueOf(new Date(o.getTahun().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
        this.pangkat = String.valueOf(o.getNilai());
        this.row = row;
    }

    public ProfilePelatihan(int incrementAndGet, EmployeeSatyaLencana o) {
        BeanCopy.copy(this, o);
        this.golongan = Objects.isNull(o) ? "" : String.valueOf(new Date(o.getTahun().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
        this.pangkat = String.valueOf(o.getSatyaLencana());
        this.row = row;
    }

    public ProfilePelatihan(int row, EmployeeHukumanDisiplin o) {
        BeanCopy.copy(this, o);
        this.golongan = Objects.isNull(o) ? "" : String.valueOf(new Date(o.getTahun().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
        this.pangkat = String.valueOf(o.getHukuman());
        this.row = row;
    }

    public ProfilePelatihan(int row, CutiSummary o) {
        BeanCopy.copy(this, o);
        this.golongan = Objects.isNull(o) ? "" : String.valueOf(new Date(o.getTahun()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
        this.pangkat = String.valueOf(o.getKuotaCuti());
        this.ambil = String.valueOf(o.getKuotaCuti());
        this.sisa = String.valueOf(o.getKuotaPastCuti() + o.getKuotaPastTwoCuti());
        this.row = row;
    }
}
