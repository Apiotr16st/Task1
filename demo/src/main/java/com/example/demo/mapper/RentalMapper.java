package com.example.demo.mapper;

import com.example.demo.dto.RentalDTO;

import com.example.demo.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RentalMapper {
    @Mapping(target = "inventoryId", expression = "java(rental.getInventory() != null ? rental.getInventory().getInventoryId() : null)")
    @Mapping(target = "customerId", expression = "java(rental.getCustomer() != null ? rental.getCustomer().getCustomerId() : null)")
    RentalDTO toGetDTO(Rental rental);
}
