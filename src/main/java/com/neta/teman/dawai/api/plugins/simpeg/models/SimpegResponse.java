package com.neta.teman.dawai.api.plugins.simpeg.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpegResponse<T> {

    boolean status;

    String message;

    String error;

    T data;

}
