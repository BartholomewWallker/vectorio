package com.vectorio.endpoint;

import com.vectorio.vector.store.DocumentRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;


@RestController
@RequestMapping("/api/chat")
public class ChatController {
    //private final OllamaChatModel chatModel;
    private final ChatClient chatClient;
    private final DocumentRepository documentRepository;
    public ChatController(
            ChatClient ollamaChatClient, DocumentRepository documentRepository) {

        //this.chatModel = chatModel;
        this.chatClient = ollamaChatClient;
        this.documentRepository = documentRepository;
    }
    @GetMapping
    public ResponseEntity<String> message(@RequestHeader("userId") String userId, @RequestParam(value = "message") String message){
        //documentRepository.addTestDocuments();
        return ResponseEntity.ok(
                chatClient
                        .prompt()
                        .user(message)
                        .advisors(
                                (advisorSpec) -> advisorSpec.param(CONVERSATION_ID, userId)
                        )
                        .call()
                        .content());
    }
}