package com.neta.teman.dawai.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegAuth;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegEmployeeDataUmum;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegResponse;
import com.neta.teman.dawai.api.plugins.simpeg.services.SimpegService;
import com.neta.teman.dawai.api.services.UserService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
class AuthTests {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    @Test
    void auth() throws JsonProcessingException {
        ServiceResolver<User> response = userService.findByUsernameAndPasswordSimpeg("196406241987032001", "123456");
        log.info(new ObjectMapper().writeValueAsString(response));
//        dataUmum(response.getData().getToken());
    }


    @Test
    void dataUmum() throws JsonProcessingException {
//        try {
//            String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvc2ltcGVnLm1hc21hbmEuaWRcL2F1dGhcL2xvZ2luIiwiaWF0IjoxNjAzNjc4OTI0LCJleHAiOjE2MDQyODM3MjQsIm5iZiI6MTYwMzY3ODkyNCwianRpIjoid3hYRXpYZGRNcUVhN2VJTiIsInN1YiI6MTA0NTMsInBydiI6Ijg3ZTBhZjFlZjlmZDE1ODEyZmRlYzk3MTUzYTE0ZTBiMDQ3NTQ2YWEifQ.6Pfk1-MvU62O2RrfSDFcE-CnbNFAlwoOYx3XflpdEn0";
//            SimpegResponse<SimpegEmployeeDataUmum> response = simpegService.dataUmum(token, "197706242005012002");
//            log.info("response employee");
//            log.info(new ObjectMapper().writeValueAsString(response));
//        } catch (Exception e) {
//            log.error(String.valueOf(e));
//        }
    }

}
