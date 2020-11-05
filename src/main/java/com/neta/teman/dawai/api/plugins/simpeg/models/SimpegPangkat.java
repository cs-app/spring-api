package com.neta.teman.dawai.api.plugins.simpeg.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.models.converter.JsonDateConverter;
import lombok.Data;

import java.util.Date;

/**
 * "tahun": 2006,
 * "tmt": "2006-03-01",
 * "pangkat": " Subbagian Umum Lembaga Penjaminan Mutu Pendidikan (Eselon 3)"
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SimpegPangkat {

    @JsonDeserialize(converter = JsonDateConverter.class)
    Date tmt;

    String gol;

    String pangkat;
}
