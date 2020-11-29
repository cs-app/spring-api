package com.neta.teman.dawai.api;

import com.neta.teman.dawai.api.applications.commons.UserCommons;
import com.neta.teman.dawai.api.services.CutiService;
import com.neta.teman.dawai.api.services.DocumentService;
import com.neta.teman.dawai.api.services.ReportService;
import com.neta.teman.dawai.api.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class TemanDawaiApiApplication implements ApplicationRunner {

    @Autowired
    UserCommons userCommons;

    @Autowired
    CutiService cutiService;

    @Autowired
    UserService userService;

    @Autowired
    ReportService reportService;

    @Autowired
    DocumentService documentService;

    public static void main(String[] args) {
        SpringApplication.run(TemanDawaiApiApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        reportService.initTemplate();
        documentService.initDocument();
        userService.initializeRole();
        userService.initializePangkatGolongan();
        userService.initializeJabatan();
        cutiService.initCutiPegawai();
        userService.updateAllUserData();
        userService.initializeNaikPangkatAndPensiun();
    }
}
