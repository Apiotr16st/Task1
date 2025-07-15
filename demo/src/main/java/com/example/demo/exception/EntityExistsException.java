package com.example.demo.exception;

public class EntityExistsException extends AppException {
    public EntityExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
