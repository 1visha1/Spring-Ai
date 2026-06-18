package com.ecom.agent.controller;

import com.ecom.agent.dto.ApiResponse;
import com.ecom.agent.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")

    public ResponseEntity<ApiResponse<String>> ask(@RequestParam String message) {
        // Hardcoded userId for single-user context.
        String userId = "default_user";
        log.info("Received request for userId: '{}' with message: '{}'", userId, message);

        if (message == null || message.trim().isEmpty()) {
            log.warn("Validation failed for userId: '{}'. Message is empty.", userId);
            throw new IllegalArgumentException("Message cannot be empty");
        }

        String aiResponse = chatService.getAiResponse(userId, message);

        return ResponseEntity.ok(ApiResponse.success(aiResponse, "Chat response generated successfully"));
    }
}