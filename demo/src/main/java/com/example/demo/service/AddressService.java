package com.example.demo.service;

import com.example.demo.dto.AddressCreateDTO;
import com.example.demo.dto.AddressGetDTO;
import com.example.demo.dto.AddressUpdateDTO;
import com.example.demo.exception.EntityExistsException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.AddressMapper;
import com.example.demo.model.Address;
import com.example.demo.model.City;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.CityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {
    private final AddressRepository repository;
    private final CityRepository cityRepository;
    private final AddressMapper addressMapper;

    public AddressService(AddressRepository repository, CityRepository cityRepository, AddressMapper addressMapper) {
        this.repository = repository;
        this.cityRepository = cityRepository;
        this.addressMapper = addressMapper;
    }

    public List<AddressGetDTO> getAll() {
        List<Address> addresses = repository.findAll();
        return addresses.stream().map(addressMapper::toGetDTO).toList();
    }

    public AddressGetDTO getById(Integer id) {
        Address a = repository.findById(id).orElseThrow(()-> new NotFoundException(ErrorCode.ADDRESS_ID_NOT_FOUND.format(id)));
        return addressMapper.toGetDTO(a);
    }


    public ResponseEntity<AddressGetDTO> create(AddressCreateDTO dto) {
        City city = cityRepository.findById(dto.cityId()).orElseThrow(() ->
                new NotFoundException(ErrorCode.CITY_ID_NOT_FOUND.format(dto.cityId())));
        Optional<Address> optAddress = repository.findByAddressAndAddress2AndDistrictAndCityAndPostalCodeAndPhone(dto.address(), dto.address2(), dto.district(), city, dto.postalCode(), dto.phone());
        if (optAddress.isPresent())
            throw new EntityExistsException(ErrorCode.ADDRESS_RECORD_EXISTS);

        Address address = addressMapper.toEntity(dto);
        address.setCity(city);
        Address saved = repository.save(address);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressMapper.toGetDTO(saved));
    }

    public ResponseEntity<Address> delete(Integer id) {
        Address address = repository.findById(id).orElseThrow(()->
                new NotFoundException(ErrorCode.ADDRESS_ID_NOT_FOUND.format(id)));
        repository.delete(address);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<AddressGetDTO> update(Integer id, AddressUpdateDTO dto) {
        Address address = repository.findById(id).orElseThrow(()->
                new NotFoundException(ErrorCode.ADDRESS_ID_NOT_FOUND.format(id)));

        if(dto.address() != null && !dto.address().isEmpty())
            address.setAddress(dto.address());

        if(dto.address2() != null && !dto.address2().isEmpty())
            address.setAddress2(dto.address2());

        if(dto.district() != null && !dto.district().isEmpty())
            address.setDistrict(dto.district());

        if(dto.cityId() != null) {
            City city = cityRepository.findById(dto.cityId()).orElseThrow(() ->
                    new NotFoundException(ErrorCode.CITY_ID_NOT_FOUND));
            address.setCity(city);
        }

        if(dto.postalCode() != null && !dto.postalCode().isEmpty())
            address.setPostalCode(dto.postalCode());

        if(dto.phone() != null && !dto.phone().isEmpty())
            address.setPhone(dto.phone());

        Address saved = repository.save(address);
        return ResponseEntity.ok().body(addressMapper.toGetDTO(saved));
    }
}
