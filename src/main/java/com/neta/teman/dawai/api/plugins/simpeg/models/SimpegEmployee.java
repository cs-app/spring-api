package com.neta.teman.dawai.api.plugins.simpeg.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.models.converter.JsonDateConverter;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SimpegEmployee {

    String nama;

    String tempatLahir;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM yyyy")
    Date tanggalLahir;

    String alamat;

    @JsonProperty("no_KTP")
    String noKTP;

    @JsonProperty("no_KK")
    String noKK;

    String kelamin;

    String statusDiri;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMMM yyyy")
    @JsonDeserialize(converter = JsonDateConverter.class)
    Date tanggalPernikahan;

    String hpNumber;

    String noTelepon;

    String emailAddress;

    List<SimpegKeluarga> keluarga;

    List<SimpegPendidikan> pendidikan;
}
