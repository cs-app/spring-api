package com.neta.teman.dawai.api.models.payload.request;

import lombok.Data;

@Data
public class LoginRequest {

    private String nip;

    private String username;

    private String password;

    private String deviceId;

    private String deviceVendor;

    private String deviceType;

    private String playerId;

    private String appVersion;

}
