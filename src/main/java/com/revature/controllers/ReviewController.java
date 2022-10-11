package com.revature.controllers;

import com.revature.exceptions.NoProductReviewException;
import com.revature.exceptions.ProductNotFoundException;
import com.revature.models.ProductReview;
import com.revature.repositories.ProductRepository;
import com.revature.repositories.ProductReviewRepository;
import com.revature.services.ProductReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/review")
public class ReviewController
{
    private ProductReviewRepository productReviewRepository;
    private ProductRepository productRepository;
    private ProductReviewService productReviewService;


    public ReviewController(ProductReviewRepository productReviewRepository, ProductRepository productRepository, ProductReviewService productReviewService)
    {
        this.productReviewRepository = productReviewRepository;
        this.productRepository = productRepository;
        this.productReviewService = productReviewService;
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> getProductReviewsByProductId(
            @PathVariable("id") int productId
    ) throws ProductNotFoundException, NoProductReviewException
    {
        if(!productRepository.existsById(productId))
        {
            throw new ProductNotFoundException();
        }
        Optional<List<ProductReview>> productReviewList = productReviewService.viewAllReviews(productId);
        if(productReviewList.isPresent())
        {
            return ResponseEntity.ok(productReviewList.get());
        }
        throw new NoProductReviewException();
    }
}
