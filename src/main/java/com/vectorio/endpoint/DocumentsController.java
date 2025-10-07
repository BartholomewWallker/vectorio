package com.vectorio.endpoint;

import com.vectorio.model.Metadata;
import com.vectorio.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController("api/docs")
public class DocumentsController {
    private final DocumentService documentService;

    public DocumentsController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<String> addDocuments(@RequestPart Metadata metadata, @RequestPart MultipartFile multipartFile) throws IOException {
        documentService.createDocumentList(metadata,multipartFile.getInputStream());
        return ResponseEntity.ok(String.format("Документы созданы с типом %s, и именем %s.", metadata.type(), metadata.name()));
    }
}
