package com.vectorio.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatClientConfig {

    /* @Bean
     public ChatMemory chatMemory(){
         return new InMemoryChatMemory();
     }*/
    @Bean
    @Primary
    public ChatMemory chatMemoryRepository( JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .build();
    }
    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemoryRepository) {
        Advisor loggerAdvisor =  new SimpleLoggerAdvisor();
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemoryRepository).build();
        return chatClientBuilder
                .defaultAdvisors(List.of(loggerAdvisor,memoryAdvisor))
                .build();
    }

   /* @Bean
    public OllamaChatModel ollamaChatClient(){
        return OllamaChatModel.builder()
                .ollamaApi(OllamaApi.builder().build())
                .defaultOptions(
                        OllamaOptions.builder()
                                .model(OllamaModel.GEMMA3)
                                .temperature(0.1)
                                .build())
                .build();
    }*/
}