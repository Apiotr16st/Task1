package com.example.demo.repository;

import com.example.demo.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByEmail(String email);
    Page<Customer> findByEmail(String email, Specification<Customer> specification, Pageable pageable);
    Page<Customer> findByFirstName(String firstName, Specification<Customer> specification, Pageable pageAble);
    Page<Customer> findByLastName(String firstName, Specification<Customer> specification, Pageable pageAble);
    Page<Customer> findByAddressCityCountryCountry(String value, Specification<Customer> specification, Pageable pageable);
    Page<Customer> findByAddressCityCity(String value, Specification<Customer> specification, Pageable pageable);
    Page<Customer> findByActivebool(Boolean active, Specification<Customer> specification, Pageable pageable);
}
