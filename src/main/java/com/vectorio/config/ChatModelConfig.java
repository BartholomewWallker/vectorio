
package com.vectorio.config;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ChatModelConfig {
    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel() {
        var ollamaApi = OllamaApi.builder().build();
        return OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder()
                        .model(OllamaModel.NOMIC_EMBED_TEXT.id())
                        .build()
                )
                .build();

    }

    @Bean
    public OpenAiChatModel openAiChatModel() {
        var openAIApi = OpenAiApi.builder()
                .apiKey(openAiApiKey)
                .build();
        var chatOptions = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_3_5_TURBO)
                .temperature(0.1)
                .maxTokens(200)
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(openAIApi)
                .defaultOptions(chatOptions)
                .build();
    }

    @Bean
    public OllamaChatModel ollamaChatClient(){
        return OllamaChatModel.builder()
                .ollamaApi(OllamaApi.builder().build())
                .defaultOptions(
                        OllamaOptions.builder()
                                .model(OllamaModel.GEMMA3)
                                .temperature(0.1)
                                .build())
                .build();
    }
}
