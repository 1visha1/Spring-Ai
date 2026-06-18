package com.ecom.agent.service;

public interface SearchService {

    /**
     * Performs an internet search for the given query.
     *
     * @param query The search query.
     * @return A formatted string of search results, or an empty string if no results are found.
     */
    String search(String query);
}