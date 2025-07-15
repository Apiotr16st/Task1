package com.example.demo.exception;

public class EmptyInputException extends AppException {
    public EmptyInputException(ErrorCode errorCode) {
        super(errorCode);
    }
}
