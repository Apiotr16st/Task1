package com.example.demo.service;

import com.example.demo.dto.CustomerCreateDTO;
import com.example.demo.dto.CustomerGetDTO;
import com.example.demo.dto.CustomerUpdateDTO;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.model.Address;
import com.example.demo.model.Customer;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.specification.CustomerSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final CustomerMapper mapper;

    public CustomerService(CustomerRepository customerRepository, AddressRepository addressRepository, CustomerMapper mapper) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.mapper = mapper;
    }

    public List<CustomerGetDTO> getAll(Map<String, String> filter, Pageable pageable) {
        Map<String, String> sortMap = Map.of(
                "city", "address.city.city",
                "country", "address.city.country.country",
                "lastname", "lastName",
                "firstname", "firstName",
                "email", "email");

        Sort remappedSort = Sort.by(
                pageable.getSort()
                        .stream()
                        .map(order -> {
                            String actualField = sortMap.getOrDefault(order.getProperty().toLowerCase(), order.getProperty());
                            return new Sort.Order(order.getDirection(), actualField)
                                    .ignoreCase()
                                    .with(order.getNullHandling());
                        })
                        .toList()
        );
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), remappedSort);

        Set<String> keys = Set.of("firstname", "lastname", "email", "city", "country", "status");
        String search = filter.getOrDefault("search", "");

        Specification<Customer> spec = new CustomerSpecification(search);

        filter = filter.entrySet()
                .stream()
                .filter(e-> keys.contains(e.getKey().toLowerCase()))
                .collect(Collectors
                        .toMap(e -> e.getKey().toLowerCase(), Map.Entry::getValue)
                );

        if(!filter.isEmpty()){
            if (filter.containsKey("firstname"))
                spec = spec.and(CustomerSpecification.hasFirstName(filter.get("firstname")));

            else if (filter.containsKey("lastname"))
                spec = spec.and(CustomerSpecification.hasLastName(filter.get("lastname")));

            else if (filter.containsKey("email"))
                spec = spec.and(CustomerSpecification.hasEmail(filter.get("email")));

            else if (filter.containsKey("city"))
                spec = spec.and(CustomerSpecification.hasCity(filter.get("city")));

            else if (filter.containsKey("country"))
                spec = spec.and(CustomerSpecification.hasCountry(filter.get("country")));

            else if (filter.containsKey("status")) {
                String value = filter.get("status");
                if (!value.equalsIgnoreCase("active") && !value.equalsIgnoreCase("inactive"))
                    throw new DataIntegrityViolationException("Use value: active or inactive");
                if(value.equalsIgnoreCase("active"))
                    spec = spec.and(CustomerSpecification.hasStatus(true));
                else
                    spec = spec.and(CustomerSpecification.hasStatus(false));
            }
        }

        return customerRepository.findAll(spec, pageable).getContent()
                .stream()
                .map(mapper::toGetDTO)
                .toList();
    }

    public CustomerGetDTO getById(Integer id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("customerId"));
        return mapper.toGetDTO(customer);
    }

    public ResponseEntity<CustomerGetDTO> create(CustomerCreateDTO dto) {

        if (customerRepository.findByEmail(dto.email()).isPresent()) {
            throw new DataIntegrityViolationException("Email is already taken");
        }

        if (dto.firstName().isEmpty() || dto.lastName().isEmpty() || dto.email().isEmpty()) {
            throw new DataIntegrityViolationException("Name and email cannot be empty");
        }

        Customer customer = mapper.toEntity(dto);

        Address address = addressRepository.findById(dto.addressId())
                .orElseThrow(() -> new EntityNotFoundException("addressId"));

        customer.setAddress(address);

        Customer saved = customerRepository.save(customer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toGetDTO(saved));
    }


    public ResponseEntity<Customer> delete(Integer id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if(customer.isPresent()){
            customerRepository.delete(customer.get());
            return ResponseEntity.ok().build();      }
        else{
            throw new EntityNotFoundException("customerId");
        }
    }

    public ResponseEntity<CustomerGetDTO> update(Integer id, CustomerUpdateDTO dto) {
        Optional<Customer> optCustomer = customerRepository.findById(id);
        if(optCustomer.isPresent()){
            Customer customer = optCustomer.get();
            if(dto.storeId() != null && dto.storeId() >= 0)
                customer.setStoreId(dto.storeId());

            if(dto.firstName() != null && !dto.firstName().isEmpty())
                customer.setFirstName(dto.firstName());

            if(dto.lastName() != null && !dto.lastName().isEmpty())
                customer.setLastName(dto.lastName());

            if(dto.email() != null && !dto.email().isEmpty()) {
                if (customerRepository.findByEmail(dto.email()).isPresent() && !dto.email().equals(customer.getEmail())) {
                    throw new DataIntegrityViolationException("Wrong email");
                }
                customer.setEmail(dto.email());
            }

            if(dto.addressId() != null) {
                Address address = addressRepository.findById(dto.addressId()).orElseThrow(() -> new EntityNotFoundException("addressId"));
                customer.setAddress(address);
            }

            if(dto.active() != null)
            {
                if (dto.active() == 0 || dto.active() == 1)
                    customer.setActive(dto.active());
                else
                    throw new DataIntegrityViolationException("active must be 0 or 1");
            }

            if(dto.activebool() != null)
                customer.setActivebool(dto.activebool());

            customer.setLastUpdate(new Date());

            Customer saved = customerRepository.save(customer);
            return ResponseEntity.ok().body(mapper.toGetDTO(saved));
        }
        else{
            throw new EntityNotFoundException("customerId");
        }
    }
}
