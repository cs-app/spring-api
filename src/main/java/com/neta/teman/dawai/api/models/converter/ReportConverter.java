package com.neta.teman.dawai.api.models.converter;

import com.neta.teman.dawai.api.applications.commons.DTFomat;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.reports.Profile;

import java.util.*;

public class ReportConverter {

    public static List<Profile> profile(Map map, User user) {
        List<Profile> profiles = new ArrayList<>();
        Employee employee = user.getEmployee();
        newProfile(profiles, "NIP", employee.getNip());
        newProfile(profiles, "Nama", employee.getNama());
        newProfile(profiles, "Status Pegawai (Aktif/Pensiun/Pindah/CTLN)", employee.getStatusAktif());
        newProfile(profiles, "Tempat, Tanggal Lahir", map.get("tempat_lahir") + ", " + DTFomat.format(employee.getTanggalLahir()));
        newProfile(profiles, "Alamat Tinggal", employee.getAlamat());
        newProfile(profiles, "No. KTP", employee.getKtp());
        newProfile(profiles, "No. KK", employee.getKk());
        newProfile(profiles, "Jenis Kelamin", employee.getKelamin());
        newProfile(profiles, "Status Perkawinan", employee.getStatusDiri());
        String tglPernikahan = "";
        List<EmployeeFamily> families = employee.getFamilies();
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
//        newProfile(profiles, "No. Tlp", map.get("kelamin"));
        newProfile(profiles, "Email", email);
        newProfile(profiles, "Perkiraan Pensiun", employee.getPerkiraanPensiun());
        return profiles;
    }

    private static void newProfile(List<Profile> profiles, String key, Object val) {
        if (Objects.isNull(val)) {
            profiles.add(new Profile(key, ""));
            return;
        }
        profiles.add(new Profile(key, String.valueOf(val)));
    }

    public static Collection<?> family(User user) {
        List<EmployeeFamily> employeeFamilies = user.getEmployee().getFamilies();
        if (Objects.isNull(employeeFamilies)) return new ArrayList<>();
        for (EmployeeFamily o : employeeFamilies) {
            if("SAUDARAKANDUNG".equalsIgnoreCase(o.getFamilyStatus())) {
                o.setFamilyStatus("SAUDARA KANDUNG");
            }
            if("IBUMERTUA".equalsIgnoreCase(o.getFamilyStatus())) {
                o.setFamilyStatus("IBU MERTUA");
            }
            if("BAPAKMERTUA".equalsIgnoreCase(o.getFamilyStatus())) {
                o.setFamilyStatus("BAPAK MERTUA");
            }
            if("IBUKANDUNG".equalsIgnoreCase(o.getFamilyStatus())) {
                o.setFamilyStatus("IBU KANDUNG");
            }
            if("BAPAKKANDUNG".equalsIgnoreCase(o.getFamilyStatus())) {
                o.setFamilyStatus("BAPAK KANDUNG");
            }
        }
        employeeFamilies.sort(Comparator.comparing(EmployeeFamily::getType));//.thenComparingInt(EmployeeFamily::getAge));

        return employeeFamilies;
    }

    public static Collection<?> container() {
        ArrayList<Profile> detail = new ArrayList<>();
        detail.add(new Profile("P","Q"));
        return detail;

    }

    public static Collection<?> education(User user) {
        List<EmployeeEducation> education = user.getEmployee().getEducations();
        if(Objects.isNull(education)) return new ArrayList<>();
        return education;
    }

    public static Collection<?> mutasi(User user) {
        List<EmployeeMutasi> education = user.getEmployee().getMutasis();
        if(Objects.isNull(education)) return new ArrayList<>();
        return education;
    }

    public static Collection<?> pendukung(User user) {
        List<Profile> profiles = new ArrayList<>();
        Employee employee = user.getEmployee();
        newProfile(profiles, "ID Karpeg", employee.getNoKarpeg());
        newProfile(profiles, "NPWP", employee.getNpwp());
        newProfile(profiles, "ID Taspen", employee.getNoTaspen());
        newProfile(profiles, "ID Karis", employee.getNoKarisSu());
        return profiles;
    }
}
