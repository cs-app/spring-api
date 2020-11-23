package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.dao.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {

    Document findByName(String name);

}
