package com.neta.teman.dawai.api;

import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.Role;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.repository.RoleRepository;
import com.neta.teman.dawai.api.models.repository.UserRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.TestInstance;

import java.util.Objects;

public class TestAdminConstant {

    public static String ROLE_ADMIN = "ADMIN";
    public static String USERNAME = "user.admin@test.com";
    public static String NIK = "100000000000";
    public static String NIP = "100000000000";

    private static Role defaultRoleAdmin() {
        Role role = new Role();
        role.setName(ROLE_ADMIN);
        return role;
    }

    private static User defaultUserAdmin(Role role) {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(DigestUtils.md5Hex("password"));
        user.setRole(role);
        Employee employee = new Employee();
        employee.setNama("TEST");
        employee.setNip(NIP);
        user.setEmployee(employee);
        return user;
    }

    public static User roleAnduserIfNotExist(RoleRepository roleRepository, UserRepository userRepository) {
        // role
        Role role = roleRepository.findByName(ROLE_ADMIN);
        if (Objects.isNull(role)) {
            role = roleRepository.save(defaultRoleAdmin());
        }
        User user = userRepository.findByUsername(USERNAME);
        if (Objects.isNull(user)) {
            return userRepository.save(defaultUserAdmin(role));
        }
        return user;
    }
}
