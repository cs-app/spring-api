package com.neta.teman.dawai.api.models.mapper;

import com.neta.teman.dawai.api.models.dao.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong(1));
        return role;
    }
}
