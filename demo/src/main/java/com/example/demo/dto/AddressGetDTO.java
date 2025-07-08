package com.example.demo.dto;

public record AddressGetDTO(
        Integer addressId,
        String address,
        String address2,
        String district,
        CityGetDTO city,
        String postalCode,
        String phone) {
}
