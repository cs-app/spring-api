package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface DocumentService {

    void initDocument();

    Document validType(Long type);

    ServiceResolver<List<Document>> loadAll();

    ServiceResolver<Page<Document>> loadPage(FilterRequest request);

    ServiceResolver<Document> merge(Document copy);

    ServiceResolver remove(Document copy);

}
