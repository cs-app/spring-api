package com.neta.teman.dawai.api.models.reports;

import lombok.Data;

@Data
public class ProfileEducation {

    private Integer row;

    private String type;

    private String value;

    private Integer graduated;

    private String majors;
}
