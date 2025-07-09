package com.example.demo.controller;

import com.example.demo.dto.CustomerCreateDTO;
import com.example.demo.dto.CustomerGetDTO;
import com.example.demo.dto.CustomerUpdateDTO;
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import com.example.demo.util.LogParser;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/all")
    public Page<CustomerGetDTO> getCustomers(@RequestParam Map<String, String> filter,
                                             @PageableDefault(page =0 , size = 10, sort="customerId") Pageable pageable) {
        log.info("GET /customer/all params: {}, {}", filter.toString(), pageable);
        return customerService.getAll(filter, pageable);
    }

    @GetMapping("/{id}")
    public CustomerGetDTO getCustomer(@PathVariable Integer id){
        log.info("GET /customer/{}", id);
        System.out.println(LogParser.encodeEmail(customerService.getById(id).email()));
        return customerService.getById(id);
    }

    @PostMapping
    public ResponseEntity<CustomerGetDTO> createCustomer(@Valid @RequestBody CustomerCreateDTO customer){
        log.info("POST /customer Request Body: {}", customer);
        return customerService.create(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerGetDTO> updateCustomer(@PathVariable Integer id, @Valid @RequestBody CustomerUpdateDTO customer){
        log.info("PUT /customer/{} Request Body: {}", id, customer);
        return customerService.update(id, customer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable Integer id){
        log.info("DELETE /customer/{}", id);
        return customerService.delete(id);
    }
}
