package com.neta.teman.dawai.api.applications.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

    @JsonIgnore
    protected String status = "A";

    @JsonIgnore
//    @Column(updatable = false)
    protected Long createdBy;

    @JsonIgnore
//    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdDate;

    @JsonIgnore
    protected Long modifiedBy;

    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifiedDate;

}
