package com.revature.repositories;

import com.revature.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByPrice(String name, double price);

    Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByName(String name, double price);

    Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByCategoryId(String name, double price);

    Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByReviewCountDesc(String name, double price);

    Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByQuantity(String name, double price);
}
