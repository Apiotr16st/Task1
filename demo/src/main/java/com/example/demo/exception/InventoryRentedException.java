package com.example.demo.exception;

public class InventoryRentedException extends AppException {
    public InventoryRentedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
