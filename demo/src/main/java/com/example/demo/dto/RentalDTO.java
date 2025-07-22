package com.example.demo.dto;

import java.util.Date;

public record RentalDTO(Integer rentalId,
                        Date rentalDate,
                        Integer inventoryId,
                        Integer customerId,
                        Date returnDate,
                        Integer staffId,
                        Date lastUpdate) {
}
