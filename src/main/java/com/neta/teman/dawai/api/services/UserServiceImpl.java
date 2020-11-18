package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.converter.SimpegConverter;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.request.UserPangkatRequest;
import com.neta.teman.dawai.api.models.repository.PangkatGolonganRepository;
import com.neta.teman.dawai.api.models.repository.RoleRepository;
import com.neta.teman.dawai.api.models.repository.UserRepository;
import com.neta.teman.dawai.api.models.spech.UserSpecs;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegAuth;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegEmployeeRiwayat;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl extends RoleServiceImpl implements UserService {

    @Autowired
    PangkatGolonganRepository pangkatGolonganRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RoleService roleService;

    @Autowired
    CutiService cutiService;

    @Autowired
    FileService fileService;

    @Autowired
    SimpegConverter simpegConverter;

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

    @Override
    public ServiceResolver<List<EmployeeDocument>> findByDocument(String nip) {
        User userExist = userRepository.findOne(UserSpecs.nip(nip)).orElse(null);
        if (Objects.isNull(userExist)) return error(404, "User not found");
        if (Objects.isNull(userExist.getEmployee())) return error(404, "User not found");
        return success(userExist.getEmployee().getDocuments());
    }

    @Override
    public ServiceResolver<List<EmployeeDocument>> documentUpload(String nip, Long type, String fileName, Document document) {
        User userExist = userRepository.findOne(UserSpecs.nip(nip)).orElse(null);
        if (Objects.isNull(userExist)) return error(404, "User not found");
        if (Objects.isNull(userExist.getEmployee())) return error(404, "User not found");
        Employee employee = userExist.getEmployee();
        if (Objects.isNull(employee.getDocuments())) employee.setDocuments(new ArrayList<>());
        EmployeeDocument employeeDocument = new EmployeeDocument();
        employeeDocument.setDocument(document);
        employeeDocument.setPath(fileName);
        employee.getDocuments().add(employeeDocument);
        userRepository.save(userExist);
        return success(employee.getDocuments());
    }

    @Override
    public ServiceResolver<List<EmployeeDocument>> documentRemove(String nip, Long documentId) {
        User userExist = userRepository.findOne(UserSpecs.nip(nip)).orElse(null);
        if (Objects.isNull(userExist)) return error(404, "User not found");
        if (Objects.isNull(userExist.getEmployee())) return error(404, "User not found");
        Employee employee = userExist.getEmployee();
        if (Objects.isNull(employee.getDocuments())) employee.setDocuments(new ArrayList<>());
        for (EmployeeDocument document : employee.getDocuments()) {
            if (0 == document.getId().compareTo(documentId)) {
                employee.getDocuments().remove(document);
                fileService.deleteFile(nip, document.getPath());
                break;
            }
        }
        userRepository.save(userExist);
        return success(employee.getDocuments());
    }

    @Override
    public ServiceResolver<Page<User>> loadPage(FilterRequest request) {
        return success(userRepository.findAll(UserSpecs.page(request.getFilter()), request.pageRequest()));
    }

    @Override
    public ServiceResolver<List<User>> findAllByGolongan(FilterRequest request) {
        return success(userRepository.findAll(UserSpecs.golongan(request.getFilter())));
    }

    @Override
    public ServiceResolver pangkatAdd(UserPangkatRequest request) {
        UserPangkatRequest.Pangkat pangkat = request.getPangkat();
        PangkatGolongan pangkatGolongan = pangkatGolonganRepository.findById(pangkat.getId()).orElse(null);
        if (Objects.isNull(pangkatGolongan)) {
            return error(401, "pangkat not exist");
        }
        List<UserPangkatRequest.User> params = request.getUsers();
        if (Objects.isNull(params) || params.size() == 0) {
            return error(401, "user not exist");
        }
        List<User> updateUser = new ArrayList<>();
        for (UserPangkatRequest.User o : params) {
            User dbUser = userRepository.findOne(UserSpecs.nip(o.getNip())).orElse(null);
            if (Objects.isNull(dbUser)) continue;
            if (Objects.isNull(dbUser.getEmployee())) continue;
            Employee employee = dbUser.getEmployee();
            if (Objects.isNull(employee.getPangkats())) {
                dbUser.getEmployee().setPangkats(new ArrayList<>());
            }
            boolean exist = false;
            for (EmployeePangkatHis his : employee.getPangkats()) {
                if (isNull(his.getPangkatGolongan())) continue;
                if (isNull(pangkatGolongan)) continue;
                if (his.getPangkatGolongan().getId().equals(pangkatGolongan.getId())) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                EmployeePangkatHis his = new EmployeePangkatHis();
                his.setSumberData(AppConstants.Source.TEMAN_DAWAI);
//                his.setTmt(pangkat.getTmt());
                his.setPangkatGolongan(pangkatGolongan);
                employee.getPangkats().add(his);
                updateUser.add(dbUser);
                userRepository.save(dbUser);
            }
        }
        log.info("");
//        userRepository.saveAll(updateUser);
        return success();
    }

    @Override
    public ServiceResolver pangkatRemove(UserPangkatRequest request) {
        UserPangkatRequest.Pangkat pangkat = request.getPangkat();
        PangkatGolongan pangkatGolongan = pangkatGolonganRepository.findById(pangkat.getId()).orElse(null);
        if (Objects.isNull(pangkatGolongan)) {
            return error(401, "pangkat not exist");
        }
        User user = userRepository.findOne(UserSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) return error(404, "user not found");
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) return error(404, "user invalid");
        List<EmployeePangkatHis> pangkatHis = employee.getPangkats().stream().filter(o -> {
            if (Objects.isNull(employee.getPangkats())) return true;
            for (EmployeePangkatHis ph : employee.getPangkats()) {
                if (ph.getPangkatGolongan().getId().equals(pangkatGolongan.getId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        employee.setPangkats(pangkatHis);
        userRepository.save(user);
        return success();
    }

    private void buildEmployee(Employee employee, SimpegAuth simpegAuth, String username) {
        simpegConverter.merge(employee, simpegAuth);
        SimpegResponse<SimpegEmployeeRiwayat> dataRiwayat = dataRiwayat(simpegAuth.getToken(), username);
        simpegConverter.mergeRiwayat(employee, dataRiwayat.getData());
    }

}
