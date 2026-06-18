package com.ecom.agent.service.impl;

import com.ecom.agent.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DuckDuckGoSearchServiceImpl implements SearchService {

    @Value("${search.duckduckgo.html-url}")
    private String searchUrl;

    @Value("${search.duckduckgo.user-agent}")
    private String userAgent;

    @Value("${search.duckduckgo.timeout-ms}")
    private int timeout;

    @Value("${search.duckduckgo.max-results}")
    private int maxResults;

    @Override
    public String search(String query) {
        log.info("Performing DuckDuckGo search for query: '{}'", query);
        try {
            Document doc = Jsoup.connect(searchUrl)
                    .data("q", query)
                    .userAgent(userAgent)
                    .timeout(timeout)
                    .get();

            // Use a more specific selector to target results within the main container
            Elements results = doc.select("#results .result");

            if (results.isEmpty()) {
                log.warn("DuckDuckGo search returned no results for query: '{}'", query);
                return "No results found for the query: " + query;
            }

            String formattedResults = results.stream()
                    .limit(maxResults)
                    .map(this::formatResult)
                    .collect(Collectors.joining("\n\n"));

            log.info("Successfully retrieved and formatted {} search results.", Math.min(results.size(), maxResults));
            return formattedResults;

        } catch (IOException e) {
            log.error("Error occurred during DuckDuckGo search for query: '{}'", query, e);
            return "Error: Could not perform internet search due to a network issue.";
        }
    }

    /**
     * Formats a single search result element into a readable string.
     * Selects the title from the .result__title element and the snippet from the .result__snippet element.
     */
    private String formatResult(Element result) {
        Element titleElement = result.selectFirst("h2.result__title > a.result__a");
        Element snippetElement = result.selectFirst("a.result__snippet");

        String title = (titleElement != null) ? titleElement.text() : "No title found";
        String snippet = (snippetElement != null) ? snippetElement.text() : "No snippet found";

        return String.format("Title: %s\nSnippet: %s", title, snippet);
    }
}