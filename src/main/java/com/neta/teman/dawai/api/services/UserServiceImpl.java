package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.converter.SimpegConverter;
import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.Role;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.repository.RoleRepository;
import com.neta.teman.dawai.api.models.repository.UserRepository;
import com.neta.teman.dawai.api.models.spech.UserSpecs;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegAuth;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegEmployee;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegResponse;
import com.neta.teman.dawai.api.plugins.simpeg.services.SimpegServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends SimpegServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public User findByUsernameAndPassword(String nip, String password) {
        return userRepository.findOne(UserSpecs.login(nip, password)).orElse(null);
    }

    /**
     * create new role from server simpeg
     */
    @Override
    public ServiceResolver<User> findByUsernameAndPasswordSimpeg(String username, String password) {
        SimpegResponse<SimpegAuth> simpegResponse = auth(username, password);
        if (!simpegResponse.isStatus()) {
            return error(1001, simpegResponse.getMessage());
        }
        SimpegAuth simpegAuth = simpegResponse.getData();
        simpegAuth.setNip("197706242005012002"); // debug
        if (isNull(simpegAuth.getRole(), simpegAuth.getUsername(), simpegAuth.getToken(), simpegAuth.getNip())) {
            return error(1002, "Invalid user!");
        }
        // get role, auto create when role not found in system
        Role role = createRoleIfNotExist(simpegAuth.getRole());
        User user = createUserIfNotExist(simpegAuth, role);
        return success(user);
    }

    private Role createRoleIfNotExist(String name) {
        Role role = roleRepository.findByName(name);
        if (nonNull(role)) return role;
        // create new role
        Role newRole = new Role();
        newRole.setName(name);
        return roleRepository.save(newRole);
    }

    private User createUserIfNotExist(SimpegAuth simpegAuth, Role role) {
        User user = userRepository.findByUsername(simpegAuth.getUsername());
        if (nonNull(user)) {
            // debug only
//            userRepository.delete(user);
            return user;
        }
        // employee
        Employee employee = updateDataUmumEmployee(simpegAuth);
        employee.setNip(simpegAuth.getNip());
        // picture
        if (nonNull(simpegAuth.getPersonal()) && nonNull(simpegAuth.getPersonal().getPhoto())) {
            employee.setPicture(simpegAuth.getPersonal().getPhoto());
        }
        // user
        User newUser = new User();
        newUser.setRole(role);
        newUser.setUsername(simpegAuth.getUsername());
        newUser.setTokenSimpeg(simpegAuth.getToken());
        newUser.setEmployee(employee);
        return userRepository.save(newUser);
    }

    private Employee updateDataUmumEmployee(SimpegAuth simpegAuth) {
        Employee employee = new Employee();
        SimpegResponse<SimpegEmployee> simpegResponse = dataUmum(simpegAuth.getToken(), simpegAuth.getNip());
        SimpegConverter.merge(simpegResponse.getData(), employee);
        employee.setNip(simpegAuth.getNip());
        return employee;
    }
}
