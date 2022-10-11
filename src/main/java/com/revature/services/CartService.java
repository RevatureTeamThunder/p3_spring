package com.revature.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.revature.exceptions.CartItemNotFoundException;
import com.revature.models.Cart;
import com.revature.repositories.CartRepository;

@Service
public class CartService {

	private final CartRepository cartRepository;
	
	public CartService(CartRepository cartRepository) {
		this.cartRepository = cartRepository;
	}
	
	public List<Cart> findAll(){
		return cartRepository.findAll();
	}
	
	public Cart findById(Integer cartId) throws CartItemNotFoundException {
		Optional<Cart> cart = cartRepository.findById(cartId);
		if (cart.isPresent()) {
			return cart.get();
		} else {
			throw new CartItemNotFoundException("Cart Not Found");
		}
		
		
	}
	public void updatePurchase(Integer cartId) throws CartItemNotFoundException {
		Cart updatePurchase = this.findById(cartId);
		updatePurchase.setPurchased(true);
	}
	
}
