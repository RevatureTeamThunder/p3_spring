package com.revature.services;

import java.util.List;

import org.springframework.stereotype.Service;
import com.revature.models.ProductReview;
import com.revature.repositories.ProductReviewRepository;

@Service
public class ProductReviewService {

	private final ProductReviewRepository productReviewRepository;
	
	public ProductReviewService(ProductReviewRepository productReviewRepository) {
		this.productReviewRepository = productReviewRepository;
	}
	
	public List<ProductReview> viewAllReviews(){
		return productReviewRepository.findAll();
	}
	
}
