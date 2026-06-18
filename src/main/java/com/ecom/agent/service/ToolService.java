package com.ecom.agent.service;

import com.ecom.agent.dto.tool.SearchRequest;
import com.ecom.agent.dto.tool.WeatherRequest;
import com.ecom.agent.dto.weather.WeatherResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Slf4j
@Configuration
public class ToolService {

    private final WeatherService weatherService;
    private final SearchService searchService;

    public ToolService(WeatherService weatherService, SearchService searchService) {
        this.weatherService = weatherService;
        this.searchService = searchService;
    }

    @Bean
    @Description("Get the current weather for a specific location")
    public Function<WeatherRequest, WeatherResponse> getCurrentWeather() {
        return weatherRequest -> {
            log.info("Tool 'getCurrentWeather' called with request: {}", weatherRequest);
            return weatherService.getCurrentWeather(weatherRequest.latitude(), weatherRequest.longitude());
        };
    }

    @Bean
    @Description("Perform an internet search for a given query")
    public Function<SearchRequest, String> internetSearch() {
        return searchRequest -> {
            log.info("Tool 'internetSearch' called with query: {}", searchRequest.query());
            return searchService.search(searchRequest.query());
        };
    }
}