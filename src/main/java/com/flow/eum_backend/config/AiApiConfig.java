package com.flow.eum_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.WatchEvent;

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
    public WebClient aiWebClient(
        @Value("${ai.api-base-url") String aiBaseUrl
    ) {
        return WebClient.builder()
                .baseUrl(aiBaseUrl)
                .build();
    }

    @Bean
    public String aiApiBaseUrl() {
        return aiApiBaseUrl;
    }
}
