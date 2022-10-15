package com.revature.controllers;

import com.revature.annotations.Authorized;
import com.revature.exceptions.NoProductReviewException;
import com.revature.exceptions.ProductNotFoundException;
import com.revature.exceptions.ProductReviewNotFoundException;
import com.revature.models.ProductReview;
import com.revature.models.ProductReviewView;
import com.revature.repositories.ProductRepository;
import com.revature.repositories.ProductReviewRepository;
import com.revature.repositories.ProductReviewViewRepository;
import com.revature.services.ProductReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/review")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "http://p3-client.s3-website-us-east-1.amazonaws.com"}, allowCredentials = "true")
public class ReviewController
{
    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final ProductReviewService productReviewService;
    private final ProductReviewViewRepository reviewViewRepository;


    public ReviewController(ProductReviewRepository productReviewRepository, ProductRepository productRepository, ProductReviewService productReviewService, ProductReviewViewRepository reviewViewRepository)
    {
        this.productReviewRepository = productReviewRepository;
        this.productRepository = productRepository;
        this.productReviewService = productReviewService;
        this.reviewViewRepository = reviewViewRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductReviewByReviewId(
            @PathVariable("id") long reviewId
    ) throws ProductReviewNotFoundException
    {
        Optional<ProductReview> productReview = productReviewRepository.findByReviewId(reviewId);
        if(productReview.isPresent())
        {
            return ResponseEntity.ok(productReview.get());
        }
        throw new ProductReviewNotFoundException();
    }

    @Authorized
    @GetMapping("/me")
    public ResponseEntity<?> getMyProductReviews(
            @RequestParam(name = "customer_id", required = true) int customerId
    ) throws ProductReviewNotFoundException
    {
        Optional<List<ProductReview>> productReviewList = productReviewRepository.findAllByCustomerId(customerId);
        if(productReviewList.isPresent())
        {
            return ResponseEntity.ok(productReviewList.get());
        }
        throw new ProductReviewNotFoundException();
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> getProductReviewsByProductId(
            @PathVariable("id") int productId
    ) throws ProductNotFoundException, NoProductReviewException
    {
        if(!productRepository.existsByProductId(productId))
        {
            throw new ProductNotFoundException();
        }
        Optional<List<ProductReviewView>> productReviewList = reviewViewRepository.findAllByProductId(productId);
        if(productReviewList.isPresent())
        {
            return ResponseEntity.ok(productReviewList.get());
        }
        throw new NoProductReviewException();
    }

    @Authorized
    @PutMapping("/add")
    public ResponseEntity<?> addReview(
            @RequestParam(name = "id", required = true) long productId,
            @RequestParam(name = "customer_id", required = true) int customerId,
            @RequestParam(name = "rating", required = true) int rating,
            @RequestParam(name = "comment", required = true) String productComments
    ) throws ProductNotFoundException, NoProductReviewException
    {
        if(!productRepository.existsByProductId(productId))
        {
            throw new ProductNotFoundException();
        }

        // Call the stored procedure to update rating properly.
        productReviewRepository.add_rating((int) productId, customerId, rating, productComments);
        Optional<ProductReview> productReview = productReviewRepository.findTopByCustomerIdAndProductIdOrderByReviewIdDesc(customerId, (int) productId);
        if(productReview.isPresent())
        {
            return ResponseEntity.status(201).body(productReview.get());
        }
        throw new NoProductReviewException();
    }

    @Authorized
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable("id") long reviewId,
            @RequestParam(name = "customer_id", required = true) int customerId,
            @RequestParam(name = "rating", required = true) int rating,
            @RequestParam(name = "comment", required = true) String productComments
    ) throws ProductReviewNotFoundException, NoPermissionException
    {
        Optional<ProductReview> review = productReviewRepository.findByReviewId(reviewId);
        if(review.isPresent())
        {
            if (review.get().getCustomerId() != customerId)
            {
                throw new NoPermissionException();
            }
            // Call the stored procedure
            productReviewRepository.update_rating(review.get().getProductId(), customerId, rating, productComments, "update");
            // Only the rating and comments need to be altered before returning.
            review.get().setRating(rating);
            review.get().setProductComments(productComments);
            return ResponseEntity.ok(review.get());
        }
        throw new ProductReviewNotFoundException();
    }

    @Authorized
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable("id") long reviewId,
            @RequestParam(name = "customer_id", required = true) int customerId
    ) throws NoPermissionException, ProductReviewNotFoundException
    {
        Optional<ProductReview> productReview = productReviewRepository.findByReviewId(reviewId);
        if(productReview.isPresent())
        {
            if(productReview.get().getCustomerId() != customerId)
            {
                throw new NoPermissionException();
            }
            // Call the stored procedure to delete it.
            productReviewRepository.delete_rating(productReview.get().getProductId(), customerId, 0, "");
            return ResponseEntity.status(204).body("");
        }
        throw new ProductReviewNotFoundException();
    }
}
