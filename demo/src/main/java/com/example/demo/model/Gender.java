package com.example.demo.model;

public enum Gender {
    MALE,
    FEMALE;

    public static Gender fromString(String value) {
        return switch (value.toLowerCase()) {
            case "male" -> MALE;
            case "female" -> FEMALE;
            default -> throw new IllegalArgumentException("Unknown gender: " + value);
        };
    }
}
