package com.flow.eum_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AiApiConfig {

    @Value("${ai.api-base-url")
    private String aiApiBaseUrl;

    @Bean
    public RestTemplate aiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate;
    }

    @Bean
    public String aiApiBaseUrl() {
        return aiApiBaseUrl;
    }
}
