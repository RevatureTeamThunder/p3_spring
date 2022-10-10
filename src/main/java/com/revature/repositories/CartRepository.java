package com.revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer>{

}
