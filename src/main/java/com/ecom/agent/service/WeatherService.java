package com.ecom.agent.service;

import com.ecom.agent.dto.weather.WeatherResponse;

public interface WeatherService {

    /**
     * Gets the current weather for a given location.
     *
     * @param latitude  The latitude of the location.
     * @param longitude The longitude of the location.
     * @return A WeatherResponse object containing the current weather data.
     */
    WeatherResponse getCurrentWeather(double latitude, double longitude);
}