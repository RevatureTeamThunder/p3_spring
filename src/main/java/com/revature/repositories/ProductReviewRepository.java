package com.revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.ProductReview;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer>{

    Optional<List<ProductReview>> findAllByProductId(long productId);


    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "CALL update_rating(:productId, :customerId, :rating, :comments, 'add')", nativeQuery = true)
    public int add_rating(int productId, int customerId, int rating, String comments);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "CALL update_rating(:productId, :customerId, :rating, :comments, 'delete')", nativeQuery = true)
    public void delete_rating(int productId, int customerId, int rating, String comments);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "CALL update_rating(:productId, :customerId, :rating, :comments, :operation)", nativeQuery = true)
    public int update_rating(int productId, int customerId, int rating, String comments, String operation);

    public Optional<ProductReview> findProductReviewByCustomerIdAndProductId(int customerId, int productId);


    public Optional<ProductReview> findTopByCustomerIdAndProductIdOrderByReviewIdDesc(int customerId, int productId);

    Optional<List<ProductReview>> findAllByCustomerId(int customerId);

    Optional<ProductReview> findByReviewId(long reviewId);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    void deleteByReviewId(long reviewId);
}
