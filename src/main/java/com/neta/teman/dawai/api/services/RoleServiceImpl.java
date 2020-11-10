package com.neta.teman.dawai.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neta.teman.dawai.api.applications.commons.ResourceUtils;
import com.neta.teman.dawai.api.models.dao.Role;
import com.neta.teman.dawai.api.models.mapper.RoleMapper;
import com.neta.teman.dawai.api.models.repository.RoleRepository;
import com.neta.teman.dawai.api.plugins.simpeg.services.SimpegServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Slf4j
@SuppressWarnings({"unchecked", "rawtypes"})
public class RoleServiceImpl extends SimpegServiceImpl {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    RoleRepository roleRepository;

    public void initializeRole() {
        try {
            int[] types = new int[]{Types.INTEGER};
            String values = ResourceUtils.asString(resourceLoader.getResource("classpath:master/role.json"));
            List<Role> roleList = new ObjectMapper().readValue(values, new TypeReference<List<Role>>() {
            });
            for (Role role : roleList) {
                log.info("role data {} - {}", role.getName(), role.getDescription());
                Object[] params = new Object[]{role.getId()};
                String query = "SELECT COUNT(1) FROM APP_ROLE WHERE ID = ?";
                int foundInDB = jdbcTemplate.queryForObject(query, params, Integer.class);
                if (0 == foundInDB) {
                    String sql = "insert into APP_ROLE(id) values (?)";
                    int row = jdbcTemplate.update(sql, params, types);
                    log.info("create new role {}", row);
                }
                // update
                Role roleDB = roleRepository.findById(role.getId()).orElse(null);
                if (Objects.isNull(roleDB)) continue;
                BeanUtils.copyProperties(role, roleDB, "id");
                roleRepository.save(signature(roleDB));
            }
            log.info("\n{}", values);
        } catch (JsonProcessingException e) {
            log.error("error read json role", e);
        }
    }
}