package com.neta.teman.dawai.api.applications.commons;

import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.repository.PangkatGolonganRepository;
import com.neta.teman.dawai.api.models.repository.UserRepository;
import com.neta.teman.dawai.api.models.spech.UserSpecs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserCommons {

    @Autowired
    PangkatGolonganRepository pangkatGolonganRepository;

    @Autowired
    UserSpecs userSpecs;

    @Autowired
    UserRepository userRepository;

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
        LocalDate today = LocalDate.now();
        LocalDate userday = employee.getTanggalLahir().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = Period.between(userday, today);
        log.info("different year from dob {}, user {}", diff.getYears(), user.getEmployee().getNama());
        if (diff.getYears() < 57) return null;
        log.info("last year flag pensiun");
        for (EmployeePangkatHis his : employee.getPangkats()) {
            PangkatGolongan pg = his.getPangkatGolongan();
            if (Objects.isNull(pg)) continue;
            if (Objects.equals(pg.getId(), pangkatGolongan.getId())) {
                log.info("user already pensiun {}", user.getUsername());
                return null;
            }
        }
        LocalDate localDate = userday.plusYears(58);
        EmployeePangkatHis his = new EmployeePangkatHis();
        his.setSumberData(AppConstants.Source.TEMAN_DAWAI);
        his.setTmt(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        his.setPangkatGolongan(pangkatGolongan);
        log.info("user pensiun {} {}", user.getUsername(), his.getPangkatGolongan());
        return his;
    }

    /**
     * get last pangkat
     */
    public EmployeePangkatHis naikPangkat(User user) {
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) return null;
        if (Objects.isNull(employee.getTanggalLahir())) return null;
        if (Objects.isNull(employee.getJabatanDetail())) return null;
        Jabatan jabatan = employee.getJabatanDetail().getJabatan();
        if (Objects.isNull(jabatan)) return null;
        if (!"P".equalsIgnoreCase(jabatan.getJenisJabatan())) return null;
        List<EmployeePangkatHis> pangkatHis = employee.getPangkats().stream().filter(o -> o.getId() != 18L).collect(Collectors.toList());
        if (pangkatHis.isEmpty()) return null;
        EmployeePangkatHis lastHis = pangkatHis.get(pangkatHis.size() - 1);
        if (Objects.isNull(lastHis) || Objects.isNull(lastHis.getTmt())) return null;
        LocalDate today = LocalDate.now();
        LocalDate userday = new Date(lastHis.getTmt().getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period diff = Period.between(userday, today);
        log.info("diff month {}", diff.getMonths());
        if (diff.getMonths() < 45) return null; // 3 bulan sebelum
        String educationType = lastEducation(user);
        // max S1 = iii/d, atau id = 12
        if (educationType.contains("S1") && lastHis.getPangkatGolongan().getId() < 12) {
            // stop process S1
            LocalDate indexDate = userday.plusYears(4);
            EmployeePangkatHis tmpHis = new EmployeePangkatHis();
            tmpHis.setTmt(Date.from(indexDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            tmpHis.setSumberData(AppConstants.Source.TEMAN_DAWAI);
            tmpHis.setPangkatGolongan(pangkatGolonganRepository.getOne(lastHis.getPangkatGolongan().getId() + 1));
            log.info("user {} pendidikan {} naik golongan {}", employee.getNama(), educationType, tmpHis.getPangkatGolongan().getGolongan());
            return tmpHis;

        }
        if (educationType.contains("S2") && lastHis.getPangkatGolongan().getId() < 13) {
            // stop process S2
            LocalDate indexDate = userday.plusYears(4);
            EmployeePangkatHis tmpHis = new EmployeePangkatHis();
            tmpHis.setTmt(Date.from(indexDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            tmpHis.setSumberData(AppConstants.Source.TEMAN_DAWAI);
            tmpHis.setPangkatGolongan(pangkatGolonganRepository.getOne(lastHis.getPangkatGolongan().getId() + 1));
            log.info("user {} pendidikan {} naik golongan {}", employee.getNama(), educationType, tmpHis.getPangkatGolongan().getGolongan());
            return tmpHis;
        } else {
            log.info("{} {} sudah mencapai batas maksimum {}, {}", user.getUsername(), employee.getNama(), educationType, lastHis.getPangkatGolongan().getGolongan());
        }
        return null;
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
