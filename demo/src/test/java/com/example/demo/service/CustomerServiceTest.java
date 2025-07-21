package com.example.demo.service;

import com.example.demo.dto.AddressGetDTO;
import com.example.demo.dto.CustomerCreateDTO;
import com.example.demo.dto.CustomerGetDTO;
import com.example.demo.dto.CustomerUpdateDTO;
import com.example.demo.exception.EmptyInputException;
import com.example.demo.exception.EntityExistsException;
import com.example.demo.exception.InvalidFormatException;
import com.example.demo.exception.NotFoundException;
import com.example.demo.mapper.CustomerMapper;
import com.example.demo.model.Address;
import com.example.demo.model.Customer;
import com.example.demo.model.Gender;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private CustomerMapper mapper;

    @Mock
    private GenderService genderService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CustomerService customerService;

    private CustomerCreateDTO createDTO;
    private Address mockAddress;
    private Customer mappedCustomer;
    private Customer customer1;
    private Customer customer2;
    private CustomerGetDTO getCustomer1;
    private CustomerGetDTO getCustomer2;
    private CustomerUpdateDTO updateDTO;
    private Customer updatedCustomer;

    @BeforeEach
    void setup() {
        createDTO = new CustomerCreateDTO((short) 1, "John", "Doe", "john.doe@example.com", 1 ,1);
        mockAddress = mock(Address.class);
        mappedCustomer = new Customer();

        customer1 = new Customer(1, (short)1,"Jan", "Kowalski",
                "jan.kowalski@example.com", mockAddress, true, new Date(), new Date(), 1,Gender.MALE);
        customer2 = new Customer(2,(short)1,"Anna", "Nowak",
                "anna.nowak@example.com",mockAddress, true, new Date(), new Date(), 1,Gender.FEMALE);
        getCustomer1 = new CustomerGetDTO(1,"Jan", "Kowalski",
                "jan.kowalski@example.com", mock(AddressGetDTO.class), Gender.MALE);
        getCustomer2 = new CustomerGetDTO(2,"Anna", "Nowak",
                "anna.nowak@example.com", mock(AddressGetDTO.class),Gender.FEMALE);

        updateDTO = new CustomerUpdateDTO(
                (short)2, "NewFirstName", "NewLastName",
                "new.email@example.com", 3, 1, true);
    }

    // Get
    @Test
    @DisplayName("should return customer with id 1")
    void shouldGetCustomerById(){
        when(customerRepository.findById(1)).thenReturn(Optional.ofNullable(customer1));
        when(mapper.toGetDTO(customer1)).thenReturn(getCustomer1);
        assertEquals(getCustomer1, customerService.getById(1));
    }

    @Test
    @DisplayName("should throw exception that customer id does not exist")
    void shouldThrowNotFoundExceptionWhenCustomerIdNotExist(){
        when(customerRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, ()-> customerService.getById(1));
    }

    @Test
    @DisplayName("should return all (max 10) customers")
    void shouldGetAllCustomers() {
        Pageable pageable = PageRequest.of(0, 10);
        Map<String, String> filters = Map.of();
        Page<Customer> page = new PageImpl<>(List.of(customer1, customer2));
        when(customerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(mapper.toGetDTO(customer1)).thenReturn(getCustomer1);
        when(mapper.toGetDTO(customer2)).thenReturn(getCustomer2);

        Page<CustomerGetDTO> result = customerService.getAll(filters, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(List.of(getCustomer1, getCustomer2), result.getContent());
    }

    // Create
    @Test
    @DisplayName("should create customer when valid data given")
    void shouldCreateCustomerWhenValidDataGiven() {
        when(customerRepository.findByEmail(createDTO.email())).thenReturn(Optional.empty());
        when(emailService.validateEmail(createDTO.email())).thenReturn(false);
        when(mapper.toEntity(createDTO)).thenReturn(mappedCustomer);
        when(genderService.getGender(createDTO.firstName())).thenReturn("male");
        when(addressRepository.findById(createDTO.addressId())).thenReturn(Optional.of(mockAddress));
        when(customerRepository.save(mappedCustomer)).thenReturn(mappedCustomer);
        when(mapper.toGetDTO(mappedCustomer)).thenReturn(mock(CustomerGetDTO.class));

        ResponseEntity<CustomerGetDTO> response = customerService.create(createDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(customerRepository).save(mappedCustomer);
        verify(mapper).toGetDTO(mappedCustomer);
    }

    @Test
    @DisplayName("should throw EntityExistsException when email already exists")
    void shouldThrowEntityExistsExceptionWhenEmailExist(){
        when(customerRepository.findByEmail(createDTO.email())).thenReturn(Optional.of(new Customer()));

        assertThrows(EntityExistsException.class, ()-> customerService.create(createDTO));
    }

    @Test
    @DisplayName("should throw EmptyInputException when firstName is empty")
    void shouldThrowEmptyInputExceptionWhenFirstNameIsEmpty(){
        createDTO = new CustomerCreateDTO((short) 1, "", "Doe", "john.doe@example.com",1,1);
        assertThrows(EmptyInputException.class, () -> customerService.create(createDTO));
    }

    @Test
    @DisplayName("should throw EmptyInputException when lastName is empty")
    void shouldThrowEmptyInputExceptionWhenLastNameIsEmpty(){
        createDTO = new CustomerCreateDTO((short) 1, "John", "", "john.doe@example.com",1,1);
        assertThrows(EmptyInputException.class, () -> customerService.create(createDTO));
    }

    @Test
    @DisplayName("should throw EmptyInputException when email is empty")
    void shouldThrowEmptyInputExceptionWhenEmailIsEmpty(){
        createDTO = new CustomerCreateDTO((short) 1, "John", "Doe", "",1,1);
        assertThrows(EmptyInputException.class, () -> customerService.create(createDTO));
    }

    @Test
    @DisplayName("should throw NotFoundException when address not exist")
    void shouldThrowNotFoundExceptionWhenAddressNotExist(){
        when(mapper.toEntity(createDTO)).thenReturn(mappedCustomer);
        when(genderService.getGender(createDTO.firstName())).thenReturn("male");
        when(addressRepository.findById(createDTO.addressId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> customerService.create(createDTO));
    }


    // delete

    @Test
    @DisplayName("should delete customer")
    void shouldDeleteCustomer(){
        when(customerRepository.findById(1)).thenReturn(Optional.ofNullable(customer1));
        assertEquals(ResponseEntity.ok().build(), customerService.delete(1));
    }

    @Test
    @DisplayName("should throw NotFoundException when customer not exist")
    void shouldThrowNotFoundExceptionWhenCustomerNotExist(){
        when(customerRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, ()->customerService.delete(1));
    }

    // Update
    @Test
    @DisplayName("should update customer")
    void shouldUpdateCustomer(){
        Integer id = 1;

        Customer existingCustomer = new Customer();
        existingCustomer.setEmail("old.email@example.com");
        existingCustomer.setCustomerId(id);

        Address newAddress = new Address();
        Customer savedCustomer = new Customer();
        savedCustomer.setCustomerId(id);

        CustomerGetDTO expectedDto = new CustomerGetDTO(id, "NewFirstName", "NewLastName",
                "new.email@example.com", mock(AddressGetDTO.class), Gender.MALE);

        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.findByEmail(updateDTO.email())).thenReturn(Optional.empty());
        when(addressRepository.findById(updateDTO.addressId())).thenReturn(Optional.of(newAddress));
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);
        when(mapper.toGetDTO(savedCustomer)).thenReturn(expectedDto);

        ResponseEntity<CustomerGetDTO> response = customerService.update(id, updateDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());

        verify(customerRepository).findById(id);
        verify(customerRepository).findByEmail(updateDTO.email());
        verify(addressRepository).findById(updateDTO.addressId());
        verify(customerRepository).save(any(Customer.class));
        verify(mapper).toGetDTO(savedCustomer);

        assertEquals(updateDTO.storeId(), existingCustomer.getStoreId());
        assertEquals(updateDTO.firstName(), existingCustomer.getFirstName());
        assertEquals(updateDTO.lastName(), existingCustomer.getLastName());
        assertEquals(updateDTO.email(), existingCustomer.getEmail());
        assertEquals(newAddress, existingCustomer.getAddress());
        assertEquals(updateDTO.active(), existingCustomer.getActive());
        assertEquals(updateDTO.activebool(), existingCustomer.getActivebool());
        assertNotNull(existingCustomer.getLastUpdate());
    }

    @Test
    @DisplayName("should throw NotFoundException when customer not exist")
    void shouldThrowNotFoundCustomerIdNotExist(){
        when(customerRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, ()->customerService.update(1, updateDTO));
    }

    @Test
    @DisplayName("should throw NotFoundException when address not exist")
    void shouldThrowNotFoundAddressIdNotExist(){
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer1));
        when(customerRepository.findByEmail(updateDTO.email())).thenReturn(Optional.empty());
        when(addressRepository.findById(updateDTO.addressId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, ()->customerService.update(1, updateDTO));
    }

    @Test
    @DisplayName("should throw EntityExistsException when email exist")
    void shouldThrowEntityExistsExceptionEmailExists(){
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer1));
        when(customerRepository.findByEmail(updateDTO.email())).thenReturn(Optional.of(new Customer()));
        assertThrows(EntityExistsException.class, ()->customerService.update(1,updateDTO));
    }

    @Test
    @DisplayName("should throw InvalidFormatException when provided wrong active number")
    void shouldThrowInvalidFormatExceptionForActive(){
        updateDTO = new CustomerUpdateDTO(
                (short)2, "NewFirstName", "NewLastName",
                "new.email@example.com", 3, 2, true);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer1));
        when(customerRepository.findByEmail(updateDTO.email())).thenReturn(Optional.empty());
        when(addressRepository.findById(updateDTO.addressId())).thenReturn(Optional.of(mockAddress));
        assertThrows(InvalidFormatException.class, ()-> customerService.update(1, updateDTO));
    }
}
