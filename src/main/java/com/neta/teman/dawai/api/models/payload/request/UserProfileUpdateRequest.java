package com.neta.teman.dawai.api.models.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserProfileUpdateRequest extends ProfileDokumenRequest{

    private String nip;

    private Date tahun;

    private Date tmt;

    private Long nilai;

    private String keterangan;

    private String lencana;

    private String hukuman;

    private String diklatType;

    private String diklat;

}
