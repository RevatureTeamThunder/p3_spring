package com.revature.exceptions;

public class CartItemNotFoundException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CartItemNotFoundException(String message) {
		super(message);
	}
}
