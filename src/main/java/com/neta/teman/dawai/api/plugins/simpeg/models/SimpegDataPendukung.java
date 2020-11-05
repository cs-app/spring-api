package com.neta.teman.dawai.api.plugins.simpeg.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 "no_karpeg": "M. 049861",
 "no_akses": "",
 "NPWP": "24.520.987.9.542.000",
 "no_taspen": "132315665",
 "no_karis_su": "017485 KK"
 * */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SimpegDataPendukung {

    String noKarpeg;

    String noAkses;

    @JsonProperty("NPWP")
    String npwp;

    String noTaspen;

    String noKarisSu;
}
