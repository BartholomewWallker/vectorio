package com.vectorio.model;

import java.io.Serializable;

public record Metadata(
        String type,
        String name
)implements Serializable{}
