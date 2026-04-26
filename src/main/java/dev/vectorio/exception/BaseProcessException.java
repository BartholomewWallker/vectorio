package dev.vectorio.exception;

import dev.vectorio.model.ErrorCode;
/*
* Базовый эксепшн для ошибок логики обработки с кодом ошибки
**/
public class BaseProcessException extends RuntimeException {
    private final ErrorCode errorCode;
    public BaseProcessException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
