package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.BaseService;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.EmployeeEducation;
import com.neta.teman.dawai.api.models.payload.response.DUKFIlterResponse;
import com.neta.teman.dawai.api.models.repository.EmployeeEducationRepository;
import com.neta.teman.dawai.api.models.repository.JabatanRepository;
import com.neta.teman.dawai.api.models.repository.PangkatGolonganRepository;
import com.neta.teman.dawai.api.models.repository.PendidikanRepository;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DUKServiceImpl extends BaseService implements DUKService {

    @Autowired
    PangkatGolonganRepository pangkatGolonganRepository;

    @Autowired
    EmployeeEducationRepository employeeEducationRepository;

    @Autowired
    JabatanRepository jabatanRepository;

    @Autowired
    PendidikanRepository pendidikanRepository;

    @Override
    public ServiceResolver<DUKFIlterResponse> filterParams() {
        DUKFIlterResponse response = new DUKFIlterResponse();
        response.setEducations(pendidikanRepository.findAll());
        response.setPangkats(pangkatGolonganRepository.findAll());
        response.setJabatans(jabatanRepository.findAll());
        return success(response);
    }
}
