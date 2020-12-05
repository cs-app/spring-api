package com.neta.teman.dawai.api.models.converter;

import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.applications.commons.DTFomat;
import com.neta.teman.dawai.api.applications.commons.FamilyCommons;
import com.neta.teman.dawai.api.applications.commons.UserCommons;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.reports.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReportConverter {

    @Autowired
    UserCommons userCommons;

    @Autowired
    FamilyCommons familyCommons;

    public List<Profile> defaultDatasource(Map map, User user) {
        List<Profile> profiles = new ArrayList<>();
        Employee employee = user.getEmployee();
        newProfile(profiles, "1   NIP", employee.getNip());
        return profiles;
    }

    public List<Profile> profile(Map map, User user) {
        List<Profile> profiles = new ArrayList<>();
        Employee employee = user.getEmployee();
        newProfile(profiles, "1   NIP", employee.getNip());
        newProfile(profiles, "2   Nama", employee.getNama());
        newProfile(profiles, "3   Status Pegawai (Aktif/Pensiun/Pindah/CTLN)", employee.getStatusAktif());
        newProfile(profiles, "4   Tempat, Tanggal Lahir", map.get("tempat_lahir") + ", " + DTFomat.format(employee.getTanggalLahir()));
        newProfile(profiles, "5   Alamat Tinggal", employee.getAlamat());
        newProfile(profiles, "6   No. KTP", employee.getNik());
        newProfile(profiles, "7   No. KK", employee.getKk());
        newProfile(profiles, "8   Jenis Kelamin", employee.getKelamin());
        newProfile(profiles, "9   Status Perkawinan", employee.getStatusDiri());
        String tglPernikahan = "";
        List<EmployeeFamily> families = employee.getFamilies();
        for (EmployeeFamily f : families) {
            if (f.getType() == 1) {
                tglPernikahan = DTFomat.format(f.getMob());
                break;
            }
        }
        newProfile(profiles, "10  Tgl. Pernikahan", tglPernikahan);
        List<EmployeeContact> contacts = user.getEmployee().getContacts();
        String noTlp = "";
        String email = "";
        for (EmployeeContact c : contacts) {
            if (c.getType() == 1) {
                noTlp = c.getValue();
            } else email = c.getValue();
        }
        newProfile(profiles, "11  No. HP", noTlp);
        newProfile(profiles, "12  Email", email);
        newProfile(profiles, "13  Perkiraan Pensiun", employee.getPerkiraanPensiun());
        // orang tua
        /*
         * 1 = pasangan = suami, istri
         * 2 = anak = anak
         * 3 = orangtua = bapakkandung, ibukandung
         * 4 = mertua = bapakmertua, ibumertua
         * 5 = saudara = saudarakandung
         * */
        List<Profile> profilesOrangTua = new ArrayList<>();
        List<Profile> profilesPasangan = new ArrayList<>();
        List<Profile> profilesAnak = new ArrayList<>();
        List<Profile> profilesMertua = new ArrayList<>();
        List<Profile> profilesSaudara = new ArrayList<>();
        List<EmployeeFamily> familyList = user.getEmployee().getFamilies();
        int seqSaudara = 1;
        int seqKeluarga = 2;
        for (EmployeeFamily e : familyList) {
            if (1 == e.getType()) {
                profilesPasangan.add(new Profile(appendSpace(6) + "1a. Nama Istri/Suami", e.getName()));
                profilesPasangan.add(new Profile(appendSpace(6) + "1b. Pekerjaan", e.getOccupation()));
            }
            if (2 == e.getType()) {
                profilesAnak.add(new Profile(appendSpace(6) + seqKeluarga + "a. Nama", e.getName()));
                profilesAnak.add(new Profile(appendSpace(6) + seqKeluarga + "b. Pekerjaan", e.getOccupation()));
                seqKeluarga++;
            }
            if (3 == e.getType()) {
                // ayah
                if ("BAPAKKANDUNG".equalsIgnoreCase(e.getFamilyStatus())) {
                    profilesOrangTua.add(new Profile(appendSpace(6) + "1a. Nama Ayah", e.getName()));
                    profilesOrangTua.add(new Profile(appendSpace(6) + "1b. Pekerjaan", e.getOccupation()));
                }
                if ("IBUKANDUNG".equalsIgnoreCase(e.getFamilyStatus())) {
                    profilesOrangTua.add(new Profile(appendSpace(6) + "2a. Nama Ibu", e.getName()));
                    profilesOrangTua.add(new Profile(appendSpace(6) + "2b. Pekerjaan", e.getOccupation()));
                }

            }
            if (4 == e.getType()) {

            }
            if (5 == e.getType()) {
                if ("SAUDARAKANDUNG".equalsIgnoreCase(e.getFamilyStatus())) {
                    profilesSaudara.add(new Profile(appendSpace(6) + seqSaudara + "a. Nama", e.getName()));
                    profilesSaudara.add(new Profile(appendSpace(6) + seqSaudara + "b. Pekerjaan", e.getOccupation()));
                    seqSaudara++;
                }
            }
        }
        newProfile(profiles, "14  Orang Tua", "");
        profilesOrangTua.sort(Comparator.comparing(Profile::getTitle));
        profiles.addAll(profilesOrangTua);
        newProfile(profiles, "15  Saudara Kandung", "");
        profilesSaudara.sort(Comparator.comparing(Profile::getTitle));
        profiles.addAll(profilesSaudara);
        newProfile(profiles, "16  Keluarga", "");
        profilesAnak.sort(Comparator.comparing(Profile::getTitle));
        profilesPasangan.addAll(profilesAnak);
        profiles.addAll(profilesPasangan);
        return profiles;
    }

    private String appendSpace(int total) {
        String space = "\u00A0";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < total; i++) {
            sb.append(space);
        }
        return sb.toString();
    }

    private void newProfile(List<Profile> profiles, String key, Object val) {
        if (Objects.isNull(val)) {
            profiles.add(new Profile(key, ""));
            return;
        }
        profiles.add(new Profile(key, String.valueOf(val)));
    }

    private void newProfile(List<Profile> profiles, String num, String key, Object val) {
        if (Objects.isNull(val)) {
            profiles.add(new Profile(num, key, ""));
            return;
        }
        profiles.add(new Profile(num, key, String.valueOf(val)));
    }

    public Collection<?> family(User user) {
        List<EmployeeFamily> employeeFamilies = user.getEmployee().getFamilies();
        if (Objects.isNull(employeeFamilies)) return new ArrayList<>();
        for (EmployeeFamily o : employeeFamilies) {
            if ("SAUDARAKANDUNG".equalsIgnoreCase(o.getFamilyStatus())) {
                o.setFamilyStatus("SAUDARA KANDUNG");
            }
            if ("IBUMERTUA".equalsIgnoreCase(o.getFamilyStatus())) {
                o.setFamilyStatus("IBU MERTUA");
            }
            if ("BAPAKMERTUA".equalsIgnoreCase(o.getFamilyStatus())) {
                o.setFamilyStatus("BAPAK MERTUA");
            }
            if ("IBUKANDUNG".equalsIgnoreCase(o.getFamilyStatus())) {
                o.setFamilyStatus("IBU KANDUNG");
            }
            if ("BAPAKKANDUNG".equalsIgnoreCase(o.getFamilyStatus())) {
                o.setFamilyStatus("BAPAK KANDUNG");
            }
        }
        employeeFamilies.sort(Comparator.comparing(EmployeeFamily::getType));//.thenComparingInt(EmployeeFamily::getAge));

        return employeeFamilies;
    }

    public Collection<?> container() {
        ArrayList<Profile> detail = new ArrayList<>();
        detail.add(new Profile("P", "Q"));
        return detail;

    }

    public Collection<?> education(User user) {
        List<EmployeeEducation> education = user.getEmployee().getEducations();
        if (Objects.isNull(education)) return new ArrayList<>();
        List<ProfileEducation> educations = BeanCopy.copyCollection(education, ProfileEducation.class);
        AtomicInteger index = new AtomicInteger();
        return educations.stream().map(o -> {
            o.setRow(index.incrementAndGet());
            return o;
        }).collect(Collectors.toList());
    }

    public Collection<?> mutasi(User user) {
        List<EmployeeMutasi> education = user.getEmployee().getMutasis();
        if (Objects.isNull(education)) return new ArrayList<>();
        AtomicInteger index = new AtomicInteger();
        return education.stream().map(o -> new ProfileMutasiPangkatJabatanPelatihan(index.incrementAndGet(), o)).collect(Collectors.toList());
    }

    public Collection<?> pangkat(User user) {
        List<EmployeePangkatHis> education = user.getEmployee().getPangkats();
        if (Objects.isNull(education)) return new ArrayList<>();
        AtomicInteger index = new AtomicInteger();
        return education.stream().map(o -> new ProfilePangkat(index.incrementAndGet(), o)).collect(Collectors.toList());
    }

    public Collection<?> jabatan(User user) {
        List<EmployeePangkatHis> education = user.getEmployee().getPangkats();
        if (Objects.isNull(education)) return new ArrayList<>();
        AtomicInteger index = new AtomicInteger();
        return education.stream().map(o -> new ProfileMutasiPangkatJabatanPelatihan(index.incrementAndGet(), o.getPangkatGolongan())).collect(Collectors.toList());
    }

    public Collection<?> pelatihanJabatan(User user) {
        List<EmployeePelatihan> education = user.getEmployee().getPelatihans().stream().filter(o -> "JABATAN".equalsIgnoreCase(o.getType())
        ).collect(Collectors.toList());
        AtomicInteger index = new AtomicInteger();
        return education.stream().map(o -> new ProfilePelatihan(index.incrementAndGet(), o)).collect(Collectors.toList());
    }

    public Collection<?> pelatihanTeknis(User user) {
        List<EmployeePelatihan> education = user.getEmployee().getPelatihans().stream().filter(o -> "TEKNIS".equalsIgnoreCase(o.getType())
        ).collect(Collectors.toList());
        AtomicInteger index = new AtomicInteger();
        return education.stream().map(o -> new ProfilePelatihan(index.incrementAndGet(), o)).collect(Collectors.toList());
    }

    public Collection<?> pendukung(User user) {
        List<Profile> profiles = new ArrayList<>();
        Employee employee = user.getEmployee();
        newProfile(profiles, "1." + appendSpace(1) + "ID Karpeg", employee.getNoKarpeg());
        newProfile(profiles, "2." + appendSpace(1) + "NPWP", employee.getNpwp());
        newProfile(profiles, "3." + appendSpace(1) + "ID Taspen", employee.getNoTaspen());
        newProfile(profiles, "4." + appendSpace(1) + "ID Karis", employee.getNoKarisSu());
        return profiles;
    }

    public Collection<?> duk(List<Employee> employees) {
        List<Duk> profiles = new ArrayList<>();
        for (Employee o : employees) {
            Duk duk = BeanCopy.copy(o, Duk.class);
            if (Objects.nonNull(o.getPangkatDetail())) {
                duk.setGolongan(o.getPangkatDetail().getGol());
            }
            duk.setTmtGol(DTFomat.format(o.getTmtGol()));
            duk.setMasaWaktuDiPangkat(toReadable(o.getTmtGol()));
            duk.setTmtJabatan(DTFomat.format(o.getTmtJabatan()));
            duk.setMasaKerja(toReadable(o.getTmtJabatan()));
            duk.setTmtCpns(DTFomat.format(o.getTglMulaiCPNS()));
            duk.setMasaKerjaTahunDanBulan(toReadable(o.getTglMulaiCPNS()));
            // pendidikan
            if (o.getEducations().size() > 0) {
                EmployeeEducation education = o.getEducations().get(o.getEducations().size() - 1);
                duk.setPendidikan(education.getType());
                duk.setPendidikanInstansi(education.getValue());
                duk.setPendidikanJurusan(education.getMajors());
            }
            // tanggal lahir
            duk.setTanggalLahir(DTFomat.format(o.getTanggalLahir()));
            duk.setPensiunTMT(toPensiunDate(o.getTanggalLahir()));
            duk.setPensiunTahun(toPensiun(o.getTanggalLahir()));
            profiles.add(duk);
        }
        return profiles;
    }

    private String toPensiun(Date date) {
        if (Objects.isNull(date)) return null;
        LocalDate userday = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().plusYears(58);
        return "" + userday.getYear();
    }

    private String toPensiunDate(Date date) {
        if (Objects.isNull(date)) return null;
        LocalDate userday = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate().plusYears(58);
        Date datePensiun = Date.from(userday.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return DTFomat.format(datePensiun);
    }

    private String toReadable(Date date) {
        if (Objects.isNull(date)) return null;
        LocalDate today = LocalDate.now();
        LocalDate userday = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();//LocalDate.of(2015, Month.MAY, 15);
        Period diff = Period.between(userday, today);
        StringBuilder sb = new StringBuilder();
        sb.append(diff.getYears()).append(" Tahun ");
        sb.append(diff.getMonths()).append(" Bulan ");
        return sb.toString();
    }

    public Collection<?> skp(User user) {
        List<EmployeeSKP> education = user.getEmployee().getSkps();
        AtomicInteger index = new AtomicInteger();
        return education.stream().map(o -> new ProfilePelatihan(index.incrementAndGet(), o)).collect(Collectors.toList());
    }

    public Collection<?> credit(User user) {
        List<EmployeeCreditScore> education = user.getEmployee().getCreditScores();
        AtomicInteger index = new AtomicInteger();
        return education.stream().map(o -> new ProfilePelatihan(index.incrementAndGet(), o)).collect(Collectors.toList());
    }

    public Collection<?> lancana(User user) {
        List<EmployeeSatyaLencana> education = user.getEmployee().getSatyaLencanas();
        AtomicInteger index = new AtomicInteger();
        return education.stream().map(o -> new ProfilePelatihan(index.incrementAndGet(), o)).collect(Collectors.toList());
    }

    public Collection<?> disiplin(User user) {
        List<EmployeeHukumanDisiplin> education = user.getEmployee().getHukumanDisiplins();
        AtomicInteger index = new AtomicInteger();
        return education.stream().map(o -> new ProfilePelatihan(index.incrementAndGet(), o)).collect(Collectors.toList());
    }

    public Collection<?> cuti(List<CutiSummary> cutiSummaries) {
        AtomicInteger index = new AtomicInteger();
        return cutiSummaries.stream().map(o -> new ProfilePelatihan(index.incrementAndGet(), o)).collect(Collectors.toList());
    }

    public Collection<?> pensiun(User user) {
        List<ProfilePelatihan> result = new ArrayList<>();
        AtomicInteger index = new AtomicInteger();
        ProfilePelatihan pp = new ProfilePelatihan();
        pp.setGolongan("TMT Pensiun");
        if (Objects.isNull(user.getEmployee().getTanggalLahir())) {
            pp.setPangkat("");
        } else {
            LocalDate today = LocalDate.now();
            LocalDate userday = user.getEmployee().getTanggalLahir().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate userpen = userday.plusYears(58);
            Period diff = Period.between(today, userpen);
            if (diff.getYears() >= 0) {
                pp.setPangkat(":" + appendSpace(1) + DTFomat.format(userpen) + " (" + diff.getYears() + " Tahun, " + diff.getMonths() + " Bulan)");
            } else {
                pp.setPangkat(":" + appendSpace(1) + DTFomat.format(userpen));
            }
        }
        result.add(pp);
        return result;
//        LocalDate date = user
        //return cutiSummaries.stream().map(o -> new ProfilePelatihan(index.incrementAndGet(), o)).collect(Collectors.toList());
        //return null;
    }

    public Collection<?> blanko3ProfileFamilyAnak(Map<String, Object> userMapOrigin, User user) {
        List<MasterCol> masterCols = new ArrayList<>();
        if (Objects.isNull(user.getEmployee())) return masterCols;
        List<EmployeeFamily> employeeFamilies = user.getEmployee().getFamilies();
        if (Objects.isNull(employeeFamilies)) return masterCols;
        int index = 1;
        for (EmployeeFamily fm : employeeFamilies) {
            masterCols.add(new MasterCol("" + index, fm.getName(), familyCommons.alias(fm.getFamilyStatus()), DTFomat.format(fm.getDob()), fm.getOccupation(), DTFomat.format(fm.getMob()), ""));
            index++;
        }
        return masterCols;
    }

    public Collection<?> blanko5Profile(Map<String, Object> map, User user) {
        List<Profile> profiles = new ArrayList<>();
        Employee employee = user.getEmployee();
        newProfile(profiles, "A.", "NAMA", ": " + employee.getNama());
        newProfile(profiles, "B.", "NIP", ": " + employee.getNip());
        newProfile(profiles, "C.", "TEMPAT, TANGGAL LAHIR", ": " + map.get("tempat_lahir") + ", " + DTFomat.format(employee.getTanggalLahir()));
        newProfile(profiles, "D.", "JABATAN PEKERJAAN", ": " + employee.getJabatan());
        newProfile(profiles, "E.", "GAJI POKOK TERAKHIR", ": ");
        newProfile(profiles, "F.", "MASA KERJA GOLONGAN", ": ");
        newProfile(profiles, "G.", "MASA KERJA PENSIUN", ": ");
        newProfile(profiles, "H.", "MASA KERJA SEBELUM DIANGKAT PNS", ": ");
        newProfile(profiles, "I.", "PENDIDIKAN SEBAGAI DASAR PENGANGKATAN PERTAMA MULAI MASUK PNS", ": ");
        newProfile(profiles, "J.", "KENAIKAN PANGKAT", ": " + "");
        return profiles;
    }

    public Collection<?> blanko5ProfileFamilyPasangan(Map<String, Object> userMapOrigin, User user) {
        List<MasterCol> masterCols = new ArrayList<>();
        if (Objects.isNull(user.getEmployee())) return masterCols;
        List<EmployeeFamily> employeeFamilies = user.getEmployee().getFamilies();
        if (Objects.isNull(employeeFamilies)) return masterCols;
        int index = 1;
        for (EmployeeFamily fm : employeeFamilies) {
            if (fm.getType() == 1) {
                masterCols.add(new MasterCol("" + index, fm.getName(), DTFomat.format(fm.getDob()), DTFomat.format(fm.getMob()), fm.getFamilyStatus() + " Ke: " + index));
                index++;
            }
        }
        return masterCols;
    }

    public Collection<?> blanko5ProfileFamilyAnak(Map<String, Object> userMapOrigin, User user) {
        List<MasterCol> masterCols = new ArrayList<>();
        if (Objects.isNull(user.getEmployee())) return masterCols;
        List<EmployeeFamily> employeeFamilies = user.getEmployee().getFamilies();
        if (Objects.isNull(employeeFamilies)) return masterCols;
        int index = 1;
        for (EmployeeFamily fm : employeeFamilies) {
            if (fm.getType() == 2) {
                masterCols.add(new MasterCol("" + index, fm.getName(), "✓", "", ""));
                index++;
            }
        }
        return masterCols;
    }

    public Collection<?> naikPangkat(List<User> users) {
        List<MasterCol> masterCols = new ArrayList<>();
        int index = 1;
        for (User user : users) {
            EmployeePangkatHis before = userCommons.pangkatSebelumNaik(user);
            EmployeePangkatHis after = userCommons.pangkatSebelumNaik(user);
            Employee employee = user.getEmployee();
            PangkatGolongan beforePG = before.getPangkatGolongan();
            PangkatGolongan afterPG = after.getPangkatGolongan();
            if (Objects.isNull(beforePG) && Objects.isNull(afterPG)) {
                masterCols.add(new MasterCol("" + index, employee.getNama(), employee.getNip(), "", "", "", "", employee.getJabatan()));
                index++;
                continue;
            } else if (Objects.isNull(beforePG)) {
                masterCols.add(new MasterCol("" + index, employee.getNama(), employee.getNip(), "", "", afterPG.getGolongan(), afterPG.getNama(), employee.getJabatan()));
                index++;
                continue;
            } else if (Objects.isNull(afterPG)) {
//                index++;
                continue;
            } else {
                masterCols.add(new MasterCol("" + index, employee.getNama(), employee.getNip(), beforePG.getGolongan(), beforePG.getNama(), afterPG.getGolongan(), afterPG.getNama(), employee.getJabatan()));
                index++;
            }
        }
        return masterCols;
    }
}
