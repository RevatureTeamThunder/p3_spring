package com.revature.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.revature.exceptions.CartItemNotFoundException;
import com.revature.models.CartItems;
import com.revature.repositories.CartItemsRepository;

@Service
public class CartItemsService {
	
	private final CartItemsRepository cartItemsRepository;
	
	public CartItemsService(CartItemsRepository cartItemsRepository) {
		this.cartItemsRepository = cartItemsRepository;
	}
	//View all items in a cart by customer id
	public List<CartItems> findByCustomerId(Integer customerId) {
	        return cartItemsRepository.findByCustomerId(customerId);
    }
	//View all items in a cart by provided id
	public List<CartItems> findById() {
        return cartItemsRepository.findAll();
	}
	//View 1 item for the cart by product id
    public CartItems findByProductId(Integer productId) throws CartItemNotFoundException {
    		Optional<CartItems> cartItem = cartItemsRepository.findById(productId);
    		if(cartItem.isPresent()) {
    			return cartItem.get();
    		} else {
    	throw new CartItemNotFoundException("Cart Item Not Found");	 
    		}
    }
	//Puts the product into the cart
    public CartItems addToCart(CartItems cartItem) {
        return cartItemsRepository.save(cartItem);
    }
	//Update the quantity of the item in the shopping cart
    public void updateCartItem(Integer productId, CartItems cartItem) throws CartItemNotFoundException {
    	CartItems updateItem = this.findByProductId(productId);
    	
    	updateItem.setQuantity(cartItem.getQuantity());
    	
    	cartItemsRepository.save(updateItem);
    }
	//Remove an item from the cart
    public void removeCartItem(Integer productId) {
		cartItemsRepository.deleteById(productId);
	}
    
	//Empty the shopping cart for the specified customer
    public void deleteAllItemsByCustomerId(Integer customerId) {
    	List<CartItems> cartByCustomer = this.findByCustomerId(customerId);
    	cartItemsRepository.deleteAll(cartByCustomer);
}
}
