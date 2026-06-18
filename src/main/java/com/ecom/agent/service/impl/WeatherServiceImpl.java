package com.ecom.agent.service.impl;

import com.ecom.agent.dto.weather.WeatherResponse;
import com.ecom.agent.service.WeatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {

    private final WebClient weatherWebClient;

    public WeatherServiceImpl(@Qualifier("weatherWebClient") WebClient weatherWebClient) {
        this.weatherWebClient = weatherWebClient;
    }

    @Override
    public WeatherResponse getCurrentWeather(double latitude, double longitude) {
        log.info("Fetching current weather for latitude: {} and longitude: {}", latitude, longitude);
        try {
            return weatherWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current_weather", true)
                            .build())
                    .retrieve()
                    .bodyToMono(WeatherResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching weather data", e);
            return null; // Or throw a custom exception
        }
    }
}