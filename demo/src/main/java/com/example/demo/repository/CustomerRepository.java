package com.example.demo.repository;

import com.example.demo.dto.CustomerQueryDTO;
import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByEmail(String email);

    @Query(value = """
        SELECT
            c.first_name AS firstName,
            c.last_name AS lastName,
            c.email AS email,
            f.title AS filmTitle,
            r.rental_date
        FROM rental r
        JOIN customer c ON c.customer_id = r.customer_id
        JOIN inventory i ON i.inventory_id = r.inventory_id
        JOIN film f ON f.film_id = i.film_id
        WHERE r.return_date IS NULL
    """, nativeQuery = true)
    List<CustomerQueryDTO> findNullReturnedDate();

    @Query(value = """
        SELECT *
        FROM customer c
        WHERE c.activebool = true
          AND EXISTS (
              SELECT 1
              FROM rental r
              WHERE r.customer_id = c.customer_id
          )
          AND NOT EXISTS (
              SELECT 1
              FROM rental r
              WHERE r.customer_id = c.customer_id
                AND r.return_date IS NULL
          )
    """, nativeQuery = true)
    List<Customer> findActiveUsersWithNoFilmsLeftToReturn();
}
