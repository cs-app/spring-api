package com.neta.teman.dawai.api.models.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.neta.teman.dawai.api.models.dao.CutiSummary;
import com.neta.teman.dawai.api.models.dao.User;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserCutiSummary {

    User user;

    CutiSummary cutiSummary;

    List<HisCount> hisCount;

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class HisCount {
        int month;
        int count;
    }
}
