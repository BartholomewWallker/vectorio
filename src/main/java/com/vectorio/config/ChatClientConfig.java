package com.vectorio.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatClientConfig {
    private final VectorStore vectorStore;
    private final Advisor loggerAdvisor = new SimpleLoggerAdvisor();

    public ChatClientConfig(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Bean
    public ChatClient openAIchatClient(ChatModel openAiChatModel, ChatMemory chatMemoryRepository) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemoryRepository).build();
        Advisor ragAdvisor = RetrievalAugmentationAdvisor.builder().documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .build();
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(List.of(loggerAdvisor, memoryAdvisor, ragAdvisor))
                .build();
    }

    @Bean
    public ChatClient ollamaChatClient(ChatModel ollamaChatClient, ChatMemory chatMemoryRepository) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemoryRepository).build();
        Advisor ragAdvisor = RetrievalAugmentationAdvisor.builder().documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .build();
        return ChatClient.builder(ollamaChatClient)
                .defaultAdvisors(List.of(loggerAdvisor, memoryAdvisor, ragAdvisor))
                .build();
    }


}