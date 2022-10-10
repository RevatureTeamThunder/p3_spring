package com.revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.ProductReview;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer>{

}
