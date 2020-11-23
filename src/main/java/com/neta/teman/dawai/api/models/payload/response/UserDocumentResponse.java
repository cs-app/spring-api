package com.neta.teman.dawai.api.models.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.base.BaseEntity;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.models.dao.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserDocumentResponse {

    Long id;

    String nip;

    String nama;

    String document;

    String path;

    public UserDocumentResponse(Employee employee, EmployeeDocument document) {
        this.id = document.getId();
        this.nip = employee.getNip();
        this.nama = employee.getNama();
        this.document = document.getDocument().getName();
        this.path = document.getPath();
    }

}
