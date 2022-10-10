package com.revature.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.models.CartItems;

public interface CartItemsRepository extends JpaRepository<CartItems, Integer>{

}
