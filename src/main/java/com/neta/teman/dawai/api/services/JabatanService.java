package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.dao.Jabatan;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.response.JabatanMapResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface JabatanService {

    ServiceResolver<List<Jabatan>> loadAll();

    ServiceResolver<Page<Jabatan>> loadPage(FilterRequest request);

    ServiceResolver<Jabatan> merge(Jabatan copy);

    ServiceResolver remove(Jabatan copy);

    ServiceResolver<List<JabatanMapResponse>> loadAllMap(Integer tahun);
}
