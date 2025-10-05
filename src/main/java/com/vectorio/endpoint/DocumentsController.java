package com.vectorio.endpoint;

import com.vectorio.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("api/docs")
public class DocumentsController {
    private final DocumentService documentService;

    public DocumentsController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<String> addDocuments(@RequestBody MultipartFile multipartFile){
        documentService.createDocumentList(multipartFile);
        return ResponseEntity.ok("Документы созданы");
    }
}
