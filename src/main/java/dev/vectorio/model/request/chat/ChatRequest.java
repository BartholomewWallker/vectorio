package dev.vectorio.model.request.chat;


import lombok.Builder;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@Builder
public class ChatRequest {
    @NotNull
    private String prompt;
    @NotNull
    private String userId;

    public String getPrompt(){
        return prompt;
    }

    public String getUserId(){
        return userId;
    }
}