package com.neta.teman.dawai.api;

import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.Role;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.mapper.RoleMapper;
import com.neta.teman.dawai.api.models.repository.RoleRepository;
import com.neta.teman.dawai.api.models.repository.UserRepository;
import com.neta.teman.dawai.api.models.spech.UserSpecs;
import com.neta.teman.dawai.api.services.CutiService;
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
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CutiService cutiService;

    public static void main(String[] args) {
        SpringApplication.run(TemanDawaiApiApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        initRole();
//        initUser();
//        initCutiPegawai();
        cutiService.initCutiPegawai();
    }

    @Transactional
    private void initRole() {
        String query = "SELECT * FROM APP_ROLE WHERE ID = ?";
        Role role = new Role();
        try {
            role = jdbcTemplate.queryForObject(query, new Object[]{1L}, new RoleMapper());
        } catch (EmptyResultDataAccessException e) {
            log.error("role not found");
        }
        if (Objects.nonNull(role.getId())) {
            role = roleRepository.findById(role.getId()).get();
        }
        role.setName("ADMIN");
        roleRepository.save(role);
    }

    /**
     * initialize role, user and employee
     */
    @Transactional
    private void initUser() {
        User userExist = userRepository.findOne(UserSpecs.nip("100100100")).orElse(null);
        if (Objects.nonNull(userExist)) return;
        User user = new User();
        user.setPassword(DigestUtils.md5Hex("123456"));

        // employee
        Employee employee = new Employee();
        employee.setNip("100100100");
        user.setEmployee(employee);

        // role
        Role role = roleRepository.findById(1L).get();
        user.setRole(role);

        userRepository.save(user);
    }

    private void initCutiPegawai() {

    }
}
