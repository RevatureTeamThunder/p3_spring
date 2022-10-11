package com.revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.ProductReview;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer>{

    Optional<List<ProductReview>> findAllByProductId(int productId);
}
