package com.account.dao;

import com.account.dao.impl.AccountDAOImpl;
import com.account.dao.impl.UserDAOImpl;
import com.account.utils.Utils;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.h2.tools.RunScript;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Data DAO
 * 
 * @author mccormam
 */
public class DataDAOFactory extends DAOFactory
{
	private static Logger log = Logger.getLogger(DataDAOFactory.class);
	
	// Key-Value from application.properties.
	private static final String DATA_DRIVER = "data_driver";
	private static final String DATA_CONN_URL = "data_connection_url";
	private static final String DATA_USER = "data_user";
	private static final String DATA_PASSWORD = "data_password";
	
	// Obtain the values from the application.properties file.
	private static final String data_driver = Utils.getStringProperty(DATA_DRIVER);
	private static final String data_connection_url = Utils	.getStringProperty(DATA_CONN_URL);
	private static final String data_user = Utils.getStringProperty(DATA_USER);
	private static final String data_password = Utils.getStringProperty(DATA_PASSWORD);

	// Declare constructors.
	private final UserDAOImpl userDAO = new UserDAOImpl();
	private final AccountDAOImpl accountDAO = new AccountDAOImpl();

	/**
	 *  Data DAO constructor for the data driver
	 */
	DataDAOFactory()
	{
		// init: load driver
		DbUtils.loadDriver(data_driver);
	}

	
	/**
	 * Connect to the data base for account details. 
	 */
	public static Connection getConnection() throws SQLException
	{
		return DriverManager.getConnection(data_connection_url, data_user,
						data_password);
	}

	
	/**
	 * Obtain the user Data Access Object.
	 */
	public UserDAO getUserDAO()
	{
		return userDAO;
	}

	
	/**
	 *  Obtain the Account Data Access Object
	 */
	public AccountDAO getAccountDAO()
	{
		return accountDAO;
	}

	
	/**
	 * Execute the process to populate the account and user test data. 
	 */
	@Override
	public void populateTestData()
	{
		log.info("Populating Test User Table and data ..... ");
		
		Connection conn = null;
		
		// Connect to data base and read data source file
		try
		{
			conn = DataDAOFactory.getConnection();
			RunScript.execute(conn, new FileReader(
							"src/test/resources/demo.sql"));
		}
		// Catch SQL Exception
		catch (SQLException e)
		{
			log.error("populateTestData(): Error populating user data: ", e);
			
			throw new RuntimeException(e);
		}
		// Catch File Not Found Exception.
		catch (FileNotFoundException e)
		{
			log.error("populateTestData(): Error finding test script file ", e);
			
			throw new RuntimeException(e);
		}
		// Close the Database connection.
		finally
		{
			DbUtils.closeQuietly(conn);
		}
	}

}
