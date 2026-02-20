package com.vectorio.endpoint;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mcp/api")
public class McpController {
    private final ChatClient chatClient;
    public McpController(ChatClient openAIchatClient){
        this.chatClient = openAIchatClient;
    }
    @GetMapping
    public String chat(@RequestParam("message") String message) {
        return  chatClient.prompt().user(message).call().content();
    }

}
