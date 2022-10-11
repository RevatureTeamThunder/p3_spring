package com.revature.services;

import com.revature.dtos.ProductInfo;

import com.revature.models.Product;
import com.revature.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //List all products
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    //View detailed information about a single product
    public Optional<Product> findById(int productId) {
        return productRepository.findById(productId);
    }

    //code by rev
    public Product save(Product product) {
        return productRepository.save(product);
    }
    
   //code by rev
    public List<Product> saveAll(List<Product> productList, List<ProductInfo> metadata) {
    	return productRepository.saveAll(productList);
    }

    //Delete a product
    public void delete(int productId) {
        productRepository.deleteById(productId);
    }
}
