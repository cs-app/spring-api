package com.neta.teman.dawai.api.plugins.simpeg.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.neta.teman.dawai.api.applications.base.BaseService;
import com.neta.teman.dawai.api.plugins.simpeg.exceptions.SimpegException;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegAuth;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegAuthRequest;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegEmployee;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
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
    public SimpegResponse<SimpegEmployee> dataUmum(String token, String nip) {
        String url = "https://simpeg.masmana.id/pegawai/general-data/" + nip;
        ParameterizedTypeReference<SimpegResponse<SimpegEmployee>> bean = new ParameterizedTypeReference<SimpegResponse<SimpegEmployee>>() {
        };
        Mono<SimpegResponse<SimpegEmployee>> response = webClient.get()
                .uri(url)
                .header("Authorization", "Bearer " + token)
//                .exchange()
//                .flatMap(clientResponse -> {
//                    if (clientResponse.statusCode().is2xxSuccessful()) {
//                        return clientResponse.bodyToMono(bean);
//                    }
//                    if (clientResponse.statusCode().is4xxClientError()) {
//                        return Mono.error(new SimpegException("AUTH ERROR " + clientResponse.statusCode().value()));
//                    }
//                    return Mono.error(new SimpegException("INTERNAL ERROR " + clientResponse.statusCode().value()));
//                });
//                .body(Mono.just(new SimpegAuthRequest(username, password)), SimpegAuthRequest.class)
                .retrieve()
                .bodyToMono(bean);
        return response.block();
    }
}
