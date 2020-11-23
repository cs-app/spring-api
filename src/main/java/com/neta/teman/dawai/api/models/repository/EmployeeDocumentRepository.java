package com.neta.teman.dawai.api.models.repository;

import com.neta.teman.dawai.api.models.dao.Document;
import com.neta.teman.dawai.api.models.dao.EmployeeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long>, JpaSpecificationExecutor<EmployeeDocument> {

    Page<EmployeeDocument> findAllByDocument(Document document, Pageable pageable);

    List<EmployeeDocument> findAllByApproval(Long approval);
}
