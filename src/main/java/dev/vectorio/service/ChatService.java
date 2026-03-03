package dev.vectorio.service;

import dev.vectorio.model.request.chat.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Component
public class ChatService {
    private final ChatClient chatClient;

    public ChatService(
    @Qualifier("customChatClient")
    ChatClient ollamaChatClient) {
        this.chatClient = ollamaChatClient;
    }
    public Flux<String> chat(ChatRequest chatRequest, String chatId) {
        return chatClient.prompt()
                .system(systemMessage -> systemMessage.text(
                        """
                          Respond in NDJSON format.
                          Each JSON object should contains around 100 characters.
                          Sample json object format: {"part":0,"text":"Once in a small town..."}
                        """))
                .user(userMessage -> userMessage.text(chatRequest.getPrompt()))
                .advisors(
                        (advisorSpec) -> advisorSpec.param(CONVERSATION_ID, chatRequest.getUserId())
                )
                .stream()
                .content()
                .transform(this::toJsonChunk);
    }

    private Flux<String> toJsonChunk(Flux<String> tokenFlux) {
        return Flux.create(sink -> {
            StringBuilder buffer = new StringBuilder();
            tokenFlux.subscribe(
                    token -> {
                        buffer.append(token);
                        int idx;
                        if ((idx = buffer.indexOf("\n")) >= 0) {
                            String line = buffer.substring(0, idx);
                            sink.next(line);
                            buffer.delete(0, idx + 1);
                        }
                    },
                    sink::error,
                    () -> {
                        if (buffer.length() > 0) {
                            sink.next(buffer.toString());
                        }
                        sink.complete();
                    }
            );
        });
    }
}
