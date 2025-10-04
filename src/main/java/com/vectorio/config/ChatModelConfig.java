
package com.vectorio.config;

import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ChatModelConfig {
    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel (){
        var ollamaApi = OllamaApi.builder().build();
        return OllamaEmbeddingModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(OllamaOptions.builder()
                        .model(OllamaModel.NOMIC_EMBED_TEXT.id())
                        .build()
                )
                .build();

    }
}
