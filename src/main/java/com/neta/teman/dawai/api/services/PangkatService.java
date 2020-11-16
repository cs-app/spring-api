package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.PangkatGolongan;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import com.neta.teman.dawai.api.models.payload.request.PangkatGolonganRequest;
import org.springframework.data.domain.Page;

public interface PangkatService {

    ServiceResolver<Page<PangkatGolongan>> loadPage(FilterRequest request);

    ServiceResolver merge(PangkatGolonganRequest request);
}
