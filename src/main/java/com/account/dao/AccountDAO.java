package com.account.dao;

import com.account.exception.CustomException;
import com.account.model.Account;
import com.account.model.UserTransaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface of Account actions and/or functionality.
 * 
 * @author mccormam
 */
public interface AccountDAO
{
	/**
	 * Retrieve a list of all the accounts in the database
	 * 
	 * @return- list of all accounts in database.
	 * @throws CustomException
	 */
	List<Account> getAllAccounts() throws CustomException;

	/**
	 * Retrieve the account details by account id.
	 * 
	 * @param accountId - user account id.
	 * @return - user account details.
	 * @throws CustomException
	 */
	Account getAccountById( long accountId ) throws CustomException;

	/**
	 * Create an account using the account details.
	 * 
	 * @param account - user account details object.
	 * @return - user account id to confirm.
	 * @throws CustomException
	 */
	long createAccount( Account account ) throws CustomException;

	/**
	 * Delete/Remove account details by account id.
	 * 
	 * @param accountId - user account id.
	 * @return
	 * @throws CustomException
	 */
	int deleteAccountById( long accountId ) throws CustomException;

	/**
	 * Update given account by an amount.
	 * 
	 * @param accountId - user accountId
	 * @param deltaAmount - amount to be debit(less than 0)/credit(greater than 0).
	 * @return - number of rows updated
	 */
	int updateAccountBalance( long accountId, BigDecimal deltaAmount )
					throws CustomException;

	/**
	 * Transfer the user account balance.
	 * 
	 * @param userTransaction
	 * @return
	 * @throws CustomException
	 */
	int transferAccountBalance( UserTransaction userTransaction )
					throws CustomException;
}
