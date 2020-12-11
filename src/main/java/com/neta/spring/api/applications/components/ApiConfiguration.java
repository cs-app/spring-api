package com.neta.spring.api.applications.components;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApiConfiguration {

    @Bean
    WebClient webClient(){
        return WebClient.create();
    }

}
