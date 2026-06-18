package com.ecom.agent.service.impl;

import com.ecom.agent.service.ChatService;
import com.ecom.agent.service.MemoryService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final MemoryService memoryService;

    @Value("${ai.system.prompt}")
    private String systemPrompt;

    public ChatServiceImpl(ChatClient chatClient, MemoryService memoryService) {
        this.chatClient = chatClient;
        this.memoryService = memoryService;
    }

    @Override
    @CircuitBreaker(name = "groqApi", fallbackMethod = "fallbackResponse")
    public String getAiResponse(String userId, String message) {
        log.info("Processing chat for userId: '{}'", userId);

        String memoryContext = memoryService.search(message, userId);

        // Reverted to the correct .functions() convenience method
        String response = chatClient.prompt()
                .system(p -> p.text(systemPrompt).params(Map.of("memoryContext", memoryContext)))
                .user(message)
                .functions("internetSearch", "getCurrentWeather")
                .call()
                .content();

        memoryService.save(userId, "User: " + message);
        memoryService.save(userId, "AI: " + response);

        return response;
    }

    /**
     * Fallback method for the getAiResponse circuit breaker.
     * This method is called when the Groq API is unavailable or rate-limiting is active.
     *
     * @param userId    The user ID from the original request.
     * @param message   The message from the original request.
     * @param ex        The exception that caused the circuit breaker to open.
     * @return A user-friendly fallback message.
     */
    public String fallbackResponse(String userId, String message, Throwable ex) {
        log.warn("Circuit breaker opened for Groq API. Falling back. User: {}, Message: '{}', Exception: {}",
                userId, message, ex.getMessage());
        return "My brain is a bit tired right now, please try again in a moment.";
    }
}