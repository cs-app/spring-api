package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.Document;

import java.util.List;

public interface DocumentService {

    void initDocument();

    Document validType(Long type);

    ServiceResolver<List<Document>> loadAll();
}
