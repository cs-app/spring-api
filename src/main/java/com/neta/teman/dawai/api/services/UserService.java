package com.neta.teman.dawai.api.services;

import com.neta.teman.dawai.api.applications.base.ServiceResolver;
import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.dao.EmployeeDocument;
import com.neta.teman.dawai.api.models.dao.User;
import com.neta.teman.dawai.api.models.payload.request.FilterRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService extends RoleService {

    ServiceResolver<User> findByNip(String nip);

    ServiceResolver<User> findByUsernameAndPasswordSimpeg(String username, String password);

    ServiceResolver<List<EmployeeDocument>> findByDocument(String nip);

    ServiceResolver<List<EmployeeDocument>> documentUpload(String nip, Long type, String file, Document document);

    ServiceResolver<List<EmployeeDocument>> documentRemove(String nip, Long documentId);

    ServiceResolver<Page<User>> loadPage(FilterRequest request);

    ServiceResolver<List<User>> findAllByGolongan(FilterRequest request);
}
