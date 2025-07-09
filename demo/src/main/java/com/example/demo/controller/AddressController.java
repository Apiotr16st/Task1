package com.example.demo.controller;

import com.example.demo.dto.AddressCreateDTO;
import com.example.demo.dto.AddressGetDTO;
import com.example.demo.dto.AddressUpdateDTO;
import com.example.demo.model.Address;
import com.example.demo.service.AddressService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/all")
    public List<AddressGetDTO> getAddresses(){
        log.info("GET /address/all");
        return addressService.getAll();
    }

    @GetMapping("/{id}")
    public AddressGetDTO getAddress(@PathVariable Integer id){
        log.info("GET /address/{}", id);
        return addressService.getById(id);
    }

    @PostMapping
    public ResponseEntity<AddressGetDTO> createAddress(@Valid @RequestBody AddressCreateDTO address){
        log.info("POST /address + Request body: {}", address);
        return addressService.create(address);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressGetDTO> updateAddress(@PathVariable Integer id, @Valid @RequestBody AddressUpdateDTO address){
        log.info("PUT /address/{} + Request body: {}", id, address);
        return addressService.update(id, address);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Address> deleteAddress(@PathVariable Integer id){
        log.info("DELETE /address/{}", id);
        return addressService.delete(id);
    }

}
