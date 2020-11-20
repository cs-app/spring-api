package com.neta.teman.dawai.api.applications.components;

import com.neta.teman.dawai.api.services.CutiService;
import com.neta.teman.dawai.api.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppScheduler {

    @Autowired
    UserService userService;

    @Autowired
    CutiService cutiService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void middleOfTheNight() {
        userService.updateAllUserData();
        cutiService.initCutiPegawai();
        log.info("schedule task finish");
    }
}
