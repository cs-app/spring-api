package com.neta.teman.dawai.api.models.payload.response;

import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.Role;
import com.neta.teman.dawai.api.models.dao.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class LoginResponse {

    private String nip;

    private String name;

    private Long roleId;

    private String role;

    private String jabatan;

    public LoginResponse(User user) {
        Role role = user.getRole();
        Employee employee = user.getEmployee();
        BeanUtils.copyProperties(user, this);
        BeanUtils.copyProperties(role, this);
        BeanUtils.copyProperties(employee, this);
//        this.nip = employee.getNip();
//        this.name = employee.getNama();
//        if (Objects.nonNull(role)) {
//            this.roleId = role.getId();
//            this.role = role.getName();
//        }
    }

}
