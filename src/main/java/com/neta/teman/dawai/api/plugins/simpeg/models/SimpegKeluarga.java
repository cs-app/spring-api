package com.neta.teman.dawai.api.plugins.simpeg.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SimpegKeluarga {

    String hubungan;
    String nama;
    String pekerjaan;
}
