package com.vectorio.endpoint;

import com.vectorio.model.request.document.Metadata;
import com.vectorio.service.DocumentService;
import org.springframework.ai.document.Document;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/docs")
public class DocumentsController {
    private final DocumentService documentService;

    public DocumentsController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Document>> addDocuments(@RequestPart Metadata metadata, @RequestPart MultipartFile multipartFile) throws IOException {
        var result = documentService.createDocumentList(metadata,multipartFile.getInputStream());
        return ResponseEntity.ok(result);
    }
}
