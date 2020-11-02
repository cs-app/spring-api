package com.neta.teman.dawai.api.models.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.applications.commons.BeanCopy;
import com.neta.teman.dawai.api.models.dao.Cuti;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CutiResponse {

    private Long id;
    private Date startDate;
    private Date finishDate;
    private Integer jenisCuti;
    private Integer totalDays;
    private String description;
    private String tlpAddress;
    private String cutiAddress;
    private Integer cutiStatus;
    private Date approveAtasanDate;
    private Date approvePejabatDate;
    private CutiUserResponse user;
    private CutiUserResponse atasan;
    private CutiUserResponse pejabat;

    public CutiResponse(Cuti cuti) {
        BeanCopy.copy(this, cuti);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class CutiUserResponse {

        private String nip;
        private String name;

    }
}
