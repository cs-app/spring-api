package com.neta.teman.dawai.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.DTFomat;
import com.neta.teman.dawai.api.applications.commons.UserCommons;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.dao.EmployeeEducation;
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
    UserCommons userCommons;

    @Autowired
    UserService userService;

    @Autowired
    PangkatGolonganRepository pangkatGolonganRepository;

    @Test
    void naikPangkat() throws JsonProcessingException {
        List<UserResponse> result = userService.proyeksiPangkat(2021, 3).getResult();
        // remove pensiun and sort by id

        for (UserResponse tmp : result) {
            log.info("------------------------------------------------------------------");
            for (EmployeePangkatHis his : tmp.getPangkats()) {
                log.info("{} pangkat {} {}", tmp.getNama(), DTFomat.format(his.getTmt()), his.getPangkatGolongan().getGolongan());
            }
        }

    }


}
