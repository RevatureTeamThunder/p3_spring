package com.revature.repositories;

import com.revature.models.ProductReviewView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductReviewViewRepository extends JpaRepository<ProductReviewView, Long>
{
    Optional<List<ProductReviewView>> findAllByProductId(int productId);
}
