package com.neta.teman.dawai.api;

import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.Role;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.mapper.RoleMapper;
import com.neta.teman.dawai.api.models.repository.RoleRepository;
import com.neta.teman.dawai.api.models.repository.UserRepository;
import com.neta.teman.dawai.api.models.spech.UserSpecs;
import com.neta.teman.dawai.api.plugins.simpeg.services.SimpegService;
import com.neta.teman.dawai.api.services.CutiService;
import com.neta.teman.dawai.api.services.RoleService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@SpringBootApplication
public class TemanDawaiApiApplication implements ApplicationRunner {

    @Autowired
    CutiService cutiService;

    @Autowired
    RoleService roleService;

    public static void main(String[] args) {
        SpringApplication.run(TemanDawaiApiApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        roleService.initializeRole();
        cutiService.initCutiPegawai();
    }
}
