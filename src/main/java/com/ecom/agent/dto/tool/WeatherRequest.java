package com.ecom.agent.dto.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record WeatherRequest(
        @JsonProperty(required = true, value = "latitude") @JsonPropertyDescription("The latitude of the location") double latitude,
        @JsonProperty(required = true, value = "longitude") @JsonPropertyDescription("The longitude of the location") double longitude
) {
}