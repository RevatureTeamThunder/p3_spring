package com.revature.exceptions;

public class OrderHistoryNotFoundException extends Exception{

	public OrderHistoryNotFoundException()
	{
		super();
	}

	public OrderHistoryNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public OrderHistoryNotFoundException(Throwable cause)
	{
		super(cause);
	}

	protected OrderHistoryNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	private static final long serialVersionUID = 1L;

	public OrderHistoryNotFoundException(String message) {
		super(message);
	}
}
