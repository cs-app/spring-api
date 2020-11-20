package com.neta.teman.dawai.api.models.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FilterDukRequest extends PageRequestRest {

    Long golongan;
    Long jabatan;
    String name;
    Long pangkat;
    Long pendidikan;
    Integer status;
    Integer usia;
    Integer masaKerja;

}
