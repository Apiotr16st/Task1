package com.example.demo.exception;

public enum ErrorCode {
    EMAIL_EXISTS(100, "Email %s is already taken"),
    EMAIL_IS_DISPOSABLE(101, "Email %s is disposable"),
    EMPTY_FIRST_NAME(102,"Field firstName cannot be empty"),
    EMPTY_LAST_NAME(103,"Field lastName cannot be empty"),
    EMPTY_EMAIL(104, "Field email cannot be empty"),
    EMPTY_CITY_NAME(105,"Field city cannot be empty"),
    INVALID_ACTIVE_FILTER_FORMAT(106, "Invalid format, use value: active / inactive"),
    INVALID_ACTIVE_FORMAT(107, "Invalid input, active must be 0 or 1"),
    ADDRESS_RECORD_EXISTS(108, "Record with such fields already exists"),
    INVENTORY_IS_RENTED(109, "Inventory is already rented"),

    CUSTOMER_ID_NOT_FOUND(200,"Customer with ID %d not found"),
    ADDRESS_ID_NOT_FOUND(201, "Address with ID %d not found"),
    CITY_ID_NOT_FOUND(202, "City with ID %d not found"),
    COUNTRY_ID_NOT_FOUND(203, "Country with ID %d not found"),
    ARGUMENT_NOT_VALID(204, ""),
    INVENTORY_ID_NOT_FOUND(205, "Inventory with ID %d not found"),
    RENTAL_ID_NOT_FOUND(206,"Rental with ID %d not found");

    private final int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }

    public ErrorCode format(Object arg) {
        this.message = String.format(message, arg);
        return this;
    }
}
