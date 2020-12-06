package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.constants.AppConstants;
import com.neta.teman.dawai.api.models.converter.SimpegConverter;
import com.neta.teman.dawai.api.models.dao.*;
import com.neta.teman.dawai.api.models.payload.request.*;
import com.neta.teman.dawai.api.models.repository.*;
import com.neta.teman.dawai.api.models.spech.UserSpecs;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegAuth;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegEmployeeRiwayat;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl extends RoleServiceImpl implements UserService {

    @Autowired
    PangkatGolonganRepository pangkatGolonganRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    EmployeeDocumentRepository employeeDocumentRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    EmployeeEducationRepository employeeEducationRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RoleService roleService;

    @Autowired
    PendidikanRepository pendidikanRepository;

    @Autowired
    CutiService cutiService;

    @Autowired
    FileService fileService;

    @Autowired
    SimpegConverter simpegConverter;

    @Autowired
    UserSpecs userSpecs;

    @Autowired
    EmployeeSKPRepository skpRepository;

    @Autowired
    EmployeeCreditScoreRepository creditScoreRepository;

    @Autowired
    EmployeeSatyaLencanaRepository satyaLencanaRepository;

    @Autowired
    EmployeeHukumanDisiplinRepository hukumanDisiplinRepository;

    @Autowired
    EmployeePelatihanRepository pelatihanRepository;

    @Override
    public void updateAllUserData() {
        List<User> users = userRepository.findAll();
        List<Employee> employees = new ArrayList<>();
        for (User user : users) {
            Employee employee = user.getEmployee();
            if (Objects.isNull(employee)) continue;
            employee.setLastEducation(userCommons.lastEducation(user));
            employee.setAgeYear(userCommons.getAgeYear(employee.getTanggalLahir()));
            employee.setAgeMonth(userCommons.getAgeMonth(employee.getTanggalLahir()));
            employee.setAgeWorkYear(userCommons.getAgeYear(employee.getTglMulaiCPNS()));
            employee.setAgeWorkMonth(userCommons.getAgeMonth(employee.getTglMulaiCPNS()));
            employees.add(employee);
            if (employees.size() == 30) {
                employeeRepository.saveAll(employees);
                employees.clear();
            }
        }
        if (employees.size() > 0) employeeRepository.saveAll(employees);

        // update data pendidikan
        int startPage = 0;
        Map<String, String> typePendidikan = new HashMap<>();
        while (true) {
            Page<EmployeeEducation> educationPage = employeeEducationRepository.findAll(PageRequest.of(startPage, AppConstants.Page.SHOW_IN_PAGE));
            if (educationPage.getContent().isEmpty()) break;
            for (EmployeeEducation e : educationPage.getContent()) {
                if (Objects.isNull(e.getType())) continue;
                typePendidikan.put(e.getType(), e.getType());

            }
            startPage++;
        }
        // update data pendidikan
        List<Pendidikan> pendidikans = new ArrayList<>();
        for (Map.Entry<String, String> e : typePendidikan.entrySet()) {
            log.info("type pendidikan {}", e.getKey());
            List<Pendidikan> pendidikanTmp = pendidikanRepository.findByType(e.getKey().trim());
            if (pendidikanTmp.isEmpty()) {
                Pendidikan pendidikan = new Pendidikan();
                pendidikan.setType(e.getKey().trim());
                pendidikan.setTingkat(userCommons.tingkatPendidikan(e.getKey().trim()));
                pendidikans.add(pendidikan);
            } else {
                AtomicInteger integer = new AtomicInteger(0);
                for (Pendidikan p : pendidikanTmp) {
                    if (integer.getAndIncrement() == 0) continue;
                    pendidikanRepository.delete(p);
                }
            }
        }
        if (!pendidikans.isEmpty()) {
            pendidikanRepository.saveAll(pendidikans);
        }
        log.info("finish update user data last information");
    }

    @Override
    public void initializeNaikPangkatAndPensiun() {
        List<User> users = userRepository.findAll();
        PangkatGolongan pangkatGolongan = pangkatGolonganRepository.findById(18L).orElse(null);
        if (Objects.isNull(pangkatGolongan)) return;
        for (User user : users) {
            EmployeePangkatHis pensiun = userCommons.pensiun(user, pangkatGolongan);
            if (Objects.nonNull(pensiun)) user.getEmployee().getPangkats().add(pensiun);
            EmployeePangkatHis naikPangkat = userCommons.naikPangkat(user);
            if (Objects.nonNull(naikPangkat)) user.getEmployee().getPangkats().add(naikPangkat);
            if(Objects.isNull(pensiun) && Objects.isNull(naikPangkat)) {
                continue;
            }
            userRepository.save(user);
        }
    }

    @Override
    public ServiceResolver<User> findByNip(String nip) {
        User userExist = userRepository.findOne(userSpecs.nip(nip)).orElse(null);
        if (Objects.isNull(userExist)) return error(404, "User not found");
        return success(userExist);
    }

    /**
     * create new role from server simpeg
     */
    @Transactional
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
            Role role = roleRepository.findByName("STAFF");
            if (Objects.isNull(role)) {
                initializeRole();
                role = roleRepository.findByName("STAFF");
            }
            User user = new User();
            user.setUsername(username);
            user.setTokenSimpeg(data.getToken());
            Employee employee = employeeRepository.findById(username).orElse(new Employee());
            user.setEmployee(employee);
            buildEmployee(user.getEmployee(), data, username);
            user.setRole(role);
            userRepository.save(signature(user));
            cutiService.initCutiPegawai();
            updateUserInfo(user);
            return success(user);
        } else {
            // update
            userExist.setTokenSimpeg(data.getToken());
            buildEmployee(userExist.getEmployee(), data, username);
            User updateUser = userRepository.save(signature(userExist));
            updateUserInfo(updateUser);
            return success(updateUser);
        }
    }

    @Override
    public ServiceResolver<List<User>> findAll() {
        return success(userRepository.findAll());
    }

    /**
     * update last info user
     */
    private void updateUserInfo(User user) {
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) return;
        employee.setLastEducation(userCommons.lastEducation(user));
        employeeRepository.save(employee);
    }

    @Override
    public ServiceResolver<List<EmployeeDocument>> findByDocument(String nip) {
        User userExist = userRepository.findOne(userSpecs.nip(nip)).orElse(null);
        if (Objects.isNull(userExist)) return error(404, "User not found");
        if (Objects.isNull(userExist.getEmployee())) return error(404, "User not found");
        return success(userExist.getEmployee().getDocuments());
    }

    @Override
    public ServiceResolver<List<EmployeeDocument>> documentUpload(String nip, Long type, String fileName, Document document) {
        User userExist = userRepository.findOne(userSpecs.nip(nip)).orElse(null);
        if (Objects.isNull(userExist)) return error(404, "User not found");
        if (Objects.isNull(userExist.getEmployee())) return error(404, "User not found");
        Employee employee = userExist.getEmployee();
        if (Objects.isNull(employee.getDocuments())) employee.setDocuments(new ArrayList<>());
        EmployeeDocument employeeDocument = new EmployeeDocument();
        employeeDocument.setDocument(document);
        employeeDocument.setPath(fileName);
        employeeDocument.setApproval(AppConstants.Uploads.pending);
        employee.getDocuments().add(employeeDocument);
        userRepository.save(userExist);
        return success(employee.getDocuments());
    }

    @Override
    public ServiceResolver<List<EmployeeDocument>> documentRemove(String nip, Long documentId) {
        User userExist = userRepository.findOne(userSpecs.nip(nip)).orElse(null);
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
        if ("pensiun".equalsIgnoreCase(request.getModule())) {
            return success(userRepository.findAll(userSpecs.pagePensiun(request.getFilter()), request.pageRequest()));
        }
        if ("naikpangkat".equalsIgnoreCase(request.getModule())) {
            return success(userRepository.findAll(userSpecs.pageNaikPangkat(request.getFilter(), request.getYear(), request.getMonth()), request.pageRequest()));
        }
        return success(userRepository.findAll(userSpecs.page(request.getFilter()), request.pageRequest()));
    }

    @Override
    public ServiceResolver<Page<User>> loadPageJabatan(FilterJabatanRequest request) {
        return success(userRepository.findAll(userSpecs.pageJenisJabatan(request.getFilter(), request.getId(), request.getJenisJabatan()), request.pageRequest()));
    }

    @Override
    public ServiceResolver<List<User>> findAllByGolongan(FilterRequest request) {
        return success(userRepository.findAll(userSpecs.golongan(request.getFilter())));
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
            User dbUser = userRepository.findOne(userSpecs.nip(o.getNip())).orElse(null);
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
                his.setTmt(pangkat.getTmt());
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
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) return error(404, "user not found");
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) return error(404, "user invalid");
        List<EmployeePangkatHis> pangkatHis = employee.getPangkats().stream().filter(o -> !Objects.equals(o.getPangkatGolongan().getId(), pangkatGolongan.getId())).collect(Collectors.toList());
        employee.setPangkats(pangkatHis);
        userRepository.save(user);
        return success();
    }

    @Override
    public ServiceResolver<User> updateRole(String nip, String roleValue) {
        User user = userRepository.findOne(userSpecs.nip(nip)).orElse(null);
        if (Objects.isNull(user)) return error(404, "user not found");
        Role role = roleRepository.findByName(roleValue);
        if (Objects.isNull(role)) return error(404, "role not found");
        user.setRole(role);
        userRepository.save(user);
        return success(user);
    }

    @Override
    public ServiceResolver<User> updateProfile(UserProfileRequest request) {
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) return error(404, "user not found");
        Employee employee = user.getEmployee();
        employee.setNik(request.getNik());
        employee.setKk(request.getKk());
        employee.setNoRekening(request.getNoRekening());
        employee.setNamaBank(request.getNamaBank());
        employee.setNamaRekening(request.getNamaRekening());
        userRepository.save(user);
        return success(user);
    }

    @Override
    public ServiceResolver<User> updateProfileSKP(UserProfileUpdateRequest request) {
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) return error(404, "user not found");
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) return error(404, "employee not found");
        EmployeeSKP skp = new EmployeeSKP();
        skp.setTahun(request.getTahun());
        skp.setNilaiRata(request.getKeterangan());
        skp.setPath(request.getName());
        employee.getSkps().add(skp);
        userRepository.save(user);
        return success(user);
    }

    @Override
    public ServiceResolver removeSKP(Long id) {
        skpRepository.deleteById(id);
        return success();
    }

    @Override
    public ServiceResolver updateProfileCredit(UserProfileUpdateRequest request) {
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) return error(404, "user not found");
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) return error(404, "employee not found");
        EmployeeCreditScore creditScore = new EmployeeCreditScore();
        creditScore.setTahun(request.getTahun());
        creditScore.setNilai(request.getNilai());
        creditScore.setPath(request.getName());
        employee.getCreditScores().add(creditScore);
        userRepository.save(user);
        return success(user);
    }

    @Override
    public ServiceResolver removeCredit(Long id) {
        creditScoreRepository.deleteById(id);
        return success();
    }

    @Override
    public ServiceResolver updateProfileLencana(UserProfileUpdateRequest request) {
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) return error(404, "user not found");
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) return error(404, "employee not found");
        EmployeeSatyaLencana lencana = new EmployeeSatyaLencana();
        lencana.setTahun(request.getTahun());
        lencana.setSatyaLencana(request.getLencana());
        lencana.setPath(request.getName());
        employee.getSatyaLencanas().add(lencana);
        userRepository.save(user);
        return success(user);
    }

    @Override
    public ServiceResolver removeLencana(Long id) {
        satyaLencanaRepository.deleteById(id);
        return success();
    }

    @Override
    public ServiceResolver updateProfileDisiplin(UserProfileUpdateRequest request) {
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) return error(404, "user not found");
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) return error(404, "employee not found");
        EmployeeHukumanDisiplin lencana = new EmployeeHukumanDisiplin();
        lencana.setTahun(request.getTahun());
        lencana.setHukuman(request.getHukuman());
        lencana.setPath(request.getName());
        employee.getHukumanDisiplins().add(lencana);
        userRepository.save(user);
        return success(user);
    }

    @Override
    public ServiceResolver removeDisiplin(Long id) {
        hukumanDisiplinRepository.deleteById(id);
        return success();
    }

    @Override
    public ServiceResolver updateProfilePelatihan(UserProfileUpdateRequest request) {
        User user = userRepository.findOne(userSpecs.nip(request.getNip())).orElse(null);
        if (Objects.isNull(user)) return error(404, "user not found");
        Employee employee = user.getEmployee();
        if (Objects.isNull(employee)) return error(404, "employee not found");
        EmployeePelatihan lencana = new EmployeePelatihan();
        lencana.setTahun(request.getTahun());
        lencana.setDiklat(request.getDiklat());
        lencana.setTmt(request.getTmt());
        lencana.setPath(request.getName());
        lencana.setType(request.getDiklatType());
        employee.getPelatihans().add(lencana);
        userRepository.save(user);
        return success(user);
    }

    @Override
    public ServiceResolver removePelatihan(Long id) {
        pelatihanRepository.deleteById(id);
        return success();
    }

    private void buildEmployee(Employee employee, SimpegAuth simpegAuth, String username) {
        simpegConverter.merge(employee, simpegAuth);
        SimpegResponse<SimpegEmployeeRiwayat> dataRiwayat = dataRiwayat(simpegAuth.getToken(), username);
        simpegConverter.mergeRiwayat(employee, dataRiwayat.getData());
    }

}