package com.neta.teman.dawai.api;

import com.neta.teman.dawai.api.services.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:test.properties")
public class StartupTest {

    @Autowired
    RoleService roleService;

    @Test
    void initializeRoleTest() {
        roleService.initializeRole();
    }
}
