package com.account.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.account.dao.DAOFactory;
import com.account.exception.CustomException;
import com.account.model.MoneyUtil;
import com.account.model.UserTransaction;

@Path( "/transaction" )
@Produces( MediaType.APPLICATION_JSON )
public class TransactionService
{

	private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.DATA);

	/**
	 * Transfer fund between two accounts.
	 * 
	 * @param transaction - user transaction
	 * @return - transaction response.
	 * @throws CustomException
	 */
	@POST
	public Response transferFund( UserTransaction transaction )
					throws CustomException
	{
		// Retrieve the currency code.
		String currency = transaction.getCurrencyCode();
		
		// Ensure that the currency code is valid. Set update count.
		if (MoneyUtil.INSTANCE.validateCcyCode(currency))
		{
			int updateCount = daoFactory.getAccountDAO()
							.transferAccountBalance(transaction);
			
			// If update count is valid, return response as okay.
			if (updateCount == 2)
			{
				return Response.status(Response.Status.OK).build();
			}
			else
			{
				// transaction failed
				throw new WebApplicationException("Transaction failed",
								Response.Status.BAD_REQUEST);
			}

		}
		// Otherwise, have invalid currency code - response is invalid.
		else
		{
			throw new WebApplicationException("Currency Code Invalid ",
							Response.Status.BAD_REQUEST);
		}

	}

}
