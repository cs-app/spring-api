package com.neta.teman.dawai.api.models.converter;

import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.applications.commons.DTFomat;
import com.neta.teman.dawai.api.applications.commons.UserCommons;
import com.neta.teman.dawai.api.applications.commons.ValueValidation;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.repository.JabatanRepository;
import com.neta.teman.dawai.api.models.repository.PangkatGolonganRepository;
import com.neta.teman.dawai.api.models.repository.PendidikanRepository;
import com.neta.teman.dawai.api.plugins.simpeg.models.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SimpegConverter {

    @Autowired
    UserCommons userCommons;

    @Autowired
    PangkatGolonganRepository pangkatGolonganRepository;

    @Autowired
    JabatanRepository jabatanRepository;

    @Autowired
    PendidikanRepository pendidikanRepository;

    public void merge(Employee employee, SimpegAuth source) {
        SimpegAuth.Personal personal = source.getPersonal();
        BeanCopy.copy(employee, personal);
        BeanCopy.copy(employee, source.getEmployee());
        BeanCopy.copy(employee, source.getEmployee().getCpns());
        BeanCopy.copy(employee, source.getEmployee().getPns());
        BeanCopy.copy(employee, source.getEmployee().getJabatanEselon());
        mergeContact(employee, personal.getLainnya());
        mergeFamily(employee, source.getKeluarga());
        mergeDataLain(employee, source);
        mergeJabatanEselon(employee, source);
        mergeJabatanGolonganPangkat(employee, source);
        mergeUnitKerja(employee, source);
    }

    private void mergeUnitKerja(Employee employee, SimpegAuth source) {
        SimpegAuth.EmployeeUnitKerja unitKerja = source.getUnitKerja();
        if (Objects.isNull(unitKerja)) return;
        List<EmployeeUnit> units = new ArrayList<>();
        // unit 1
        if (Objects.nonNull(unitKerja.getUnitUtama())) {
            EmployeeUnit unit = BeanCopy.copy(unitKerja.getUnitUtama(), EmployeeUnit.class);
            unit.setUnit(1);
            units.add(unit);
        }
        // unit 2
        if (Objects.nonNull(unitKerja.getUnitKerja2())) {
            EmployeeUnit unit = BeanCopy.copy(unitKerja.getUnitKerja2(), EmployeeUnit.class);
            unit.setUnit(2);
            units.add(unit);
        }
        // unit 3
        if (Objects.nonNull(unitKerja.getUnitKerja3())) {
            EmployeeUnit unit = BeanCopy.copy(unitKerja.getUnitKerja3(), EmployeeUnit.class);
            unit.setUnit(3);
            units.add(unit);
        }
        // unit 4
        if (Objects.nonNull(unitKerja.getUnitKerja4())) {
            EmployeeUnit unit = BeanCopy.copy(unitKerja.getUnitKerja4(), EmployeeUnit.class);
            unit.setUnit(4);
            units.add(unit);
        }

        List<EmployeeUnit> newUnits = units.stream().filter(o -> {
            if (Objects.isNull(employee.getUnits())) return true;
            for (EmployeeUnit u : employee.getUnits()) {
                if (u.getUnit().equals(o.getUnit())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        if (Objects.isNull(employee.getUnits())) {
            employee.setUnits(newUnits);
        } else employee.getUnits().addAll(newUnits);
    }

    private void mergeJabatanGolonganPangkat(Employee employee, SimpegAuth source) {
        SimpegAuth.Employee.EmployeeGolonganPangkat golonganPangkat = source.getEmployee().getGolonganPangkat();
        EmployeeGolongan golongan = employee.getGolonganDetail();
        if (Objects.isNull(golongan)) {
            golongan = new EmployeeGolongan();
            employee.setGolonganDetail(golongan);
        }
        BeanUtils.copyProperties(golonganPangkat, employee, "id");
        BeanUtils.copyProperties(golonganPangkat.getGolData(), golongan, "id");
        EmployeePangkat pangkat = employee.getPangkatDetail();
        if (Objects.isNull(pangkat)) {
            pangkat = new EmployeePangkat();
            employee.setPangkatDetail(pangkat);
        }
        BeanUtils.copyProperties(golonganPangkat.getPangkatData(), pangkat, "id");
    }

    private void mergeJabatanEselon(Employee employee, SimpegAuth source) {
        SimpegAuth.Employee.EmployeeJabatanEselon jabatanEselon = source.getEmployee().getJabatanEselon();
        EmployeeJabatan jabatan = employee.getJabatanDetail();
        if (Objects.isNull(jabatan)) {
            jabatan = new EmployeeJabatan();
            employee.setJabatanDetail(jabatan);
        }
        SimpegAuth.Employee.EmployeeJabatanEselon.EmployeeJabatanData jabatanData = jabatanEselon.getJabatanData();
        Jabatan masterJabatan = jabatanRepository.findByName(jabatanEselon.getJabatan().toUpperCase().trim());
        if (Objects.isNull(masterJabatan)) {
            masterJabatan = new Jabatan();
            masterJabatan.setSimpegId(jabatanData.getJabatanId());
            masterJabatan.setName(jabatanData.getNamaJabatan().toUpperCase());
            masterJabatan.setKelasJabatan(jabatanData.getKelasJabatan());
            jabatanRepository.save(masterJabatan);
            masterJabatan = jabatanRepository.findByName(jabatan.getNamaJabatan());
        } else {
            masterJabatan.setSimpegId(jabatanData.getJabatanId());
            jabatanRepository.save(masterJabatan);
        }
        jabatan.setJabatan(masterJabatan);
        BeanUtils.copyProperties(jabatanEselon.getJabatanData(), jabatan, "id");

        EmployeeEselon eselon = employee.getEselonDetail();
        if (Objects.isNull(eselon)) {
            eselon = new EmployeeEselon();
            employee.setEselonDetail(eselon);
        }
        BeanUtils.copyProperties(jabatanEselon.getEselonData(), eselon, "id");
        BeanUtils.copyProperties(jabatanEselon.getEselonData(), employee);

    }

    private static void mergeDataLain(Employee employee, SimpegAuth source) {
        BeanCopy.copy(employee, source.getPersonal().getLainnya());
        BeanCopy.copy(employee, source.getEmployee());
    }

    /**
     * flag dari simpeg pake suffix, grouping
     * 1 = pasangan = suami, istri
     * 2 = anak = anak
     * 3 = orangtua = bapakkandung, ibukandung
     * 4 = mertua = bapakmertua, ibumertua
     * 5 = saudara = saudarakandung
     */
    private void mergeFamily(Employee employee, SimpegAuth.Keluarga keluarga) {
        List<EmployeeFamily> families = new ArrayList<>();
        // if (Objects.isNull(families)) families = new ArrayList<>();
        // families.clear();
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
        List<EmployeeFamily> newFamilies = families.stream().filter(o -> {
            if (Objects.isNull(employee.getFamilies())) return true;
            for (EmployeeFamily f : employee.getFamilies()) {
                if (f.getType().equals(o.getType()) &&
                        f.getFamilyStatus().equalsIgnoreCase(o.getFamilyStatus()) &&
                        f.getName().equalsIgnoreCase(o.getName())
//                        DTFomat.format(f.getDob()).equalsIgnoreCase(DTFomat.format(o.getDob())) &&
//                        f.getPob().equalsIgnoreCase(o.getPob())
                ) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        if (Objects.isNull(employee.getFamilies())) {
            employee.setFamilies(newFamilies);
        } else employee.getFamilies().addAll(newFamilies);
    }

    private void copyFamily(EmployeeFamily family, SimpegAuth.Keluarga.KeluargaDetail o) {
        family.setName(o.getNama());
        family.setOccupation(o.getPekerjaan());
        family.setDob(o.getTanggalLahir());
        family.setPob(o.getTempatLahir());
        family.setMob(o.getTanggalNikah());
    }

    private void mergeContact(Employee employee, SimpegAuth.Personal.PersonalLainya source) {
        List<EmployeeContact> contacts = new ArrayList<>();
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
        List<EmployeeContact> newUnits = contacts.stream().filter(o -> {
            if (Objects.isNull(employee.getContacts())) return true;
            for (EmployeeContact c : employee.getContacts()) {
                if (Objects.isNull(o.getValue())) continue;
                if (c.getType() == o.getType() && c.getValue().equalsIgnoreCase(o.getValue())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        if (Objects.isNull(employee.getUnits())) {
            employee.setContacts(contacts);
        } else employee.getContacts().addAll(newUnits);

    }

    public void mergeRiwayat(Employee employee, SimpegEmployeeRiwayat source) {
        initializeEducation(source, employee);
        initializeMutasi(source, employee);
        initializePangkat(source, employee);
    }

    private void initializeEducation(SimpegEmployeeRiwayat source, Employee employee) {
        List<EmployeeEducation> educations = employee.getEducations();
        if (Objects.isNull(educations)) educations = new ArrayList<>();
        List<SimpegPendidikan> newPendidikan = source.getPendidikan().stream().filter(o -> {
            if (Objects.isNull(employee.getPangkats())) return true;
            for (EmployeeEducation ph : employee.getEducations()) {
                if (ph.getGraduated().equals(o.getTahunLulus()) && ph.getType().equalsIgnoreCase(o.getJenjang())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        for (SimpegPendidikan o : newPendidikan) {
            if (Objects.isNull(o.getJenjang())) continue;
            EmployeeEducation education = new EmployeeEducation();
            education.setType(o.getJenjang());
            education.setMajors(o.getJurusan());
            education.setValue(o.getNamaSekolah());
            education.setGraduated(o.getTahunLulus());
            educations.add(education);
            // check pendidikan
            List<Pendidikan> pendidikanTmp = pendidikanRepository.findByType(o.getJenjang());
            if (pendidikanTmp.isEmpty()) {
                Pendidikan pendidikan = new Pendidikan();
                pendidikan.setType(o.getJenjang());
                pendidikan.setTingkat(userCommons.tingkatPendidikan(o.getJenjang()));
                pendidikanRepository.save(pendidikan);
            }
        }
        employee.setEducations(educations);
    }

    private void initializePangkat(SimpegEmployeeRiwayat source, final Employee employee) {
        List<EmployeePangkatHis> pangkats = employee.getPangkats();
        if (Objects.isNull(pangkats)) pangkats = new ArrayList<>();
        List<PangkatGolongan> pangkatGolongans = pangkatGolonganRepository.findAll();
        // exclude exist pangkat
        Map<String, SimpegPangkat> indexes = new HashMap<>();
        for (SimpegPangkat o : source.getPangkat()) {
            int found = 0;
            for (SimpegPangkat ph : source.getPangkat()) {
                if (ph.getGol().equalsIgnoreCase(o.getGol()) && ph.getPangkat().equalsIgnoreCase(o.getPangkat())) {
                    ++found;
                }
            }
            if (found > 1) {
                indexes.put(o.getGol(), o);
            }
        }
        source.getPangkat().removeAll(indexes.values());
        List<SimpegPangkat> newPangkat = source.getPangkat().stream().filter(o -> {
            if (Objects.isNull(employee.getPangkats())) return true;
            for (EmployeePangkatHis ph : employee.getPangkats()) {
                if (ph.getPangkatGolongan().getGolongan().equalsIgnoreCase(o.getGol())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        for (SimpegPangkat o : newPangkat) {
            EmployeePangkatHis pangkat = BeanCopy.copy(o, EmployeePangkatHis.class);
            PangkatGolongan pg = pangkatGolongan(pangkatGolongans, o.getGol());
            if (Objects.nonNull(pg)) pangkat.setPangkatGolongan(pg);
            pangkat.setSumberData(AppConstants.Source.SIMPEG);
            pangkats.add(pangkat);
        }
        employee.setPangkats(pangkats);
    }

    // find pangkat golongan, by gol from api
    private PangkatGolongan pangkatGolongan(List<PangkatGolongan> pangkatGolongans, String gol) {
        for (PangkatGolongan pg : pangkatGolongans) {
            if (Objects.isNull(pg.getGolongan())) continue;
            if (pg.getGolongan().trim().isEmpty()) continue;
            if (pg.getGolongan().equalsIgnoreCase(gol)) {
                return pg;
            }
        }
        return null;
    }

    private void initializeMutasi(SimpegEmployeeRiwayat source, Employee employee) {
        List<EmployeeMutasi> mutasis = employee.getMutasis();
        if (Objects.isNull(mutasis)) mutasis = new ArrayList<>();
        List<SimpegMutasi> newMutasi = source.getMutasi().stream().filter(o -> {
            if (Objects.isNull(employee.getPangkats())) return true;
            for (EmployeeMutasi em : employee.getMutasis()) {
                if (em.getPangkat().equalsIgnoreCase(o.getPangkat()) && em.getTahun().equals(o.getTahun())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        for (SimpegMutasi o : newMutasi) {
            mutasis.add(BeanCopy.copy(o, EmployeeMutasi.class));
        }
        employee.setMutasis(mutasis);
    }
}
