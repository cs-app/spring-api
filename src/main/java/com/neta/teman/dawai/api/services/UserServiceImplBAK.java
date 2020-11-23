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
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegEmployeeDataUmum;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegEmployeeRiwayat;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegResponse;
import com.neta.teman.dawai.api.plugins.simpeg.services.SimpegServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplBAK extends SimpegServiceImpl {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserSpecs userSpecs;


    //@Override
    public User findByUsernameAndPassword(String nip, String password) {
        return userRepository.findOne(userSpecs.login(nip, password)).orElse(null);
    }

    /**
     * create new role from server simpeg
     */
    //@Override
    public ServiceResolver<User> findByUsernameAndPasswordSimpeg(String username, String password) {
        SimpegResponse<SimpegAuth> simpegResponse = auth(username, password);
        if (!simpegResponse.isStatus()) {
            return error(1001, simpegResponse.getMessage());
        }
        SimpegAuth simpegAuth = simpegResponse.getData();
//        simpegAuth.setNip("197706242005012002"); // debug
//        if (isNull(simpegAuth.getRole(), simpegAuth.getUsername(), simpegAuth.getToken(), simpegAuth.getNip())) {
//            return error(1002, "Invalid user!");
//        }
        // get role, auto create when role not found in system
//        Role role = roleRepository.findByName(simpegAuth.getRole());
//        User user = createUserIfNotExist(simpegAuth, role);
//        return success(user);
        return success(null);
    }

    /*
    private User createUserIfNotExist(SimpegAuth simpegAuth, Role role) {
        User user = userRepository.findByUsername(simpegAuth.getUsername());
        if (nonNull(user)) {
            updateDataUmumEmployee(simpegAuth, user.getEmployee());
            updateDataRiwayat(simpegAuth, user.getEmployee());
            user.setTokenSimpeg(simpegAuth.getToken());
            userRepository.save(user);
            return user;
        }
        // employee
        Employee employee = new Employee();
        updateDataUmumEmployee(simpegAuth, employee);
        updateDataRiwayat(simpegAuth, employee);
        // user
        User newUser = new User();
        newUser(simpegAuth, role, newUser, employee);
        return userRepository.save(newUser);
    }

    private void newUser(SimpegAuth simpegAuth, Role role, User user, Employee employee) {
        user.setRole(role);
        user.setExternalId(simpegAuth.getId());
        user.setUsername(simpegAuth.getUsername());
        user.setTokenSimpeg(simpegAuth.getToken());
        user.setEmployee(employee);
    }

    private void updateDataUmumEmployee(SimpegAuth simpegAuth, Employee employee) {
        SimpegResponse<SimpegEmployeeDataUmum> simpegResponse = dataUmum(simpegAuth.getToken(), simpegAuth.getNip());
        SimpegConverter.mergeUmum(simpegResponse.getData(), employee);
        employee.setNip(simpegAuth.getNip());
        // picture
        if (nonNull(simpegAuth.getPersonal()) && nonNull(simpegAuth.getPersonal().getPhoto())) {
            employee.setPicture(simpegAuth.getPersonal().getPhoto());
        }
    }

    private void updateDataRiwayat(SimpegAuth simpegAuth, Employee employee) {
        SimpegResponse<SimpegEmployeeRiwayat> simpegResponse = dataRiwayat(simpegAuth.getToken(), simpegAuth.getNip());
        SimpegConverter.mergeRiwayat(simpegResponse.getData(), employee);
    }
    */
}
