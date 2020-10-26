package com.neta.teman.dawai.api.applications.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

    @JsonIgnore
    protected String status = "A";

    @JsonIgnore
    @Column(updatable = false)
    protected Long createdBy;

    @JsonIgnore
    @Column(updatable = false)
    protected Date createdDate;

    @JsonIgnore
    protected Long modifiedBy;

    @JsonIgnore
    protected Date modifiedDate;

}
