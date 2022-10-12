package com.revature.repositories;

import com.revature.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByPrice(String name, double price);

    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByName(String name, double price);

    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByCategoryId(String name, double price);

    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByReviewCountDesc(String name, double price);

    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByQuantity(String name, double price);

    public Optional<Product> findByProductId(long productId);

    public void deleteByProductId(long productId);
}
