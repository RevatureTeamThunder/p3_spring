package com.revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

}
