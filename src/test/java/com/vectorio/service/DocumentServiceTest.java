package com.vectorio.service;

import com.vectorio.model.request.document.Metadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import static org.mockito.Mockito.mock;

public class DocumentServiceTest {
    private final ChatClient chatClient = mock();
    private final DocumentService documentService = new DocumentService(chatClient,10,10);
    public final

    @Test
    void getDocumentListTest_when_fileIsNotPDF_thenThrowIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, ()->documentService.createDocumentList(new Metadata("книга","книга"),new FileInputStream(Objects.requireNonNull(getClass().getResource("/some.txt")).getFile())));
    }

    @Test
    void getDocumentList_whenAllIsOk() throws IOException {
        var multipartFile = new FileInputStream(Objects.requireNonNull(getClass().getResource("/example.pdf")).getFile());
        var result = documentService.createDocumentList(new Metadata("документ","тестовый текст"),multipartFile);
        Assertions.assertFalse(result.isEmpty());
        result.forEach(e-> {
            System.out.println("++++++START_CHUNK++++++");
            System.out.println(e.getText());
            System.out.println("======END_CHUNK=======");
        });
    }
}
