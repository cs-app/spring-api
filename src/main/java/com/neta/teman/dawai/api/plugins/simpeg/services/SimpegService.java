package com.neta.teman.dawai.api.plugins.simpeg.services;

import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegAuth;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegEmployee;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegResponse;

public interface SimpegService {

    SimpegResponse<SimpegAuth> auth(String username, String password);

    SimpegResponse<SimpegEmployee> dataUmum(String token, String nip);
}
