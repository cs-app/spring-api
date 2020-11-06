package com.neta.teman.dawai.api.models.converter;

import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.applications.commons.ValueValidation;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.plugins.simpeg.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimpegConverterBAK {

    public static void mergeUmum(SimpegEmployeeDataUmum source, Employee employee) {
//        employee.setNik(source.getNoKTP());
//        employee.setKk(source.getNoKK());
//        employee.setName(source.getNama());
//        employee.setPob(source.getTempatLahir());
//        employee.setDob(source.getTanggalLahir());
//        employee.setAddress(source.getAlamat());
//        employee.setGender(source.getKelamin());
//        employee.setMaritalStatus(source.getStatusDiri());
//        employee.setMaritalDate(source.getTanggalPernikahan());
//        employee.setPerkiraanPensiun(source.getPerkiraanPensiun());
        // contacts
        initializeContact(source, employee);
        // keluarga
        initializeFamily(source, employee);

    }

    public static void mergeRiwayat(SimpegEmployeeRiwayat source, Employee employee) {
        initializeEducation(source, employee);
        initializeMutasi(source, employee);
        initializePangkat(source, employee);
        BeanCopy.copy(employee, source.getDataPendukung());
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

    // contact, 1=phone, 2=email
    public static void initializeContact(SimpegEmployeeDataUmum source, Employee employee) {
        List<EmployeeContact> contacts = employee.getContacts();
        if (Objects.isNull(contacts)) contacts = new ArrayList<>();
        if (!ValueValidation.isNull(source.getNoTelepon())) {
            EmployeeContact phone = new EmployeeContact();
            phone.setType(AppConstants.Contact.PHONE);
            phone.setValue(source.getNoTelepon());
            if (!contactExist(contacts, source.getNoTelepon())) {
                contacts.add(phone);
            }
        }
        if (!ValueValidation.isNull(source.getHpNumber())) {
            EmployeeContact phone = new EmployeeContact();
            phone.setType(AppConstants.Contact.PHONE);
            phone.setValue(source.getHpNumber());
            if (!contactExist(contacts, source.getHpNumber())) {
                contacts.add(phone);
            }
        }
        if (!ValueValidation.isNull(source.getEmailAddress())) {
            EmployeeContact email = new EmployeeContact();
            email.setType(AppConstants.Contact.EMAIL);
            email.setValue(source.getEmailAddress());
            if (!contactExist(contacts, source.getEmailAddress())) {
                contacts.add(email);
            }
        }
        employee.setContacts(contacts);
    }

    private static boolean contactExist(List<EmployeeContact> contacts, String value) {
        for (EmployeeContact o : contacts) {
            if (o.getValue().equalsIgnoreCase(value)) return true;
        }
        return false;
    }

    private static void initializeFamily(SimpegEmployeeDataUmum source, Employee employee) {
        if (ValueValidation.isNull(source.getKeluarga()) || source.getKeluarga().isEmpty()) return;
        List<EmployeeFamily> families = employee.getFamilies();
        if (Objects.isNull(families)) families = new ArrayList<>();
        families.clear();
        // keluarga
        for (SimpegKeluarga o : source.getKeluarga()) {
            EmployeeFamily family = typeFamily(o);
            family.setName(o.getNama());
            family.setOccupation(o.getPekerjaan());
            families.add(family);
        }
        employee.setFamilies(families);
    }

    /**
     * flag dari simpeg pake suffix, grouping
     * 1 = pasangan = suami, istri
     * 2 = anak = anak
     * 3 = orangtua = bapakkandung, ibukandung
     * 4 = mertua = bapakmertua, ibumertua
     * 5 = saudara = saudarakandung
     */
    private static EmployeeFamily typeFamily(SimpegKeluarga o) {
        EmployeeFamily family = new EmployeeFamily();
        String hubungan = o.getHubungan().replaceAll("\\d", "");
        // group 1
        if ("suami".equalsIgnoreCase(hubungan)) {
            family.setType(1);
            family.setFamilyStatus("SUAMI");
        }
        if ("istri".equalsIgnoreCase(hubungan)) {
            family.setType(1);
            family.setFamilyStatus("ISTRI");
        }
        // group 2
        if ("anak".equalsIgnoreCase(hubungan)) {
            family.setType(2);
            family.setFamilyStatus("ANAK");
        }
        // group 3
        if ("bapakkandung".equalsIgnoreCase(hubungan)) {
            family.setType(3);
            family.setFamilyStatus("ORANG TUA");
        }
        if ("ibukandung".equalsIgnoreCase(hubungan)) {
            family.setType(3);
            family.setFamilyStatus("ORANG TUA");
        }
        // group 4
        if ("bapakmertua".equalsIgnoreCase(hubungan)) {
            family.setType(4);
            family.setFamilyStatus("MERTUA");
        }
        if ("ibumertua".equalsIgnoreCase(hubungan)) {
            family.setType(4);
            family.setFamilyStatus("MERTUA");
        }
        // group 5
        if ("saudarakandung".equalsIgnoreCase(hubungan)) {
            family.setType(5);
            family.setFamilyStatus("SAUDARA KANDUNG");
        }
        return family;
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
}
