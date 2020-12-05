package com.neta.teman.dawai.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.neta.teman.dawai.api.models.dao.Cuti;
import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.repository.CutiRepository;
import com.neta.teman.dawai.api.models.repository.EmployeeRepository;
import com.neta.teman.dawai.api.services.ReportService;
import com.neta.teman.dawai.api.services.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:test.properties")
class ReportTests {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    UserService userService;

    @Autowired
    ReportService reportService;

    @Autowired
    CutiRepository cutiRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @BeforeAll
    void generatedTable() {
        reportService.initTemplate();
    }

    @Test
    void printCV() throws JsonProcessingException {
        reportService.printCV("196406241987032001", null);
    }

    @Test
    void printCuti() throws JsonProcessingException {
        User user = userService.findByNip("196406241987032001").getResult();
        List<Cuti> cuti = cutiRepository.findAllByUser(user);
        reportService.printCuti(user, cuti.get(0), null);
    }

    @Test
    void printDUK() throws JsonProcessingException {
        List<Employee> employees = employeeRepository.findAll();
        reportService.printDUK(employees, null);
    }

    @Test
    void printPensiunAjuan() throws JsonProcessingException {
        User user = userService.findByNip("196406241987032001").getResult();
        reportService.printPensiunAjuan(user, null);
    }

    @Test
    void printPensiunBlanko1() throws JsonProcessingException {
        User user = userService.findByNip("196406241987032001").getResult();
        reportService.printBlanko1(user, null);
    }

    @Test
    void printPensiunBlanko2() throws JsonProcessingException {
        User user = userService.findByNip("196406241987032001").getResult();
        reportService.printBlanko2(user, null);
    }

    @Test
    void printPensiunBlanko3() throws JsonProcessingException {
        User user = userService.findByNip("196406241987032001").getResult();
        reportService.printBlanko3(user, null);
    }

    @Test
    void printPensiunBlanko4() throws JsonProcessingException {
        User user = userService.findByNip("196406241987032001").getResult();
        reportService.printBlanko4(user, null);
    }

    @Test
    void printPensiunBlanko5() throws JsonProcessingException {
        User user = userService.findByNip("196406241987032001").getResult();
        reportService.printBlanko5(user, null);
    }

    @Test
    void printPensiunNaikPangkat() throws JsonProcessingException {
        reportService.printNaikPangkat(2020, null);
    }


}
