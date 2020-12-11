package com.neta.spring.api.applications.components;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppScheduler {

    @Scheduled(cron = "0 0 0 * * ?")
    public void middleOfTheNight() {
        log.info("schedule task finish");
    }
}
