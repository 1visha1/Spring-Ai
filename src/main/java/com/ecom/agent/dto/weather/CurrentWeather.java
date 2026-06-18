package com.ecom.agent.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentWeather(
        double temperature,
        double windspeed
) {
}