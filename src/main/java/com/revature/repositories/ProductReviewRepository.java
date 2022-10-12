package com.revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.ProductReview;
import org.springframework.data.jpa.repository.query.Procedure;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer>{

    Optional<List<ProductReview>> findAllByProductId(int productId);

    @Procedure(value = "update_rating")
    public void productReview(int productId, int customerId, int rating, String comments, String operation);

    public Optional<ProductReview> findProductReviewByCustomerIdAndProductId(int customerId, int productId);

    Optional<List<ProductReview>> findAllByCustomerId(int customerId);

    Optional<ProductReview> findByReviewId(long reviewId);
}
