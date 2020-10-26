package com.neta.teman.dawai.api.plugins.simpeg.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpegAuthRequest {

    String username;

    String password;
}
