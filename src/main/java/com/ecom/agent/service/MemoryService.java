package com.ecom.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MemoryService {

    private final VectorStore vectorStore;

    @Value("${spring.ai.vectorstore.search.top-k:5}")
    private int topK;

    public MemoryService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Saves a message to the vector store, associating it with a user ID.
     *
     * @param userId  The ID of the user.
     * @param message The message content to save.
     */
    public void save(String userId, String message) {
        log.debug("Saving message to vector store for userId: {}", userId);
        Document document = new Document(message, Map.of("userId", userId));
        vectorStore.add(List.of(document));
        log.info("Successfully saved message for userId: {}", userId);
    }

    /**
     * Searches the vector store for messages similar to the query for a specific user.
     *
     * @param query  The query to search for.
     * @param userId The ID of the user.
     * @return A string containing the formatted content of the top K similar documents.
     */
    public String search(String query, String userId) {
        log.debug("Performing similarity search for userId: '{}' with topK: {}", userId, topK);

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .filterExpression("userId == '" + userId + "'")
                .build();

        List<Document> results = vectorStore.similaritySearch(request);
        log.info("Found {} similar documents for userId: '{}'", results.size(), userId);

        return results.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n"));
    }
}