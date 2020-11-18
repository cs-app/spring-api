package com.neta.teman.dawai.api.models.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class JabatanRequest {

    private Long id;

    private String name;

    private String jenisJabatan;

    private Long kelasJabatan;

    private Long kebutuhan;
}
