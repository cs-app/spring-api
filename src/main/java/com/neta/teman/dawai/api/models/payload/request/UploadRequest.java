package com.neta.teman.dawai.api.models.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UploadRequest {

    private Long type;

    private String nip;

    private String file;

    private String ext;

    private String name;

    // hanya untuk delete
    private Long documentId;

}
