package com.neta.teman.dawai.api.plugins.simpeg.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SimpegPendidikan {

    String jenjang;

    String namaSekolah;

    Integer tahunLulus;

    @JsonProperty("Jurusan")
    String jurusan;
}
