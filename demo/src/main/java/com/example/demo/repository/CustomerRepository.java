package com.example.demo.repository;

import com.example.demo.dto.CustomerQueryDTO;
import com.example.demo.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByEmail(String email);

    @Query(value = """
        SELECT DISTINCT new com.example.demo.dto.CustomerQueryDTO(c.customerId, c.firstName, c.lastName, c.email, f.title, r.rentalDate)
        FROM Rental r
        JOIN r.customer c
        JOIN r.inventory i
        JOIN i.film f
        WHERE r.returnDate IS NULL
        AND r.rentalDate <= :weekAgo
    """)
    List<CustomerQueryDTO> findNullReturnedDate(@Param("weekAgo") LocalDateTime weekAgo);

    @Query("""
        SELECT new com.example.demo.dto.CustomerQueryDTO(
            c.customerId,
            c.firstName,
            c.lastName,
            c.email,
            c.email,
            MAX(r.returnDate)
        )
        FROM Rental r
        JOIN r.customer c
        WHERE c.activebool = true
          AND NOT EXISTS (
              SELECT 1 FROM Rental r2
              WHERE r2.customer = c AND r2.returnDate IS NULL
          )
        GROUP BY c.customerId, c.firstName, c.lastName, c.email
        HAVING MAX(r.returnDate) < :monthAgo
    """)
    List<CustomerQueryDTO> findActiveUsersWithNoFilmsLeftToReturn(@Param("monthAgo") LocalDateTime monthAgo);
}
