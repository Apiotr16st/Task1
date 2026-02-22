package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;
    @Column( name = "store_id")
    private Short storeId;

    @Column(name = "first_name", length = 45)
    private String firstName;

    @Column(name = "last_name", length = 45)
    private String lastName;

    @Column(length = 45)
    private String email;

    @NotNull(message = "Address cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    private Boolean activebool;

    @Column(name ="create_date")
    private Date createDate;

    @Column(name="last_update")
    private Date lastUpdate;

    private Integer active;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Gender gender;
}
