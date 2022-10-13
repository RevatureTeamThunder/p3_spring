package com.revature.repositories;

import com.revature.models.Product;
import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT * FROM product WHERE name LIKE %:name% and price < :price ORDER By price", nativeQuery = true)
    public Optional<List<Product>> findAllByNameLikeAndPriceLessThanOrderByPrice(String name, double price);

    @Query(value = "SELECT * from product where name Like %:name%  and price < :price order by price", nativeQuery = true)
    public Optional<List<Product>> findAllByNameContainsAndPriceLessThanOrderByPrice(String name, double price);

    @Query(value = "SELECT * from product where name Like %:name% AND price < :price order by name", nativeQuery = true)
    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByName(String name, double price);

    @Query(value = "SELECT * from product where name Like %:name% AND price < :price order by category_id", nativeQuery = true)
    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByCategoryId(String name, double price);

    @Query(value = "SELECT * from product where name Like %:name% AND price < :price order by review_count desc", nativeQuery = true)
    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByReviewCountDesc(String name, double price);

    @Query(value = "SELECT * from product where name Like %:name% AND price < :price order by quantity", nativeQuery = true)
    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByQuantity(String name, double price);

    public Optional<Product> findByProductId(long productId);

    public boolean existsByProductId(long productId);

    public void deleteByProductId(long productId);
}
