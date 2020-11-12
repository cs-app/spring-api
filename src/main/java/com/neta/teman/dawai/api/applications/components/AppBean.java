package com.neta.teman.dawai.api.applications.components;

import com.neta.teman.dawai.api.applications.config.FileStorageProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppBean {

    @Bean
    public FileStorageProperties fileStorageProperties(){
        return new FileStorageProperties();
    }
}
