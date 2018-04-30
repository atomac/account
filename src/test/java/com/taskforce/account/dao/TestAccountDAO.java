package com.taskforce.account.dao;

import com.account.dao.DAOFactory;
import com.account.exception.CustomException;
import com.account.model.Account;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Test suite for Account Data Access Object functionality.
 * 
 * @author mccormam
 */
public class TestAccountDAO
{
	private static final DAOFactory DataDAOFactory = DAOFactory
					.getDAOFactory(DAOFactory.DATA);

	@BeforeClass
	public static void setup()
	{
		// Prepare test database and test data. Test data are initialised from
		// src/test/resources/demo.sql
		DataDAOFactory.populateTestData();
	}

	@After
	public void tearDown()
	{

	}

	/**
	 * Test to retrieve all account details.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testGetAllAccounts() throws CustomException
	{
		List<Account> allAccounts = DataDAOFactory.getAccountDAO()
						.getAllAccounts();
		assertTrue(allAccounts.size() > 1);
	}

	/**
	 * Test to retrieve dedicated account details by Id.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testGetAccountById() throws CustomException
	{
		Account account = DataDAOFactory.getAccountDAO().getAccountById(1L);

		assertTrue(account.getUserName().equals("frederick"));
	}

	/** 
	 * Test to retrieve account record for id that does not exist. Expect to
	 * fail.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testGetNonExistingAccById() throws CustomException
	{
		Account account = DataDAOFactory.getAccountDAO().getAccountById(100L);
		
		assertTrue(account == null);
	}

	/**
	 * Test to create new account record.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testCreateAccount() throws CustomException
	{
		BigDecimal balance = new BigDecimal(10).setScale(4,
						RoundingMode.HALF_EVEN);
		
		Account a = new Account("piere", balance, "CDN");
		
		long aid = DataDAOFactory.getAccountDAO().createAccount(a);
		
		Account afterCreation = DataDAOFactory.getAccountDAO().getAccountById(aid);
		
		assertTrue(afterCreation.getUserName().equals("piere"));
		assertTrue(afterCreation.getCurrencyCode().equals("CDN"));
		assertTrue(afterCreation.getBalance().equals(balance));
	}

	/**
	 * Test to delete dedicated account record.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testDeleteAccount() throws CustomException
	{
		int rowCount = DataDAOFactory.getAccountDAO().deleteAccountById(2L);
		
		// assert one row(user) deleted
		assertTrue(rowCount == 1);
		// assert user no longer there
		assertTrue(DataDAOFactory.getAccountDAO().getAccountById(2L) == null);
	}

	/**
	 * Test to attempt delete a non-existing account - expect failure.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testDeleteNonExistingAccount() throws CustomException
	{
		int rowCount = DataDAOFactory.getAccountDAO().deleteAccountById(500L);
		
		// assert no row(user) deleted
		assertTrue(rowCount == 0);
	}

	/**
	 * Test to Update account balance.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testUpdateAccountBalanceSufficientFund() throws CustomException
	{
		BigDecimal deltaDeposit = new BigDecimal(50).setScale(4,
						RoundingMode.HALF_EVEN);
		BigDecimal afterDeposit = new BigDecimal(150).setScale(4,
						RoundingMode.HALF_EVEN);
		
		int rowsUpdated = DataDAOFactory.getAccountDAO().updateAccountBalance(
						1L, deltaDeposit);
		
		assertTrue(rowsUpdated == 1);
		
		assertTrue(DataDAOFactory.getAccountDAO().getAccountById(1L)
						.getBalance().equals(afterDeposit));
		
		BigDecimal deltaWithDraw = new BigDecimal(-50).setScale(4,
						RoundingMode.HALF_EVEN);
		BigDecimal afterWithDraw = new BigDecimal(100).setScale(4,
						RoundingMode.HALF_EVEN);
		
		int rowsUpdatedW = DataDAOFactory.getAccountDAO().updateAccountBalance(
						1L, deltaWithDraw);
		
		assertTrue(rowsUpdatedW == 1);
		assertTrue(DataDAOFactory.getAccountDAO().getAccountById(1L)
						.getBalance().equals(afterWithDraw));

	}

	/**
	 * Test Custom Exception attempting account balance not enough funds.
	 * 
	 * @throws CustomException
	 */
	@Test( expected = CustomException.class )
	public void testUpdateAccountBalanceNotEnoughFund() throws CustomException
	{
		BigDecimal deltaWithDraw = new BigDecimal(-50000).setScale(4,
						RoundingMode.HALF_EVEN);
		int rowsUpdatedW = DataDAOFactory.getAccountDAO().updateAccountBalance(
						1L, deltaWithDraw);
		assertTrue(rowsUpdatedW == 0);

	}

}