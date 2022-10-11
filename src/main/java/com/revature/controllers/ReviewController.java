package com.revature.controllers;

import com.revature.annotations.Authorized;
import com.revature.exceptions.NoProductReviewException;
import com.revature.exceptions.ProductNotFoundException;
import com.revature.exceptions.ProductReviewNotFoundException;
import com.revature.models.ProductReview;
import com.revature.repositories.ProductRepository;
import com.revature.repositories.ProductReviewRepository;
import com.revature.services.ProductReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductReviewByReviewId(
            @PathVariable("id") int reviewId
    ) throws ProductReviewNotFoundException
    {
        Optional<ProductReview> productReview = productReviewRepository.findById(reviewId);
        if(productReview.isPresent())
        {
            return ResponseEntity.ok(productReview.get());
        }
        throw new ProductReviewNotFoundException();
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

    @Authorized
    @PutMapping("/add")
    public ResponseEntity<?> addReview(
            @RequestParam(name = "id", required = true) int productId,
            @RequestParam(name = "customer_id", required = true) int customerId,
            @RequestParam(name = "rating", required = true) int rating,
            @RequestParam(name = "comment", required = true) String productComments
    ) throws ProductNotFoundException
    {
        if(!productRepository.existsById(productId))
        {
            throw new ProductNotFoundException();
        }
        ProductReview productReview = new ProductReview();
        productReview.setProductId(productId);
        productReview.setCustomerId(customerId);
        productReview.setRating(rating);
        productReview.setProductComments(productComments);
        return ResponseEntity.status(201).body(productReviewRepository.save(productReview));
    }

    @Authorized
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable("id") int reviewId,
            @RequestParam(name = "customer_id", required = true) int customerId,
            @RequestParam(name = "rating", required = true) int rating,
            @RequestParam(name = "comment", required = true) String productComments
    ) throws ProductReviewNotFoundException, NoPermissionException
    {
        Optional<ProductReview> review = productReviewRepository.findById(reviewId);
        if(review.isPresent())
        {
            if (review.get().getCustomerId() != customerId)
            {
                throw new NoPermissionException();
            }
            ProductReview productReview = new ProductReview();
            productReview.setReviewId(reviewId);
            productReview.setRating(rating);
            productReview.setProductComments(productComments);
            return ResponseEntity.status(201).body(productReviewRepository.save(productReview));
        }
        throw new ProductReviewNotFoundException();
    }

    @Authorized
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable("id") int reviewId,
            @RequestParam(name = "customer_id", required = true) int customerId
    ) throws NoPermissionException, ProductReviewNotFoundException
    {
        Optional<ProductReview> productReview = productReviewRepository.findById(reviewId);
        if(productReview.isPresent())
        {
            if(productReview.get().getCustomerId() != customerId)
            {
                throw new NoPermissionException();
            }
            productReviewRepository.deleteById(reviewId);
            return ResponseEntity.status(204).body("");
        }
        throw new ProductReviewNotFoundException();
    }
}
