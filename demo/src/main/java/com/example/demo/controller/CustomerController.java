package com.example.demo.controller;

import com.example.demo.dto.CustomerCreateDTO;
import com.example.demo.dto.CustomerGetDTO;
import com.example.demo.dto.CustomerUpdateDTO;
import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.Map;

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
        return customerService.getAll(filter, pageable);
    }

    @GetMapping("/{id}")
    public CustomerGetDTO getCustomer(@PathVariable Integer id){
        return customerService.getById(id);
    }

    @PostMapping
    public ResponseEntity<CustomerGetDTO> createCustomer(@Valid @RequestBody CustomerCreateDTO customer){
        return customerService.create(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerGetDTO> updateCustomer(@PathVariable Integer id, @Valid @RequestBody CustomerUpdateDTO customer){
        return customerService.update(id, customer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable Integer id){
        return customerService.delete(id);
    }
}
