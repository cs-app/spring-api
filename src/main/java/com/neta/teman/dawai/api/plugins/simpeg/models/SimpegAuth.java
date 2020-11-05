package com.neta.teman.dawai.api.plugins.simpeg.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.Date;

/**
 * "id": 10453,
 * "username": "kincat4",
 * "NIP": null,
 * "name": "kincat 4",
 * "email": null,
 * "role": "ADMIN",
 * "phone": "11111",
 * "photo": "users/default.png",
 * "deleted": 0,
 * "remember_token": "",
 * "created_at": "2020-07-25 11:25:41",
 * "updated_at": "2020-10-25 22:38:14",
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SimpegAuth {

    Long id;

    String username;

    @JsonProperty("NIP")
    String nip;

    String name;

    String email;

    String role;

    String phone;

    String photo;

    String rememberToken;

    String token;

    @JsonProperty("lastLogin")
    LastLogin lastLogin;

    Personal personal;

    Employee employee;

    Integer deleted;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    Date updatedAt;

    /**
     * "date": "2020-10-25 22:38:14.894520",
     * "timezone_type": 3,
     * "timezone": "UTC"
     */
    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class LastLogin {

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        Date date;
        Integer timezoneType;
        String timezone;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Personal {
        String nama;
        String photo;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Employee {
        JabatanEselon jabatanEselon;
    }

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class JabatanEselon {
        String eselon;
        String jabatan;
    }

}
