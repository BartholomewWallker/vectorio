package com.vectorio.endpoint;

import com.vectorio.vector.store.DocumentRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class SimpleController {
    //private final OllamaChatModel chatModel;
    private final ChatClient chatClient;
    private final DocumentRepository documentRepository;
    private final VectorStore vectorStore;
    public SimpleController(
            //OllamaChatModel chatModel,
            ChatClient chatClient, DocumentRepository documentRepository, VectorStore vectorStore) {

        //this.chatModel = chatModel;
        this.chatClient = chatClient;
        this.documentRepository = documentRepository;
        this.vectorStore = vectorStore;
    }
    @GetMapping
    public ResponseEntity<String> message(@RequestHeader("userId") String userId, @RequestParam(value = "message") String message){
        documentRepository.addTestDocuments();
        return ResponseEntity.ok(
                chatClient
                        .prompt()
                        .user(message)
                        .advisors(
                                //(advisorSpec) -> advisorSpec.param(CONVERSATION_ID, userId),
                                RetrievalAugmentationAdvisor.builder().documentRetriever(VectorStoreDocumentRetriever.builder()
                                        .similarityThreshold(0.50)
                                        .vectorStore(vectorStore)
                                        .build())
                                        .build()
                        )
                        .call()
                        .content());
    }
}