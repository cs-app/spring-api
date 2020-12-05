package com.neta.teman.dawai.api.models.reports;

import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.models.dao.EmployeeMutasi;
import com.neta.teman.dawai.api.models.dao.EmployeePangkatHis;
import com.neta.teman.dawai.api.models.dao.PangkatGolongan;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
public class ProfilePangkat {

    Integer row;

    Date tmt;

    String golongan;

    String pangkat;

    public ProfilePangkat(int row, EmployeePangkatHis his) {
        BeanCopy.copy(this, his);
        if (Objects.isNull(his)) return;
        if (Objects.isNull(his.getPangkatGolongan())) return;
        PangkatGolongan o = his.getPangkatGolongan();
        this.pangkat = o.getNama();
        this.golongan = o.getGolongan();
        this.row = row;
    }

}
