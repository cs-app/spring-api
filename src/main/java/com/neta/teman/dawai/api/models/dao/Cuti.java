package com.neta.teman.dawai.api.models.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.base.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "td_cuti")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cuti extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Temporal(TemporalType.DATE)
    private Date finishDate;
    private Integer jenisCuti;
    private Integer totalDays;
    private String description;
    private String tlpAddress;
    private String cutiAddress;
    @Column(columnDefinition = "integer default 1")
    private Integer cutiStatus;
    @Temporal(TemporalType.TIMESTAMP)
    private Date approveAtasanDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvePejabatDate;
    private String descriptionAtasan;
    private String descriptionPejabat;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne()
    private User atasan;

    @ManyToOne()
    private User pejabat;

}
