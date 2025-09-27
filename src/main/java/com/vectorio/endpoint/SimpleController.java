package com.vectorio.endpoint;

import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> message(@RequestParam(value = "message", defaultValue = "Меня зовут Энди") String message){
        return ResponseEntity.ok(chatClient.prompt().user(message).call().content());
    }
}