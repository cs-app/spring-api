package com.neta.teman.dawai.api.plugins.simpeg.services;

import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegAuth;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegEmployeeDataUmum;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegEmployeeRiwayat;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegResponse;

public interface SimpegService {

    SimpegResponse<SimpegAuth> auth(String username, String password);

    SimpegResponse<SimpegEmployeeDataUmum> dataUmum(String token, String nip);

    SimpegResponse<SimpegEmployeeRiwayat> dataRiwayat(String token, String nip);
}
