package com.revature.services;

import org.springframework.stereotype.Service;

import com.revature.repositories.CartItemsRepository;

@Service
public class CartItemsServices {
	
	private final CartItemsRepository cartItemsRepository;
	
	public CartItemsServices(CartItemsRepository cartItemsRepository) {
		this.cartItemsRepository = cartItemsRepository;
	}

	public void removeCartItem(Integer productId) {
		cartItemsRepository.deleteById(productId);
	}
}
