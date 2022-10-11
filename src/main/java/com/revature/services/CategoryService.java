package com.revature.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.revature.dtos.ProductInfo;
import com.revature.models.Category;
import com.revature.repositories.CategoryRepository;

@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;
	
	public CategoryService(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(int id) {
        return categoryRepository.findById(id);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    
    public List<Category> saveAll(List<Category> categoryList, List<ProductInfo> metadata) {
    	return categoryRepository.saveAll(categoryList);
    }

    public void delete(int id) {
    	categoryRepository.deleteById(id);
    }
	
}
