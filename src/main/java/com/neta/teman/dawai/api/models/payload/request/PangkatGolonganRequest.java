package com.neta.teman.dawai.api.models.payload.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.models.dao.Document;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PangkatGolonganRequest {

    Long id;

    private List<Document> documentPangkat;
}
