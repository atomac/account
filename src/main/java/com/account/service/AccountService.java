package com.account.service;

import com.account.dao.DAOFactory;
import com.account.exception.CustomException;
import com.account.model.Account;
import com.account.model.MoneyUtil;

import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Account Service
 */
@Path( "/account" )
@Produces( MediaType.APPLICATION_JSON )
public class AccountService
{
	private static Logger log = Logger.getLogger(AccountService.class);
	
	private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.DATA);

	/**
	 * Find all accounts
	 * 
	 * @return - all account details
	 * @throws CustomException
	 */
	@GET
	@Path( "/all" )
	public List<Account> getAllAccounts() throws CustomException
	{
		return daoFactory.getAccountDAO().getAllAccounts();
	}

	/**
	 * Find by account id
	 * 
	 * @param accountId - account id
	 * @return - account object details.
	 * @throws CustomException
	 */
	@GET
	@Path( "/{accountId}" )
	public Account getAccount( @PathParam( "accountId" ) long accountId )
					throws CustomException
	{
		return daoFactory.getAccountDAO().getAccountById(accountId);
	}

	/**
	 * Find balance by account Id
	 * 
	 * @param accountId - account id
	 * @return - account balance
	 * @throws CustomException
	 */
	@GET
	@Path( "/{accountId}/balance" )
	public BigDecimal getBalance( @PathParam( "accountId" ) long accountId )
					throws CustomException
	{
		// Retrieve account details from account data table.
		final Account account = daoFactory.getAccountDAO().getAccountById(
						accountId);

		// If account is null, throw exception.
		if (account == null)
		{
			throw new WebApplicationException("Account not found",
							Response.Status.NOT_FOUND);
		}
		
		// Return balance.
		return account.getBalance();
	}

	/**
	 * Create Account
	 * 
	 * @param account - account object details.
	 * @return - new account details.
	 * @throws CustomException
	 */
	@PUT
	@Path( "/create" )
	public Account createAccount( Account account ) throws CustomException
	{
		// Create account
		final long accountId = daoFactory.getAccountDAO()
						.createAccount(account);
		
		// Return account details.
		return daoFactory.getAccountDAO().getAccountById(accountId);
	}

	/**
	 * Deposit amount by account Id
	 * 
	 * @param accountId - account id.
	 * @param amount - account amount
	 * @return - updated account details.
	 * @throws CustomException
	 */
	@PUT
	@Path( "/{accountId}/deposit/{amount}" )
	public Account deposit( @PathParam( "accountId" ) long accountId,
					@PathParam( "amount" ) BigDecimal amount )
					throws CustomException
	{

		// If amount is not valid, have a web aplication exception.
		if (amount.compareTo(MoneyUtil.zeroAmount) <= 0)
		{
			throw new WebApplicationException("Invalid Deposit amount",
							Response.Status.BAD_REQUEST);
		}

		// Update the account amount.
		daoFactory.getAccountDAO().updateAccountBalance(accountId,
						amount.setScale(4, RoundingMode.HALF_EVEN));
		
		// Return the update account details.
		return daoFactory.getAccountDAO().getAccountById(accountId);
	}

	/**
	 * Withdraw amount by account Id
	 * 
	 * @param accountId - account id
	 * @param amount - amount to withdraw.
	 * @return - updated account details.
	 * @throws CustomException
	 */
	@PUT
	@Path( "/{accountId}/withdraw/{amount}" )
	public Account withdraw( @PathParam( "accountId" ) long accountId,
					@PathParam( "amount" ) BigDecimal amount )
					throws CustomException
	{

		// If amount is invalid, throw exception.
		if (amount.compareTo(MoneyUtil.zeroAmount) <= 0)
		{
			throw new WebApplicationException("Invalid Deposit amount",
							Response.Status.BAD_REQUEST);
		}
		
		// Set withdrawal amount.
		BigDecimal delta = amount.negate();
		
		// If debug is enabled, output log message.
		if (log.isDebugEnabled())
		{
			log.debug("Withdraw service: delta change to account  " + delta
							+ " Account ID = " + accountId);
		}
		
		// Update account balance.
		daoFactory.getAccountDAO().updateAccountBalance(accountId,
						delta.setScale(4, RoundingMode.HALF_EVEN));
		
		// Return updated account details.
		return daoFactory.getAccountDAO().getAccountById(accountId);
	}

	/**
	 * Delete amount by account Id
	 * 
	 * @param accountId - account id
	 * @param amount - amount to delete.
	 * @return - account response
	 * @throws CustomException
	 */
	@DELETE
	@Path( "/{accountId}" )
	public Response deleteAccount( @PathParam( "accountId" ) long accountId )
					throws CustomException
	{
		// Retrieve account details by account id.
		int deleteCount = daoFactory.getAccountDAO().deleteAccountById(
						accountId);
		
		// Compare for valid response for delete
		if (deleteCount == 1)
		{
			return Response.status(Response.Status.OK).build();
		}
		// Otherwise details not found.
		else
		{
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

}
