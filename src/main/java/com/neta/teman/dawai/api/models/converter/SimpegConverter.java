package com.neta.teman.dawai.api.models.converter;

import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.applications.commons.ValueValidation;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.plugins.simpeg.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpegConverter {

    public static void merge(Employee employee, SimpegAuth source) {
        SimpegAuth.Personal personal = source.getPersonal();
        BeanCopy.copy(employee, personal);
        BeanCopy.copy(employee, source.getEmployee());
        BeanCopy.copy(employee, source.getEmployee().getCpns());
        BeanCopy.copy(employee, source.getEmployee().getPns());
        BeanCopy.copy(employee, source.getEmployee().getJabatanEselon());
        mergeContact(employee, personal.getLainnya());
        mergeFamily(employee, source.getKeluarga());
        mergeDataLain(employee, source);
    }

    private static void mergeDataLain(Employee employee, SimpegAuth source) {
        BeanCopy.copy(employee, source.getPersonal().getLainnya());
        BeanCopy.copy(employee, source.getEmployee());
//        employee.setNpwp(source.getPersonal().getLainnya().getNpwp());
//        employee.setNoTaspen(source.getPersonal().getLainnya().getNoTaspen());
//        employee.setNoKarpeg(source.getEmployee().getNoKarpeg());
//        employee.setNoKarisSu(source.getEmployee().getNoKarisSu());
//        employee.setStatusPegawai(source.getEmployee().getStatusPeg());

    }

    /**
     * flag dari simpeg pake suffix, grouping
     * 1 = pasangan = suami, istri
     * 2 = anak = anak
     * 3 = orangtua = bapakkandung, ibukandung
     * 4 = mertua = bapakmertua, ibumertua
     * 5 = saudara = saudarakandung
     */
    private static void mergeFamily(Employee employee, SimpegAuth.Keluarga keluarga) {
        List<EmployeeFamily> families = employee.getFamilies();
        if (Objects.isNull(families)) families = new ArrayList<>();
        families.clear();
        // pasangan
        for (SimpegAuth.Keluarga.KeluargaDetail o : keluarga.getPasangan()) {
            String hubungan = o.getHubungan().replaceAll("\\d", "");
            EmployeeFamily family = new EmployeeFamily();
            family.setType(1);
            family.setFamilyStatus(hubungan.toUpperCase());
            copyFamily(family, o);
            families.add(family);
        }
        // anak
        for (SimpegAuth.Keluarga.KeluargaDetail o : keluarga.getAnak()) {
            String hubungan = o.getHubungan().replaceAll("\\d", "");
            EmployeeFamily family = new EmployeeFamily();
            family.setType(2);
            family.setFamilyStatus(hubungan.toUpperCase());
            copyFamily(family, o);
            families.add(family);
        }
        // orangtua
        for (SimpegAuth.Keluarga.KeluargaDetail o : keluarga.getOrangTua()) {
            String hubungan = o.getHubungan().replaceAll("\\d", "");
            EmployeeFamily family = new EmployeeFamily();
            family.setType(3);
            family.setFamilyStatus(hubungan.toUpperCase());
            copyFamily(family, o);
            families.add(family);
        }
        // mertua
        for (SimpegAuth.Keluarga.KeluargaDetail o : keluarga.getMertua()) {
            String hubungan = o.getHubungan().replaceAll("\\d", "");
            EmployeeFamily family = new EmployeeFamily();
            family.setType(4);
            family.setFamilyStatus(hubungan.toUpperCase());
            copyFamily(family, o);
            families.add(family);
        }
        // saudara
        for (SimpegAuth.Keluarga.KeluargaDetail o : keluarga.getSaudara()) {
            String hubungan = o.getHubungan().replaceAll("\\d", "");
            EmployeeFamily family = new EmployeeFamily();
            family.setType(5);
            family.setFamilyStatus(hubungan.toUpperCase());
            copyFamily(family, o);
            families.add(family);
        }
        employee.setFamilies(families);
    }

    private static void copyFamily(EmployeeFamily family, SimpegAuth.Keluarga.KeluargaDetail o){
        family.setName(o.getNama());
        family.setOccupation(o.getPekerjaan());
        family.setDob(o.getTanggalLahir());
        family.setPob(o.getTempatLahir());
        family.setMob(o.getTanggalNikah());
    }

    private static void mergeContact(Employee employee, SimpegAuth.Personal.PersonalLainya source) {
        List<EmployeeContact> contacts = employee.getContacts();
        if (Objects.isNull(contacts)) contacts = new ArrayList<>();
        contacts.clear();
        if (!ValueValidation.isNull(source.getHpNumber())) {
            EmployeeContact phone = new EmployeeContact();
            phone.setType(AppConstants.Contact.PHONE);
            phone.setValue(source.getHpNumber());
            contacts.add(phone);
        }
        if (!ValueValidation.isNull(source.getEmailAddress())) {
            EmployeeContact email = new EmployeeContact();
            email.setType(AppConstants.Contact.EMAIL);
            email.setValue(source.getEmailAddress());
            contacts.add(email);
        }
        employee.setContacts(contacts);
    }

    public static void mergeRiwayat(Employee employee, SimpegEmployeeRiwayat source) {
        initializeEducation(source, employee);
        initializeMutasi(source, employee);
        initializePangkat(source, employee);
    }

    private static void initializeEducation(SimpegEmployeeRiwayat source, Employee employee) {
        List<EmployeeEducation> educations = employee.getEducations();
        if (Objects.isNull(educations)) educations = new ArrayList<>();
        educations.clear();
        for (SimpegPendidikan o : source.getPendidikan()) {
            EmployeeEducation education = new EmployeeEducation();
            education.setType(o.getJenjang());
            education.setMajors(o.getJurusan());
            education.setValue(o.getNamaSekolah());
            education.setGraduated(o.getTahunLulus());
            educations.add(education);
        }
        employee.setEducations(educations);
    }

    private static void initializePangkat(SimpegEmployeeRiwayat source, Employee employee) {
        List<EmployeePangkat> pangkats = employee.getPangkats();
        if (Objects.isNull(pangkats)) pangkats = new ArrayList<>();
        pangkats.clear();
        for (SimpegPangkat o : source.getPangkat()) {
            EmployeePangkat pangkat = BeanCopy.copy(o, EmployeePangkat.class);
            pangkats.add(pangkat);
        }
        employee.setPangkats(pangkats);
    }

    private static void initializeMutasi(SimpegEmployeeRiwayat source, Employee employee) {
        List<EmployeeMutasi> mutasis = employee.getMutasis();
        if (Objects.isNull(mutasis)) mutasis = new ArrayList<>();
        mutasis.clear();
        for (SimpegMutasi o : source.getMutasi()) {
            mutasis.add(BeanCopy.copy(o, EmployeeMutasi.class));
        }
        employee.setMutasis(mutasis);
    }
}
