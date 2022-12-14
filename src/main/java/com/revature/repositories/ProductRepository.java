package com.revature.repositories;

import com.revature.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT * FROM product WHERE name ilike %:name% and price < :price ORDER By price", nativeQuery = true)
    public Optional<List<Product>> findAllByNameAndPriceLessThanOrderByPrice(String name, double price);

    @Query(value = "SELECT * from product where name ilike %:name%  and price < :price order by price", nativeQuery = true)
    public Optional<List<Product>> findAllByNameContainsAndPriceLessThanOrderByPrice(String name, double price);

    @Query(value = "SELECT * from product where name ilike %:name% AND price < :price order by name", nativeQuery = true)
    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByName(String name, double price);

    @Query(value = "SELECT * from product where name ilike %:name% AND price < :price order by category_id", nativeQuery = true)
    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByCategoryId(String name, double price);

    @Query(value = "SELECT * from product where name ilike %:name% AND price < :price order by review_count desc", nativeQuery = true)
    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByReviewCountDesc(String name, double price);

    @Query(value = "SELECT * from product where name ilike %:name% AND price < :price order by quantity", nativeQuery = true)
    public Optional<List<Product>> findAllByNameContainingAndPriceLessThanOrderByQuantity(String name, double price);

    @Query(value = "select * from product order by random() limit 2", nativeQuery = true)
    List<Product> getTwoRandom();

    @Query(value = "select * from product where review_count > 0 order by random() limit 2", nativeQuery = true)
    List<Product> getTwoRandomWithReviews();

    @Query(value = "select * from product where review_count > :reviewCount limit 1", nativeQuery = true)
    Product findByReviewCountGreaterThan(int reviewCount);

    public Optional<Product> findByProductId(long productId);

    public boolean existsByProductId(long productId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    public void deleteByProductId(long productId);

    @Query(value = "SELECT * from product where name ilike %:name% AND price < :price order by price", nativeQuery = true)
    Optional<List<Product>> findAllByNameLikeAndPriceLessThanOrderByPrice(String name, double price);

    @Query(value = "select * from product where product_id = :productId", nativeQuery = true)
    Optional<Product> exists(int productId);
}
