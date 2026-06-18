package com.ecom.agent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class MemoryService {

    private final Map<String, Deque<String>> memory = new HashMap<>();
    private final int maxSize = 10;

    public void save(String userId, String message) {
        memory.computeIfAbsent(userId, k -> new LinkedList<>());

        Deque<String> messages = memory.get(userId);

        if (messages.size() >= maxSize) {
            messages.removeFirst();
        }

        messages.addLast(message);

        log.debug("Saved message for userId: {}", userId);
    }

    public String search(String query, String userId) {
        // simple keyword match (NO embeddings)
        Deque<String> messages = memory.getOrDefault(userId, new LinkedList<>());

        return messages.stream()
                .filter(m -> m.toLowerCase().contains(query.toLowerCase()))
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }

    public List<String> getRecent(String userId) {
        return new ArrayList<>(memory.getOrDefault(userId, new LinkedList<>()));
    }
}