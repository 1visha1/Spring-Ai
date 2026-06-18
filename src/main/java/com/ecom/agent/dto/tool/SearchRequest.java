package com.ecom.agent.dto.tool;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record SearchRequest(
        @JsonProperty(required = true, value = "query") @JsonPropertyDescription("The search query to perform on the internet") String query
) {
}