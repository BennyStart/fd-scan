package com.fardo.common.exception;

public class FdException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FdException(String message){
		super(message);
	}
	
	public FdException(Throwable cause)
	{
		super(cause);
	}
	
	public FdException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
