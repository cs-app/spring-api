package com.neta.spring.api.applications.components;

import com.fasterxml.jackson.databind.*;
import com.neta.spring.api.applications.config.FileStorageProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppBean {

    @Bean
    public FileStorageProperties fileStorageProperties() {
        return new FileStorageProperties();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

}
