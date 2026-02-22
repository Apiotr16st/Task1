package com.example.demo.exception;

public class InvalidFormatException extends AppException {
    public InvalidFormatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
