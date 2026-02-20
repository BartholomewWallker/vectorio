package com.vectorio.service;

import com.vectorio.model.request.document.Metadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DocumentServiceTest {
    private final ChatClient chatClient = mock();
    private final ChatClient.ChatClientRequestSpec chatClientRequestSpec = mock();
    private final ChatClient.CallResponseSpec callResponseSpec = mock();
    private final DocumentService documentService = new DocumentService(chatClient, 10, 10);

    public final

    @Test
    void getDocumentListTest_when_fileIsNotPDF_thenThrowIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> documentService.createDocumentList(new Metadata("книга", "книга"), new FileInputStream(Objects.requireNonNull(getClass().getResource("/some.txt")).getFile())));
    }

    @Test
    void getDocumentList_whenAllIsOk() throws IOException {
        when(chatClient.prompt()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.messages()).thenReturn(chatClientRequestSpec);
        when(chatClientRequestSpec.call()).thenReturn(callResponseSpec);
        when(callResponseSpec.entity(new ListOutputConverter(new DefaultConversionService()))).thenReturn(List.of(""));

        var multipartFile = new FileInputStream(("C:/Users/Тимка/IdeaProjects/vectorio/src/test/resources/example.pdf"));
        var result = documentService.createDocumentList(new Metadata("документ", "тестовый текст"), multipartFile);

        Assertions.assertFalse(result.isEmpty());
        result.forEach(e -> {
            System.out.println("++++++START_CHUNK++++++");
            System.out.println(e.getText());
            System.out.println("======END_CHUNK=======");
        });
    }
}
