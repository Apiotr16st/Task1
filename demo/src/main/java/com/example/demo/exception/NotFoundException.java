package com.example.demo.exception;

public class NotFoundException extends AppException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
