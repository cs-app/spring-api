package com.neta.teman.dawai.api.models.converter;

import com.neta.teman.dawai.api.applications.commons.DTFomat;
import com.neta.teman.dawai.api.models.dao.EmployeeContact;
import com.neta.teman.dawai.api.models.dao.EmployeeFamily;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.reports.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReportConverter {

    public static List<Profile> profile(Map map, User user) {
        List<Profile> profiles = new ArrayList<>();
        newProfile(profiles, "NIP", map.get("nip"));
        newProfile(profiles, "Nama", map.get("nama"));
        newProfile(profiles, "Status Pegawai (Aktif/Pensiun/Pindah/CTLN)", map.get("status_aktif"));
        newProfile(profiles, "Tempat, Tanggal Lahir", map.get("tempat_lahir") + ", " + DTFomat.format((Long) map.get("tanggal_lahir")));
        newProfile(profiles, "Alamat Tinggal", map.get("alamat"));
        newProfile(profiles, "No. KTP", map.get("ktp"));
        newProfile(profiles, "No. KK", map.get("kk"));
        newProfile(profiles, "Jenis Kelamin", map.get("kelamin"));
        newProfile(profiles, "Status Perkawinan", map.get("status_diri"));
        String tglPernikahan = "";
        List<EmployeeFamily> families = new ArrayList<>();
        for (EmployeeFamily f : families) {
            if (f.getType() == 1) {
                tglPernikahan = DTFomat.format(f.getMob());
                break;
            }
        }
        newProfile(profiles, "Tgl. Pernikahan", tglPernikahan);
        List<EmployeeContact> contacts = user.getEmployee().getContacts();
        String noTlp = "";
        String email = "";
        for (EmployeeContact c : contacts) {
            if (c.getType() == 1) {
                noTlp = c.getValue();
            } else email = c.getValue();
        }
        newProfile(profiles, "No. HP", noTlp);
        newProfile(profiles, "No. Tlp", map.get("kelamin"));
        newProfile(profiles, "Email", email);
        return profiles;
    }

    private static void newProfile(List<Profile> profiles, String key, Object val) {
        if(Objects.isNull(val)) {
            profiles.add(new Profile(key, ""));
            return;
        }
        profiles.add(new Profile(key, String.valueOf(val)));
    }
}
