package com.vectorio.model.request.document;

import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

public record DocumentRequest (
        Metadata metadata,
    MultipartFile multipartFile
) implements Serializable{}
