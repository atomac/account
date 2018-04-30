package com.taskforce.account.dao;

import com.account.dao.AccountDAO;
import com.account.dao.DAOFactory;
import com.account.dao.DataDAOFactory;
import com.account.exception.CustomException;
import com.account.model.Account;
import com.account.model.UserTransaction;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import static junit.framework.TestCase.assertTrue;

public class TestAccountBalance
{
	private static Logger log = Logger.getLogger(TestAccountDAO.class);
	
	private static final int THREADS_COUNT = 100;
	
	private static final DAOFactory DataDaoFactory = DAOFactory
					.getDAOFactory(DAOFactory.DATA);

	@BeforeClass
	public static void setup()
	{
		// Prepare test database and test data, Test data are initialised from
		// src/test/resources/demo.sql
		DataDaoFactory.populateTestData();
	}

	@After
	public void tearDown()
	{

	}

	/**
	 * Test for the transfer of a balance from one account to another.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testAccountSingleThreadSameCcyTransfer() throws CustomException
	{
		final AccountDAO accountDAO = DataDaoFactory.getAccountDAO();

		BigDecimal transferAmount = new BigDecimal(50.01234).setScale(4,
						RoundingMode.HALF_EVEN);

		UserTransaction transaction = new UserTransaction("EUR",
						transferAmount, 5L, 6L);

		long startTime = System.currentTimeMillis();

		accountDAO.transferAccountBalance(transaction);
		
		long endTime = System.currentTimeMillis();

		log.info("TransferAccountBalance finished, time taken: "
						+ (endTime - startTime) + "ms");

		Account accountFrom = accountDAO.getAccountById(5);

		Account accountTo = accountDAO.getAccountById(6);

		log.debug("Account From: " + accountFrom);

		log.debug("Account From: " + accountTo);

		assertTrue(accountFrom.getBalance().compareTo(
						new BigDecimal(449.9877).setScale(4,
										RoundingMode.HALF_EVEN)) == 0);
		assertTrue(accountTo.getBalance().equals(
						new BigDecimal(550.0123).setScale(4,
										RoundingMode.HALF_EVEN)));

	}

	/** 
	 * Test to perform account transfer.
	 * 
	 * @throws InterruptedException
	 * @throws CustomException
	 */
	@Test
	public void testAccountMultiThreadedTransfer() throws InterruptedException,
					CustomException
	{
		final AccountDAO accountDAO = DataDaoFactory.getAccountDAO();
		
		// Transfer a total of 200USD from 100USD balance in multi-threaded
		// mode, expect half of the transaction fail
		final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);
		
		for ( int i = 0; i < THREADS_COUNT; i++ )
		{
			new Thread(new Runnable()
			{
				// @Override
				public void run()
				{
					try
					{
						UserTransaction transaction = new UserTransaction(
										"USD",
										new BigDecimal(2).setScale(4,
														RoundingMode.HALF_EVEN),
										1L, 2L);
						accountDAO.transferAccountBalance(transaction);
					}
					catch (Exception e)
					{
						log.error("Error occurred during transfer ", e);
					}
					finally
					{
						latch.countDown();
					}
				}
			}).start();
		}

		latch.await();

		Account accountFrom = accountDAO.getAccountById(1);

		Account accountTo = accountDAO.getAccountById(2);

		log.debug("Account From: " + accountFrom);

		log.debug("Account From: " + accountTo);

		assertTrue(accountFrom.getBalance().equals(
						new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN)));
		assertTrue(accountTo.getBalance()
						.equals(new BigDecimal(300).setScale(4,
										RoundingMode.HALF_EVEN)));

	}

	/**
	 * Test for transfer on Data base failure.
	 * 
	 * @throws CustomException
	 * @throws SQLException
	 */
	@Test
	public void testTransferFailOnDBLock() throws CustomException, SQLException
	{
		final String SQL_LOCK_ACC = "SELECT * FROM Account WHERE AccountId = 5 FOR UPDATE";
		Connection conn = null;
		PreparedStatement lockStmt = null;
		ResultSet rs = null;
		Account fromAccount = null;

		try
		{
			conn = DataDAOFactory.getConnection();
			conn.setAutoCommit(false);
			
			// lock account for writing:
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC);
			rs = lockStmt.executeQuery();
			
			if (rs.next())
			{
				fromAccount = new Account(rs.getLong("AccountId"),
								rs.getString("UserName"),
								rs.getBigDecimal("Balance"),
								rs.getString("CurrencyCode"));
				
				if (log.isDebugEnabled())
				{
					log.debug("Locked Account: " + fromAccount);
				}
			}

			if (fromAccount == null)
			{
				throw new CustomException("Locking error during test, SQL = "
								+ SQL_LOCK_ACC);
			}
			// after lock account 5, try to transfer from account 6 to 5
			// default data timeout for acquire lock is 1sec
			BigDecimal transferAmount = new BigDecimal(50).setScale(4,
							RoundingMode.HALF_EVEN);

			UserTransaction transaction = new UserTransaction("GBP",
							transferAmount, 6L, 5L);
			DataDaoFactory.getAccountDAO().transferAccountBalance(transaction);
			
			conn.commit();
		}
		catch (Exception e)
		{
			log.error("Exception occurred, initiate a rollback");
			try
			{
				if (conn != null)
					conn.rollback();
			}
			catch (SQLException re)
			{
				log.error("Fail to rollback transaction", re);
			}
		}
		finally
		{
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
		}

		// now inspect account 3 and 4 to verify no transaction occurred
		BigDecimal originalBalance = new BigDecimal(500).setScale(4,
						RoundingMode.HALF_EVEN);
		
		BigDecimal bgLower5 = originalBalance.subtract(new BigDecimal(52));
		BigDecimal bgUpper5 = originalBalance.subtract(new BigDecimal(48));
		
		BigDecimal bgLower6 = originalBalance.add(new BigDecimal(48));
		BigDecimal bgUpper6 = originalBalance.add(new BigDecimal(52));
				
		assertTrue(valid(DataDaoFactory.getAccountDAO().getAccountById(5)
						.getBalance(), bgLower5, bgUpper5));
		assertTrue(valid(DataDaoFactory.getAccountDAO().getAccountById(6)
						.getBalance(), bgLower6, bgUpper6));
	}
	
	/**
	 * BigDecimal validation comparable test between bound lower and upper
	 * limits.
	 * @param input - value to be compared.
	 * @param low - lower bound limit
	 * @param high - upper bound limit.
	 * @return - boolean, true if within limt, false otherwise.
	 */
	public boolean valid(BigDecimal input, BigDecimal low, BigDecimal high) 
	{
        return(input.compareTo(low) >= 0 && input.compareTo(high) <= 0);
	}

}
