package dev.vectorio.model;

public enum ErrorCode {
    DOCUMENT_PARSE_ERROR("Ошибка парсинга документов");

    ErrorCode(String description) {
        this.description = description;
    }

    private final String description;

    public String description() {
        return description;
    }
}
