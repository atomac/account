package com.account.service;

import org.apache.log4j.Logger;
import com.account.exception.ErrorResponse;
import com.account.exception.CustomException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Class for Service Exception Manager.
 * 
 * @author mccormam
 */
@Provider
public class ServiceExceptionMapper implements ExceptionMapper<CustomException>
{
	private static Logger log = Logger.getLogger(ServiceExceptionMapper.class);

	/**
	 * Constructor for the Service Exception Manager.
	 */
	public ServiceExceptionMapper()
	{
	}

	/**
	 * Manage Service Exception Manager Response.
	 */
	public Response toResponse( CustomException daoException )
	{
		// If ddebug is enabled, write to log.
		if (log.isDebugEnabled())
		{
			log.debug("Mapping exception to Response....");
		}
		
		// Instantiate error response.
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(daoException.getMessage());

		// return internal server error for DAO exceptions
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(errorResponse).type(MediaType.APPLICATION_JSON)
						.build();
	}

}
