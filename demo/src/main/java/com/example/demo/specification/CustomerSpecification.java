package com.example.demo.specification;

import com.example.demo.model.Customer;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecification implements Specification<Customer> {

    private final String criteria;

    public CustomerSpecification(String criteria) {
        this.criteria = criteria;
    }

    public static Specification<Customer> hasFirstName(String value) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("firstName")), value.toLowerCase());
    }

    public static Specification<Customer> hasLastName(String value) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("lastName")), value.toLowerCase());
    }

    public static Specification<Customer> hasEmail(String value) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("email")), value.toLowerCase());
    }

    public static Specification<Customer> hasCity(String value) {
        return (root, query, cb) ->
                cb.equal(cb.lower(
                        root.join("address", JoinType.LEFT)
                                .join("city", JoinType.LEFT)
                                .get("city")), value.toLowerCase());
    }

    public static Specification<Customer> hasCountry(String value) {
        return (root, query, cb) ->
            cb.equal(cb.lower(
                    root.join("address", JoinType.LEFT)
                            .join("city", JoinType.LEFT)
                            .join("country", JoinType.LEFT)
                            .get("country")), value.toLowerCase());
    }

    public static Specification<Customer> hasStatus(Boolean value) {
        return (root, query, cb) -> cb.equal(root.get("activebool"), value);
    }

    @Override
    public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (criteria == null || criteria.isBlank())
            return cb.conjunction();

        String pattern = "%" + criteria.toLowerCase() + "%";

        Join<Object, Object> addressJoin = root.join("address", JoinType.LEFT);
        Join<Object, Object> cityJoin = addressJoin.join("city", JoinType.LEFT);
        Join<Object, Object> countryJoin = cityJoin.join("country", JoinType.LEFT);

        return cb.or(
                cb.like(cb.lower(root.get("firstName")), pattern),
                cb.like(cb.lower(root.get("lastName")), pattern),
                cb.like(cb.lower(cityJoin.get("city")),pattern),
                cb.like(cb.lower(countryJoin.get("country")), pattern)
        );
    }
}

