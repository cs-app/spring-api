package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.BaseService;
import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.dao.PangkatGolongan;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.request.PangkatGolonganRequest;
import com.neta.teman.dawai.api.models.repository.DocumentRepository;
import com.neta.teman.dawai.api.models.repository.PangkatGolonganRepository;
import com.neta.teman.dawai.api.models.spech.PangkatSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PangkatServiceImpl extends BaseService implements PangkatService {

    @Autowired
    PangkatGolonganRepository pangkatGolonganRepository;

    @Autowired
    DocumentRepository documentRepository;

    @Override
    public ServiceResolver<Page<PangkatGolongan>> loadPage(FilterRequest request) {
        return success(pangkatGolonganRepository.findAll(PangkatSpecs.name(request.getFilter()), request.pageRequest()));
    }

    @Override
    public ServiceResolver merge(PangkatGolonganRequest request) {
        PangkatGolongan pangkatGolongan = pangkatGolonganRepository.findById(request.getId()).orElse(null);
        if (Objects.isNull(pangkatGolongan)) return error(404, "pangkat golongan not found!");
        List<Document> documentList = new ArrayList<>();
        for (Document document : request.getDocumentPangkat()) {
            if (Objects.isNull(document.getId())) continue;
            Document tmp = documentRepository.findById(document.getId()).orElse(null);
            if (Objects.isNull(tmp)) continue;
            documentList.add(tmp);
        }
        pangkatGolongan.setDocumentPangkat(documentList);
        pangkatGolonganRepository.save(pangkatGolongan);
        return success();
    }
}
