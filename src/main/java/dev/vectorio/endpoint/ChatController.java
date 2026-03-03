package dev.vectorio.endpoint;

import dev.vectorio.model.request.chat.ChatRequest;
import dev.vectorio.service.ChatService;
import dev.vectorio.vector.store.DocumentRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/chat")
public class ChatController {
    //private final OllamaChatModel chatModel;
    private final ChatService chatService;
    private final DocumentRepository documentRepository;

    public ChatController(
            ChatService chatService, DocumentRepository documentRepository) {

        //this.chatModel = chatModel;
        this.chatService = chatService;
        this.documentRepository = documentRepository;
    }

    @PostMapping("{chatId}")
    public Mono<String> message(@PathVariable String chatId,  @RequestBody ChatRequest chatRequest) {
        //documentRepository.addTestDocuments();
        return chatService.chat(chatRequest, chatId).collectList().map(list -> String.join(" ", list));
    }
}