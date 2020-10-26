package com.neta.teman.dawai.api.models.dao;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.base.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Data
@Entity
@Table(name = "app_user")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class User extends BaseEntity {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@JsonAlias({"id", "userId"})
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String tokenSimpeg;

    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(cascade = {CascadeType.ALL, CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
    Employee employee;

    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
    Role role;

}
