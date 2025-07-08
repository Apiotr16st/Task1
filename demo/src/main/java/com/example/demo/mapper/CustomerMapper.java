package com.example.demo.mapper;

import com.example.demo.dto.CustomerCreateDTO;
import com.example.demo.dto.CustomerGetDTO;
import com.example.demo.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface CustomerMapper {
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "activebool", constant = "true")
    @Mapping(target = "createDate", expression = "java(new java.util.Date())")
    @Mapping(target = "lastUpdate", expression = "java(new java.util.Date())")
    Customer toEntity(CustomerCreateDTO dto);

    CustomerGetDTO toGetDTO(Customer customer);
}
