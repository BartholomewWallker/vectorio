package com.vectorio.service;

import com.vectorio.model.Metadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class DocumentServiceTest {
    private final DocumentService documentService = new DocumentService(10,10);
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
