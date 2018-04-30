package com.account.exception;

/**
 * Error code response.
 * 
 * @author mccormam
 */
public class ErrorResponse
{
	private String errorCode;

	/**
	 * Get response error code.
	 * 
	 * @return - error code.
	 */
	public String getErrorCode()
	{
		return errorCode;
	}

	/**
	 * Set the response error code.
	 * 
	 * @param errorCode
	 */
	public void setErrorCode( String errorCode )
	{
		this.errorCode = errorCode;
	}
	
}
