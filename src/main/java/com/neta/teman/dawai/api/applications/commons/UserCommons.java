package com.neta.teman.dawai.api.applications.commons;

import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.repository.PangkatGolonganRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Slf4j
@Component
public class UserCommons {

    @Autowired
    PangkatGolonganRepository pangkatGolonganRepository;

    public String lastEducation(User user) {
        if (Objects.isNull(user)) return null;
        if (Objects.isNull(user.getEmployee())) return null;
        List<EmployeeEducation> educationList = user.getEmployee().getEducations();
        if (Objects.isNull(educationList)) return null;
        Optional<EmployeeEducation> optional = educationList.stream().max(Comparator.comparing(EmployeeEducation::getGraduated));
        return optional.map(EmployeeEducation::getType).orElse(null);
    }

    public Integer getAgeYear(Date tanggalLahir) {
        if (Objects.isNull(tanggalLahir)) return null;
        LocalDate today = LocalDate.now();
        LocalDate userday = tanggalLahir.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = Period.between(userday, today);
        return diff.getYears();
    }

    public Integer getAgeMonth(Date tanggalLahir) {
        if (Objects.isNull(tanggalLahir)) return null;
        LocalDate today = LocalDate.now();
        LocalDate userday = tanggalLahir.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = Period.between(userday, today);
        return diff.getMonths();
    }

    public Integer tingkatPendidikan(String type) {
        if (Objects.isNull(type)) return null;
        return null;
    }

    public EmployeePangkatHis pensiun(User user, PangkatGolongan pangkatGolongan) {
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) return null;
        if (Objects.isNull(employee.getTanggalLahir())) return null;
        if (Objects.isNull(employee.getJabatanDetail())) return null;
        if (Objects.isNull(employee.getJabatanDetail().getJabatan())) return null;
        LocalDate today = LocalDate.now();
        LocalDate userday = employee.getTanggalLahir().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = Period.between(userday, today);
        log.info("different year from dob {}, user {}", diff.getYears(), user.getEmployee().getNama());
        if (diff.getYears() < 57) return null;
        log.info("last year flag pensiun");
        boolean isPensiun = false;
        for (EmployeePangkatHis his : employee.getPangkats()) {
            PangkatGolongan pg = his.getPangkatGolongan();
            if (Objects.isNull(pg)) continue;
            if (18 == pg.getId()) {
                isPensiun = true;
                break;
            }
        }
        if (!isPensiun) {
            EmployeePangkatHis his = new EmployeePangkatHis();
            his.setSumberData(AppConstants.Source.TEMAN_DAWAI);
            his.setPangkatGolongan(pangkatGolongan);
            log.info("user pensiun {} {}", user.getUsername(), his.getPangkatGolongan());
            return his;
        } else log.info("user already pensiun {}", user.getEmployee().getNama());
        return null;
    }


    public EmployeePangkatHis naikPangkat(User user) {
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) return null;
        if (Objects.isNull(employee.getTanggalLahir())) return null;
        if (Objects.isNull(employee.getJabatanDetail())) return null;
        Jabatan jabatan = employee.getJabatanDetail().getJabatan();
        if (Objects.isNull(jabatan)) return null;
        if (!"P".equalsIgnoreCase(jabatan.getJenisJabatan())) return null;
        List<EmployeePangkatHis> pangkatHis = employee.getPangkats();
        if (Objects.isNull(pangkatHis)) return null;
        if (pangkatHis.isEmpty()) return null;
        Long maxId = 0L;
        AtomicReference<EmployeePangkatHis> lastPangkat = new AtomicReference<>();
        for (EmployeePangkatHis his : pangkatHis) {
            PangkatGolongan pangkatGolongan = his.getPangkatGolongan();
            if (Objects.isNull(pangkatGolongan)) continue;
            // 18 = id pensiun
            if (pangkatGolongan.getId() == 18L) continue;
            if (maxId < pangkatGolongan.getId()) {
                maxId = pangkatGolongan.getId();
                lastPangkat.set(his);
            }
        }
        if (maxId == 0L) return null;
        if (maxId == 17) return null;
        EmployeePangkatHis last = lastPangkat.get();
        if (Objects.isNull(last) || Objects.isNull(last.getTmt())) return null;
        LocalDate today = LocalDate.now();
        LocalDate userday = employee.getTanggalLahir().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = Period.between(userday, today);
        if (diff.getYears() < 4) return null;
        PangkatGolongan pangkatGolongan = pangkatGolonganRepository.findById((maxId + 1L)).orElse(null);
        if (Objects.isNull(pangkatGolongan)) return null;
        EmployeePangkatHis his = new EmployeePangkatHis();
        his.setSumberData(AppConstants.Source.TEMAN_DAWAI);
        his.setPangkatGolongan(pangkatGolongan);
        return his;
    }

    public EmployeePangkatHis pangkatSebelumNaik(User user) {
        List<EmployeePangkatHis> pangkatHis = user.getEmployee().getPangkats();
        if (pangkatHis.size() == 1) return pangkatHis.get(0);
        return pangkatHis.get(pangkatHis.size() - 2);
    }

    public EmployeePangkatHis pangkatNaik(User user) {
        List<EmployeePangkatHis> pangkatHis = user.getEmployee().getPangkats();
        if (pangkatHis.size() == 1) return pangkatHis.get(0);
        return pangkatHis.get(pangkatHis.size() - 1);
    }
}
