package com.vectorio.model.request.document;

import java.io.Serializable;

public record Metadata(
        String type,
        String name
)implements Serializable{}
