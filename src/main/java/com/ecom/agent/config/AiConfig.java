package com.ecom.agent.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Slf4j
@Configuration
public class AiConfig {

    /**
     * Creates a simple, non-persistent chat memory bean for short-term conversational context.
     */
    @Bean
    public ChatMemory chatMemory() {
        log.info("Creating InMemoryChatMemory bean.");
        return new InMemoryChatMemory();
    }

    /**
     * Creates the primary, stateful ChatClient for conversations.
     * It's configured with a MessageChatMemoryAdvisor to automatically manage chat history
     * and enabled for function calling.
     */
    @Bean
    @Primary
    public ChatClient conversationalChatClient(ChatClient.Builder builder,
                                               ChatMemory chatMemory,
                                               @Value("${spring.ai.chat.memory.window-size:10}") int windowSize) {
        log.info("Creating conversational ChatClient with a message window size of {}.", windowSize);
        return builder
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory, "default", windowSize))
                .build();
    }
}