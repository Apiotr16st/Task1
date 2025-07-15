package com.example.demo.exception;

public class DisposableEmailException extends AppException {
    public DisposableEmailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
