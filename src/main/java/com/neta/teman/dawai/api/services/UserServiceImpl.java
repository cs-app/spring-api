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
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegEmployeeRiwayat;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegResponse;
import com.neta.teman.dawai.api.plugins.simpeg.services.SimpegServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl extends RoleServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RoleService roleService;

    @Autowired
    CutiService cutiService;

    @Override
    public ServiceResolver<User> findByNip(String nip) {
        User userExist = userRepository.findOne(UserSpecs.nip(nip)).orElse(null);
        if (Objects.isNull(userExist)) return error(404, "User not found");
        return success(userExist);
    }

    /**
     * create new role from server simpeg
     */
    @Override
    public ServiceResolver<User> findByUsernameAndPasswordSimpeg(String username, String password) {
        SimpegResponse<SimpegAuth> response = auth(username, password);
        if (!response.isStatus()) {
            return error(1001, response.getMessage());
        }
        SimpegAuth data = response.getData();
        if (isNull(data) || isNull(data.getPersonal(), data.getEmployee())) {
            return error(1002, "invalid response");
        }
        User userExist = userRepository.findByUsername(username);
        if (Objects.isNull(userExist)) {
            // create
            Role role = roleRepository.findByName("USER");
            if (Objects.isNull(role)) {
                initializeRole();
                role = roleRepository.findByName("USER");
            }
            User user = new User();
            user.setUsername(username);
            user.setTokenSimpeg(data.getToken());
            user.setEmployee(new Employee());
            buildEmployee(user.getEmployee(), data, username);
            user.setRole(role);
            userRepository.save(signature(user));
            cutiService.initCutiPegawai();
            return success(user);
        } else {
            // update
            userExist.setTokenSimpeg(data.getToken());
            buildEmployee(userExist.getEmployee(), data, username);
            return success(userRepository.save(signature(userExist)));
        }
    }

    private void buildEmployee(Employee employee, SimpegAuth simpegAuth, String username) {
        SimpegConverter.merge(employee, simpegAuth);
        SimpegResponse<SimpegEmployeeRiwayat> dataRiwayat = dataRiwayat(simpegAuth.getToken(), username);
        SimpegConverter.mergeRiwayat(employee, dataRiwayat.getData());
    }

}
