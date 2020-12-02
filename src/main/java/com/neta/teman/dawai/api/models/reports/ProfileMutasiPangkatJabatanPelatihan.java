package com.neta.teman.dawai.api.models.reports;

import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.models.dao.EmployeeMutasi;
import com.neta.teman.dawai.api.models.dao.EmployeePangkatHis;
import com.neta.teman.dawai.api.models.dao.PangkatGolongan;
import lombok.Data;

import java.util.Date;

@Data
public class ProfileMutasiPangkatJabatanPelatihan {

    Integer row;

    Date tmt;

    Integer tahun;

    String gol;

    String golongan;

    String jabatan;

    String pangkat;

    public ProfileMutasiPangkatJabatanPelatihan(int row, EmployeeMutasi o) {
        BeanCopy.copy(this, o);
        this.row = row;
    }

    public ProfileMutasiPangkatJabatanPelatihan(int row, EmployeePangkatHis o) {
        BeanCopy.copy(this, o);
        this.pangkat = o.getPangkatGolongan().getNama();
        this.row = row;
    }

    public ProfileMutasiPangkatJabatanPelatihan(int row, PangkatGolongan o) {
        BeanCopy.copy(this, o);
        this.row = row;
    }
}
