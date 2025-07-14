package com.example.demo.service;

import com.example.demo.dto.CustomerCreateDTO;
import com.example.demo.dto.CustomerGetDTO;
import com.example.demo.dto.CustomerUpdateDTO;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.model.Address;
import com.example.demo.model.Customer;
import com.example.demo.model.Gender;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.specification.CustomerSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
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
    private final GenderService genderService;
    private final EmailService emailService;

    public CustomerService(CustomerRepository customerRepository, AddressRepository addressRepository, CustomerMapper mapper, GenderService genderService, EmailService emailService) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.mapper = mapper;
        this.genderService = genderService;
        this.emailService = emailService;
    }

    public Page<CustomerGetDTO> getAll(Map<String, String> filter, Pageable pageable) {
        pageable = remapSorting(pageable);
        Specification<Customer> spec = filterSpec(filter);
        return customerRepository.findAll(spec, pageable)
                .map(mapper::toGetDTO);
    }

    private Pageable remapSorting(Pageable pageable) {
        Map<String, String> sortMap = Map.of(
                "city", "address.city.city",
                "country", "address.city.country.country",
                "lastname", "lastName",
                "firstname", "firstName",
                "email", "email"
        );

        Sort mappedSort = pageable.getSort().stream()
                .map(order -> new Sort.Order(
                        order.getDirection(),
                        sortMap.getOrDefault(order.getProperty().toLowerCase(), order.getProperty()))
                        .ignoreCase()
                        .with(order.getNullHandling()))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Sort::by));

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), mappedSort);
    }

    private Specification<Customer> filterSpec(Map<String, String> filter){
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
            for (String fil: filter.keySet()) {
                spec = switch (fil) {
                    case "firstname" -> spec.and(CustomerSpecification.hasFirstName(filter.get("firstname")));
                    case "lastname" -> spec.and(CustomerSpecification.hasLastName(filter.get("lastname")));
                    case "email" -> spec.and(CustomerSpecification.hasEmail(filter.get("email")));
                    case "city" -> spec.and(CustomerSpecification.hasCity(filter.get("city")));
                    case "country" -> spec.and(CustomerSpecification.hasCountry(filter.get("country")));
                    case "status" -> spec.and(CustomerSpecification.hasStatus(parseStatus(filter.get("status"))));
                    default -> spec;
                };
            }
        }
        return spec;
    }

    private boolean parseStatus(String value) {
        return switch (value.toLowerCase()) {
            case "active" -> true;
            case "inactive" -> false;
            default -> throw new DataIntegrityViolationException("Use value: active or inactive");
        };
    }

    public CustomerGetDTO getById(Integer id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("customerId"));
        return mapper.toGetDTO(customer);
    }

    public ResponseEntity<CustomerGetDTO> create(CustomerCreateDTO dto) {

        if (customerRepository.findByEmail(dto.email()).isPresent()) {
            throw new DataIntegrityViolationException("Email is already taken");
        }

        if (emailService.validateEmail(dto.email())){
            throw new DataIntegrityViolationException("Email is disposable");
        }

        if (dto.firstName().isEmpty() || dto.lastName().isEmpty() || dto.email().isEmpty()) {
            throw new DataIntegrityViolationException("Name and email cannot be empty");
        }

        Customer customer = mapper.toEntity(dto);

        String gender = genderService.getGender(dto.firstName());
        customer.setGender(Gender.fromString(gender));

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
