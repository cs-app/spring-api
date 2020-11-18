package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.BaseService;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.models.dao.EmployeeJabatan;
import com.neta.teman.dawai.api.models.dao.Jabatan;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.response.JabatanMapResponse;
import com.neta.teman.dawai.api.models.repository.EmployeeJabatanRepository;
import com.neta.teman.dawai.api.models.repository.JabatanRepository;
import com.neta.teman.dawai.api.models.repository.UserRepository;
import com.neta.teman.dawai.api.models.spech.JabatanSpecs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class JabatanServiceImpl extends BaseService implements JabatanService {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    JabatanRepository jabatanRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmployeeJabatanRepository employeeJabatanRepository;

    @Autowired


    @Override
    public ServiceResolver<List<Jabatan>> loadAll() {
        return success(jabatanRepository.findAll());
    }

    @Override
    public ServiceResolver<List<JabatanMapResponse>> loadAllMap() {
        List<JabatanMapResponse> responses = new ArrayList<>();
        // load all jabatan
        List<Jabatan> jabatans = jabatanRepository.findAll();
        for (Jabatan o : jabatans) {
            JabatanMapResponse tmp = BeanCopy.copy(o, JabatanMapResponse.class);
            if (Objects.isNull(tmp.getKebutuhan())) tmp.setKebutuhan(0L);
            Long kebutuhan = tmp.getKebutuhan();
            Long countResource = userRepository.countByEmployee_JabatanDetail_Jabatan(o);
            tmp.setKetersediaan(countResource);
            tmp.setTotal(countResource - kebutuhan);
            responses.add(tmp);
        }
        return success(responses);
    }

    @Override
    public ServiceResolver<Page<Jabatan>> loadPage(FilterRequest request) {
        Page<Jabatan> page = jabatanRepository.findAll(JabatanSpecs.name(request.getFilter()), request.pageRequest());
        return success(page);
    }

    @Override
    public ServiceResolver<Jabatan> merge(Jabatan copy) {
        Jabatan jabatan = jabatanRepository.findByName(copy.getName().toUpperCase());
        if (isNull(jabatan)) {
            return success(jabatanRepository.save(copy));
        } else {
            BeanUtils.copyProperties(copy, jabatan, "id");
            return success(jabatanRepository.save(copy));
        }
    }

    @Override
    public ServiceResolver remove(Jabatan copy) {
        Jabatan jabatan = jabatanRepository.findById(copy.getId()).orElse(null);
        if (isNull(jabatan)) return success("already deleted");
        Page<EmployeeJabatan> employeeDocuments = employeeJabatanRepository.findAllByJabatan(jabatan, PageRequest.of(0, 1));
        if (employeeDocuments.isEmpty()) {
            jabatanRepository.deleteById(copy.getId());
            return success();
        } else return error(401, "cannot delete reference data");

    }


}
