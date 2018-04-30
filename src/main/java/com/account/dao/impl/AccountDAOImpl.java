package com.account.dao.impl;

import com.account.dao.AccountDAO;
import com.account.dao.DataDAOFactory;
import com.account.exception.CustomException;
import com.account.model.Account;
import com.account.model.MoneyUtil;
import com.account.model.UserTransaction;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Account Data Acces Object.
 * 
 * @author mccormam
 */
@Repository
public class AccountDAOImpl implements AccountDAO
{
	private static Logger log = Logger.getLogger(AccountDAOImpl.class);
	
	// SQL Statements
	private final static String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? ";
	private final static String SQL_LOCK_ACC_BY_ID = "SELECT * FROM Account WHERE AccountId = ? FOR UPDATE";
	private final static String SQL_CREATE_ACC = "INSERT INTO Account (UserName, Balance, CurrencyCode) VALUES (?, ?, ?)";
	private final static String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET Balance = ? WHERE AccountId = ? ";
	private final static String SQL_GET_ALL_ACC = "SELECT * FROM Account";
	private final static String SQL_DELETE_ACC_BY_ID = "DELETE FROM Account WHERE AccountId = ?";

	/**
	 * Retrieve all the accounts from the ACCOUNTS data table.
	 */
	public List<Account> getAllAccounts() throws CustomException
	{
		// Initialise connection parameters.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<Account> allAccounts = new ArrayList<Account>();
		
		// Connect to database and execute the query.
		try
		{
			conn = DataDAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_ALL_ACC);
			rs = stmt.executeQuery();
			
			// Process the retrieved data.
			while (rs.next())
			{
				Account acc = new Account(rs.getLong("AccountId"),
								rs.getString("UserName"),
								rs.getBigDecimal("Balance"),
								rs.getString("CurrencyCode"));
				
				// Output debug if enabled.
				if (log.isDebugEnabled())
				{
					log.debug("getAllAccounts(): Get  Account " + acc);
				}
				
				// Add account details to account list
				allAccounts.add(acc);
			}
			
		    return allAccounts;
		}
		// Catch SQL Exception
		catch (SQLException e)
		{
			throw new CustomException(
							"getAccountById(): Error reading account data", e);
		}
		// Finally close the database.
		finally
		{
			DbUtils.closeQuietly(conn, stmt, rs);
		}
	}

	/**
	 * Retrieve the account details by id
	 */
	public Account getAccountById( long accountId ) throws CustomException
	{
		// Initialise connection parameters.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		Account acc = null;
		
		// Connect to database and execute the sql statement.
		try
		{
			conn = DataDAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_ACC_BY_ID);
			stmt.setLong(1, accountId);
			rs = stmt.executeQuery();
			
			// Process the retrieved data.
			if (rs.next())
			{
				acc = new Account(rs.getLong("AccountId"),
								rs.getString("UserName"),
								rs.getBigDecimal("Balance"),
								rs.getString("CurrencyCode"));
				
				// Output message is debug is enabled
				if (log.isDebugEnabled())
				{
					log.debug("Retrieve Account By Id: " + acc);
				}
			}
			
			return acc;
		}
		// Catch SQL Exception
		catch (SQLException e)
		{
			throw new CustomException(
							"getAccountById(): Error reading account data", e);
		}
		// Finally close the database.
		finally
		{
			DbUtils.closeQuietly(conn, stmt, rs);
		}

	}

	/**
	 * Create account
	 */
	public long createAccount( Account account ) throws CustomException
	{
		// Initialise connection parameters.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet generatedKeys = null;
		
		// Connect to database. Set the values for the account creation.
		// Execute the sql statement.
		try
		{
			conn = DataDAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_CREATE_ACC);
			
			stmt.setString(1, account.getUserName());
			stmt.setBigDecimal(2, account.getBalance());
			stmt.setString(3, account.getCurrencyCode());
			
			int affectedRows = stmt.executeUpdate();
			
			// Check the number of rows created.
			if (affectedRows == 0)
			{
				log.error("createAccount(): Creating account failed, no rows affected.");
				
				throw new CustomException("Account Cannot be created");
			}
			
			generatedKeys = stmt.getGeneratedKeys();
			
			// Check key created.
			if (generatedKeys.next())
			{
				return generatedKeys.getLong(1);
			}
			// Otherwise, account creation failed.
			else
			{
				log.error("Creating account failed, no ID obtained.");
				
				throw new CustomException("Account Cannot be created");
			}
		}
		// Catch SQL Exception
		catch (SQLException e)
		{
			log.error("Error Inserting Account  " + account);
			throw new CustomException(
							"createAccount(): Error creating user account "
											+ account, e);
		}
		// Finally, close database.
		finally
		{
			DbUtils.closeQuietly(conn, stmt, generatedKeys);
		}
	}

	/**
	 * Delete account by id
	 */
	public int deleteAccountById( long accountId ) throws CustomException
	{
		// Initialise connection parameters.
		Connection conn = null;
		PreparedStatement stmt = null;
		
		// Connect to the database and execute the update.
		try
		{
			conn = DataDAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_DELETE_ACC_BY_ID);
			stmt.setLong(1, accountId);
			
			int returnDelete = stmt.executeUpdate();
			
			// Commit change.
			conn.commit();
			
			return returnDelete;
		}
		// Catch SQL Exception.
		catch (SQLException e)
		{
			throw new CustomException(
							"deleteAccountById(): Error deleting user account Id "
											+ accountId, e);
		}
		finally
		{
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(stmt);
		}
	}

	/**
	 * Update account balance
	 */
	public int updateAccountBalance( long accountId, BigDecimal deltaAmount )
					throws CustomException
	{
		// Initialise connection parameters. 
		Connection conn = null;
		PreparedStatement lockStmt = null;
		PreparedStatement updateStmt = null;
		
		ResultSet rs = null;
		Account targetAccount = null;
		
		int updateCount = -1;
		
		// Connect to database and execute.
		try
		{
			conn = DataDAOFactory.getConnection();
			conn.setAutoCommit(false);
			
			// lock account for writing:
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, accountId);
			
			rs = lockStmt.executeQuery();
			
			// Process query result.
			if (rs.next())
			{
				targetAccount = new Account(rs.getLong("AccountId"),
								rs.getString("UserName"),
								rs.getBigDecimal("Balance"),
								rs.getString("CurrencyCode"));
				
				// If debug is enabled, output log message.
				if (log.isDebugEnabled())
				{
					log.debug("updateAccountBalance from Account: "
									+ targetAccount);
				}
			}

			// If the returned account is null, unable to process.
			if (targetAccount == null)
			{
				throw new CustomException(
								"updateAccountBalance(): fail to lock account : "
												+ accountId);
			}
			
			// update account upon success locking
			BigDecimal balance = targetAccount.getBalance().add(deltaAmount);
			
			// If the balance is in debt, can not process
			if (balance.compareTo(MoneyUtil.zeroAmount) < 0)
			{
				throw new CustomException("Not sufficient Fund for account: "
								+ accountId);
			}

			// Prepare the update statement and execute.
			updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
			updateStmt.setBigDecimal(1, balance);
			updateStmt.setLong(2, accountId);
			updateCount = updateStmt.executeUpdate();
			
			conn.commit();
			
			// If debug is enabled, output log message.
			if (log.isDebugEnabled())
			{
				log.debug("New Balance after Update: " + targetAccount);
			}
			
			return updateCount;
		}
		// SQL Exception
		catch (SQLException se)
		{
			// rollback transaction if exception occurs
			log.error("updateAccountBalance(): User Transaction Failed, rollback initiated for: "
							+ accountId, se);
			
			// If connection is not null, rollback commit if error.
			try
			{
				// Connection is not null, rollback commit.
				if (conn != null)
				{
					conn.rollback();
				}
			}
			// Catch SQL Exception
			catch (SQLException re)
			{
				throw new CustomException("Fail to rollback transaction", re);
			}
		}
		// Perform following regardless.
		finally
		{
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
			DbUtils.closeQuietly(updateStmt);
		}
		
		return updateCount;
	}

	/**
	 * Transfer balance between two accounts.
	 */
	@SuppressWarnings( "resource" )
	public int transferAccountBalance( UserTransaction userTransaction )
					throws CustomException
	{
		// Initialise connection parameters.

		Connection conn = null;
		PreparedStatement lockStmt = null;
		PreparedStatement updateStmt = null;
		
		ResultSet rs = null;
		Account fromAccount = null;
		Account toAccount = null;
		
		int result = -1;

		// Connext to DAO Factory source.
		try
		{
			conn = DataDAOFactory.getConnection();
			conn.setAutoCommit(false);
			
			// lock the credit and debit account for writing:
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, userTransaction.getFromAccountId());
			
			rs = lockStmt.executeQuery();
			
			// Process returned resultset. Set fromAccount details.
			if (rs.next())
			{
				fromAccount = new Account(rs.getLong("AccountId"),
								rs.getString("UserName"),
								rs.getBigDecimal("Balance"),
								rs.getString("CurrencyCode"));
				
				// If debug is enabled, output log message.
				if (log.isDebugEnabled())
				{
					log.debug("transferAccountBalance from Account: "
									+ fromAccount);
				}
			}
			
			// Prepare SQL Statement and execute.
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, userTransaction.getToAccountId());
			
			rs = lockStmt.executeQuery();
			
			// Process returned result set.
			if (rs.next())
			{
				toAccount = new Account(rs.getLong("AccountId"),
								rs.getString("UserName"),
								rs.getBigDecimal("Balance"),
								rs.getString("CurrencyCode"));
				
				// If debug is enabled, output log message.
				if (log.isDebugEnabled())
				{
					log.debug("transferAccountBalance to Account: " + toAccount);
				}
				
			}

			// Check locking status
			if ((fromAccount == null) || (toAccount == null))
			{
				throw new CustomException(
								"Fail to lock both accounts for write");
			}

			// check transaction currency
			if (!fromAccount.getCurrencyCode().equals(
							userTransaction.getCurrencyCode()))
			{
				throw new CustomException(
								"Fail to transfer Fund, transaction ccy are different from source/destination");
			}

			// Check currency code is the same for both accounts
			if (!fromAccount.getCurrencyCode().equals(
							toAccount.getCurrencyCode()))
			{
				throw new CustomException(
								"Fail to transfer Fund, the source and destination account are in different currency");
			}

			// check enough fund in source account
			BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(
							userTransaction.getAmount());
			
			// Ensure the the "fromAccount" has sufficient funds.
			if (fromAccountLeftOver.compareTo(MoneyUtil.zeroAmount) < 0)
			{
				throw new CustomException(
								"Not enough Fund from source Account ");
			}
			
			// Proceed with update
			updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
			updateStmt.setBigDecimal(1, fromAccountLeftOver);
			updateStmt.setLong(2, userTransaction.getFromAccountId());
			updateStmt.addBatch();
			updateStmt.setBigDecimal(
							1,
							toAccount.getBalance().add(
											userTransaction.getAmount()));
			updateStmt.setLong(2, userTransaction.getToAccountId());
			updateStmt.addBatch();
			
			int[] rowsUpdated = updateStmt.executeBatch();
			result = rowsUpdated[0] + rowsUpdated[1];
			
			// If debug is enabled, output log message.
			if (log.isDebugEnabled())
			{
				log.debug("Number of rows updated for the transfer : " + result);
			}
			
			// If there is no error, commit the transaction
			conn.commit();
			
			// Close instantiations.
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
			DbUtils.closeQuietly(updateStmt);
		}
		// SQL Exception.
		catch (SQLException se)
		{

			log.error("transferAccountBalance(): User Transaction Failed, rollback initiated for: "
							+ userTransaction, se);
			
			// Rollback transaction if exception occurs
			try
			{
				// If connection is not null, perform rollack.
				if (conn != null)
				{
					conn.rollback();
				}
			}
			catch (SQLException re)
			{
				throw new CustomException("Fail to rollback transaction", re);
			}
		}
		// Execute regardless.
		finally
		{
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
			DbUtils.closeQuietly(updateStmt);
		}
		
		return result;
	}

}
