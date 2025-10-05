package com.vectorio.service;

import com.vectorio.model.Metadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class DocumentServiceTest {
    private final DocumentService documentService = new DocumentService(10,10);
    private final MultipartFile multipartFile = Mockito.mock();

    @Test
    void getDocumentListTest_when_fileIsNotPDF_thenThrowIllegalArgumentException() throws IOException {
        Mockito.when(multipartFile.getInputStream()).thenReturn(new FileInputStream(Objects.requireNonNull(getClass().getResource("/some.txt")).getFile()));
        Assertions.assertThrows(IllegalArgumentException.class, ()->documentService.createDocumentList(new Metadata("книга","книга"),multipartFile));
    }
}
