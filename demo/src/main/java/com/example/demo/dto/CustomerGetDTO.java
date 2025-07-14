package com.example.demo.dto;

import com.example.demo.model.Gender;

public record CustomerGetDTO(
        Integer customerId,
        String firstName,
        String lastName,
        String email,
        AddressGetDTO address,
        Gender gender
) {
}
