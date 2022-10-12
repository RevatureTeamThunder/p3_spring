package com.revature.controllers;

import com.revature.annotations.Authorized;

import com.revature.exceptions.ProductNotFoundException;
import com.revature.models.Product;
import com.revature.repositories.ProductRepository;
import com.revature.services.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "http://p3-client.s3-website-us-east-1.amazonaws.com"}, allowCredentials = "true")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;

    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @Authorized
    @GetMapping
    public ResponseEntity<List<Product>> getInventory() {
        return ResponseEntity.ok(productService.findAll());
    }

    @Authorized
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam(name = "name", required = true) String name,
            @RequestParam(name = "order_by", required = false, defaultValue = "price") String orderBy,
            @RequestParam(name = "price", required = false, defaultValue = "9999.99") double price
    ) throws ProductNotFoundException
    {
        Optional<List<Product>> productList;
        switch(orderBy)
        {
            case "price":
                productList = productRepository.findAllByNameContainsAndPriceLessThanOrderByPrice(name, price);
                break;
            case "name":
                productList = productRepository.findAllByNameContainingAndPriceLessThanOrderByName(name, price);
                break;
            case "category":
            case "category_id":
                productList = productRepository.findAllByNameContainingAndPriceLessThanOrderByCategoryId(name, price);
                break;
            case "quantity":
                productList = productRepository.findAllByNameContainingAndPriceLessThanOrderByQuantity(name, price);
                break;
            case "review":
            case "reviews":
            case "review_count":
                productList = productRepository.findAllByNameContainingAndPriceLessThanOrderByReviewCountDesc(name, price);
                break;
            default:
                productList = productRepository.findAllByNameLikeAndPriceLessThanOrderByPrice(name, price);
                break;
        }
        if(productList.isPresent())
        {
            return ResponseEntity.ok(productList);
        }
        throw new ProductNotFoundException();
    }

    @Authorized
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable("id") long id) {
        Optional<Product> optional = productRepository.findByProductId(id);

        return optional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Authorized
    @PutMapping
    public ResponseEntity<Product> upsert(@RequestBody Product product) {
        return ResponseEntity.ok(productService.save(product));
    }


    @Authorized
    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") long id) {
        Optional<Product> optional = productRepository.findByProductId(id);

        if(!optional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        productRepository.deleteByProductId(id);

        return ResponseEntity.ok(optional.get());
    }
}
