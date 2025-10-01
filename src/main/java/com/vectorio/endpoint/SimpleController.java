package com.vectorio.endpoint;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;


@RestController
@RequestMapping("/api")
public class SimpleController {
    //private final OllamaChatModel chatModel;
    private final ChatClient chatClient;
    public SimpleController(
            //OllamaChatModel chatModel,
            ChatClient chatClient) {

        //this.chatModel = chatModel;
        this.chatClient = chatClient;
    }
    @GetMapping
    public ResponseEntity<String> message(@RequestHeader("userId") String userId, @RequestParam(value = "message") String message){
        return ResponseEntity.ok(
                chatClient
                        .prompt()
                        .user(message)
                        .advisors(
                                advisorSpec -> advisorSpec.param(CONVERSATION_ID, userId)
                        )
                        .call()
                        .content());
    }
}