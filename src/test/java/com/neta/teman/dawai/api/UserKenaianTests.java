package com.neta.teman.dawai.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.DTFomat;
import com.neta.teman.dawai.api.models.dao.EmployeePangkatHis;
import com.neta.teman.dawai.api.models.dao.PangkatGolongan;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.response.UserResponse;
import com.neta.teman.dawai.api.models.repository.PangkatGolonganRepository;
import com.neta.teman.dawai.api.services.UserService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
class UserKenaianTests {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    @Autowired
    PangkatGolonganRepository pangkatGolonganRepository;

    @Test
    void naikPangkat() throws JsonProcessingException {
        // remove pensiun and sort by id
        List<PangkatGolongan> pangkatGolongans = pangkatGolonganRepository.findAll().stream().filter(o -> o.getId() != 18L).sorted(Comparator.comparing(PangkatGolongan::getId)).collect(Collectors.toList());
        for (PangkatGolongan pg : pangkatGolongans) {
            log.info("pangkat gol {}, {}", pg.getId(), pg.getGolongan());
        }
        List<UserResponse> userList = userService.findAll().getResult().stream().filter(u -> {
            List<EmployeePangkatHis> his = u.getEmployee().getPangkats();
            return his.size() > 0;
        }).map(u -> {
            UserResponse o = new UserResponse(u);
            List<EmployeePangkatHis> his = o.getPangkats().stream().filter(ep -> ep.getPangkatGolongan().getId() != 18L).sorted(Comparator.comparing(eps -> eps.getPangkatGolongan().getId())).collect(Collectors.toList());
            for(EmployeePangkatHis up: his) {
                log.info("{}, {}, {}", u.getEmployee().getNama(), DTFomat.format(up.getTmt()), up.getPangkatGolongan().getGolongan());
            }
            EmployeePangkatHis firstHis = his.get(his.size() - 1);
            // naik per 4 tahun, get start
            AtomicInteger index = new AtomicInteger(0);
            LocalDate start = o.getTglSKCPNS().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            List<UserResponse.ProyeksiKenaikan> kenaikans = pangkatGolongans.stream().filter(p -> {
                log.info("compare id {}, {}", p.getId(), firstHis.getPangkatGolongan().getId());
                return p.getId() >= firstHis.getPangkatGolongan().getId();
            }).map(p -> {
                LocalDate indexDate = start.plusYears(index.getAndAdd(4));
                UserResponse.ProyeksiKenaikan proyeksi = new UserResponse.ProyeksiKenaikan();
                proyeksi.setTahun(indexDate.getYear());
                proyeksi.setBulan(indexDate.getMonthValue());
                proyeksi.setPangkatGolongan(p);
                return proyeksi;
            }).collect(Collectors.toList());
            o.setProyeksiKenaikans(kenaikans);
            log.info("---------------------------------------------------------------");
//            for (UserResponse.ProyeksiKenaikan up : kenaikans) {
//                log.info("{}, {}, {}, {}", u.getEmployee().getNama(), up.getTahun(), up.getBulan(), up.getPangkatGolongan().getGolongan());
//            }
            return o;
        }).collect(Collectors.toList());
        // filter kenaikan pangkat

    }


}
