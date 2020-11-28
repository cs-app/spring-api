package com.neta.teman.dawai.api.models.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Date;

/**
 * "start_date": "2020-09-30T17:00:00.000Z",
 * "finish_date": "2020-10-29T17:00:00.000Z",
 * "total_days": 30,
 * "jenis_cuti": 1,
 * "description": "keterangan",
 * "tlp_address": "021284190270",
 * "tlp_address": "neroktog"
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CutiRequest {

    Long id;
    String nip;
    Date startDate;
    Date finishDate;
    Integer totalDays;
    Integer jenisCuti;
    String description;
    String tlpAddress;
    String cutiAddress;
    Integer approveStatus;
    String descriptionApprove;
    String file;
    String fileName;
    String ext;
}
