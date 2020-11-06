package com.neta.teman.dawai.api.plugins.simpeg.services;

import com.neta.teman.dawai.api.applications.base.BaseService;
import com.neta.teman.dawai.api.plugins.simpeg.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SimpegServiceImpl extends BaseService implements SimpegService {

    protected final String USER_NOT_EXIST = "USER_NOT_EXIST";

    @Autowired
    WebClient webClient;

    @Override
    public SimpegResponse<SimpegAuth> auth(String username, String password) {
        String url = "https://simpeg.masmana.id/auth/login";
        url = "https://simpeg.masmana.id/auth/login";
        ParameterizedTypeReference<SimpegResponse<SimpegAuth>> bean = new ParameterizedTypeReference<SimpegResponse<SimpegAuth>>() {
        };
        Mono<SimpegResponse<SimpegAuth>> response = webClient.post()
                .uri(url)
                // .header("Authorization", "Basic " + Base64Utils.encodeToString((username + ":" + token).getBytes(UTF_8)))
                .body(Mono.just(new SimpegAuthRequest(username, password)), SimpegAuthRequest.class)
                .retrieve()
                .bodyToMono(bean);
        return response.block();
    }

    @Override
    public SimpegResponse<SimpegEmployeeDataUmum> dataUmum(String token, String nip) {
        String url = "https://simpeg.masmana.id/pegawai/general-data/" + nip;
        ParameterizedTypeReference<SimpegResponse<SimpegEmployeeDataUmum>> bean = new ParameterizedTypeReference<SimpegResponse<SimpegEmployeeDataUmum>>() {
        };
        Mono<SimpegResponse<SimpegEmployeeDataUmum>> response = webClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(bean);
        return response.block();
    }

    @Override
    public SimpegResponse<SimpegEmployeeRiwayat> dataRiwayat(String token, String nip) {
        String url = "https://simpeg.masmana.id/pegawai/history-data/" + nip;
        ParameterizedTypeReference<SimpegResponse<SimpegEmployeeRiwayat>> bean = new ParameterizedTypeReference<SimpegResponse<SimpegEmployeeRiwayat>>() {
        };
        Mono<SimpegResponse<SimpegEmployeeRiwayat>> response = webClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(bean);
        return response.block();
    }
}
