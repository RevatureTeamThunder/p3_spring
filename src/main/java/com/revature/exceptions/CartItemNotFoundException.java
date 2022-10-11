package com.revature.exceptions;

public class CartItemNotFoundException extends Exception{

	public CartItemNotFoundException()
	{
		super();
	}

	public CartItemNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CartItemNotFoundException(Throwable cause)
	{
		super(cause);
	}

	protected CartItemNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CartItemNotFoundException(String message) {
		super(message);
	}
}
