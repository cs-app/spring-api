package com.neta.teman.dawai.api.models.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "hibernate_lazy_initializer", "handler"})
public class JabatanMapResponse {

    private Long id;

    private Long simpegId;

    private String name;

    private String jenisJabatan;

    private Long kelasJabatan;

    private Long ketersediaan;

    private Long kebutuhan;

    private Long total;

}
