package com.neta.teman.dawai.api.plugins.simpeg.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SimpegEmployeeRiwayat {

    SimpegDataPendukung dataPendukung;

    List<SimpegMutasi> mutasi;

    List<SimpegPangkat> pangkat;

    List<SimpegPendidikan> pendidikan;

}
