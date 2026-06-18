package com.ecom.agent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${weather.api.base-url}")
    private String weatherApiBaseUrl;

    /**
     * Creates a WebClient bean specifically for the Weather API.
     */
    @Bean
    @Qualifier("weatherWebClient")
    public WebClient weatherWebClient() {
        log.info("Creating WebClient for Weather API.");
        return WebClient.builder()
                .baseUrl(weatherApiBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}