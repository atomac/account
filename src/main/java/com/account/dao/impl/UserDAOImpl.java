package com.account.dao.impl;

import com.account.dao.DataDAOFactory;
import com.account.dao.UserDAO;
import com.account.exception.CustomException;
import com.account.model.User;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the User Data Acces Object.
 * 
 * @author mccormam
 */
@Repository
public class UserDAOImpl implements UserDAO
{
	private static Logger log = Logger.getLogger(UserDAOImpl.class);
	
	private final static String SQL_GET_USER_BY_ID = "SELECT * FROM User WHERE UserId = ? ";
	private final static String SQL_GET_ALL_USERS = "SELECT * FROM User";
	private final static String SQL_GET_USER_BY_NAME = "SELECT * FROM User WHERE UserName = ? ";
	private final static String SQL_INSERT_USER = "INSERT INTO User (UserName, EmailAddress) VALUES (?, ?)";
	private final static String SQL_UPDATE_USER = "UPDATE User SET UserName = ?, EmailAddress = ? WHERE UserId = ? ";
	private final static String SQL_DELETE_USER_BY_ID = "DELETE FROM User WHERE UserId = ? ";

	/**
	 * Find all users
	 */
	public List<User> getAllUsers() throws CustomException
	{
		// Initialise connection parameters.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<User> users = new ArrayList<User>();
		
		// Connect to database and establish SQL statement. Execute SQL.
		try
		{
			conn = DataDAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_ALL_USERS);
			
			rs = stmt.executeQuery();
			
			// Process returned result set.
			while (rs.next())
			{
				User u = new User(rs.getLong("UserId"),
								rs.getString("UserName"),
								rs.getString("EmailAddress"));
				
				// Add details to list
				users.add(u);
				
				// Output debug message if enabled.
				if (log.isDebugEnabled())
				{
					log.debug("getAllUsers() Retrieve User: " + u);
				}
			}
			
			return users;
		}
		// SQL Exception.
		catch (SQLException e)
		{
			throw new CustomException("Error reading user data", e);
		}
		// Execute regardless.
		finally
		{
			DbUtils.closeQuietly(conn, stmt, rs);
		}
		
	}

	/**
	 * Find user by userId
	 */
	public User getUserById( long userId ) throws CustomException
	{
		// Initialise connection parameters.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		User u = null;
		
		// Connect to database and establish SQL statement. Execute SQL.
		try
		{
			conn = DataDAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_USER_BY_ID);
			stmt.setLong(1, userId);
			
			rs = stmt.executeQuery();
			
			// Process returned result set
			if (rs.next())
			{
				u = new User(rs.getLong("UserId"), rs.getString("UserName"),
								rs.getString("EmailAddress"));
				
				// Output debug message if enabled.
				if (log.isDebugEnabled())
				{
					log.debug("getUserById(): Retrieve User: " + u);
				}
			}
			
			return u;
		}
		// SQL Exception.
		catch (SQLException e)
		{
			throw new CustomException("Error reading user data", e);
		}
		// Execute regardless.
		finally
		{
			DbUtils.closeQuietly(conn, stmt, rs);
		}
	}

	/**
	 * Find user by userName
	 */
	public User getUserByName( String userName ) throws CustomException
	{
		// Initialise connection parameters.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		User u = null;
		
		// Connect to database and execute statement.
		try
		{
			conn = DataDAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_USER_BY_NAME);
			stmt.setString(1, userName);
			
			rs = stmt.executeQuery();
			
			// Process returned result set.
			if (rs.next())
			{
				u = new User(rs.getLong("UserId"), rs.getString("UserName"),
								rs.getString("EmailAddress"));
				
				// Output debug message if enabled.
				if (log.isDebugEnabled())
				{
					log.debug("Retrieve User: " + u);
				}
				
			}
			
			return u;
		}
		// SQL Exception.
		catch (SQLException e)
		{
			throw new CustomException("Error reading user data", e);
		}
		// Execute regardless.
		finally
		{
			DbUtils.closeQuietly(conn, stmt, rs);
		}
	}

	/**
	 * Save User
	 */
	public long insertUser( User user ) throws CustomException
	{
		// Initialise connection parameters.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet generatedKeys = null;
		
		// Connect to database and establish SQL statement. Execute SQL.
		try
		{
			conn = DataDAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_INSERT_USER,
							Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, user.getUserName());
			stmt.setString(2, user.getEmailAddress());
			
			// Retrieve the number of affectes rows.
			int affectedRows = stmt.executeUpdate();
			
			// If the number of rows is zero, have error.
			if (affectedRows == 0)
			{
				log.error("insertUser(): Creating user failed, no rows affected."
								+ user);
				
				throw new CustomException("Users Cannot be created");
			}
			
			// Generate the statement key.
			generatedKeys = stmt.getGeneratedKeys();
			
			// Return key for creating new user.
			if (generatedKeys.next())
			{
				return generatedKeys.getLong(1);
			}
			// Otherwise, no key, log error message.
			else
			{
				log.error("insertUser():  Creating user failed, no ID obtained."
								+ user);
				
				throw new CustomException("Users Cannot be created");
			}
		}
		// SQL Exception.
		catch (SQLException e)
		{
			log.error("Error Inserting User :" + user);
			
			throw new CustomException("Error creating user data", e);
		}
		// Execute regardless.
		finally
		{
			DbUtils.closeQuietly(conn, stmt, generatedKeys);
		}

	}

	/**
	 * Update User
	 */
	public int updateUser( Long userId, User user ) throws CustomException
	{
		// Initialise connection parameters.
		Connection conn = null;
		PreparedStatement stmt = null;

		// Connect to database and establish SQL statement. Execute SQL.
		try
		{
			conn = DataDAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_UPDATE_USER);
			
			stmt.setString(1, user.getUserName());
			stmt.setString(2, user.getEmailAddress());
			stmt.setLong(3, userId);
			
			int returnUpdate = stmt.executeUpdate();
			
			// Commit change.
			conn.commit();
			
			return returnUpdate;
		}
		// SQL Exception.
		catch (SQLException e)
		{
			log.error("Error Updating User :" + user);
			
			throw new CustomException("Error update user data", e);
		}
		// Execute regardless.
		finally
		{
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(stmt);
		}
	}

	/**
	 * Delete User
	 */
	public int deleteUser( long userId ) throws CustomException
	{
		// Initialise connection parameters.
		Connection conn = null;
		PreparedStatement stmt = null;

		// Connect to database and establish SQL statement. Execute SQL.
		try
		{
			conn = DataDAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_DELETE_USER_BY_ID);
			stmt.setLong(1, userId);
			
			int returnDelete = stmt.executeUpdate();
			
			// Commit change.
			conn.commit();
			
			return returnDelete;
		}
		// SQL Exception.
		catch (SQLException e)
		{
			log.error("Error Deleting User :" + userId);
			
			throw new CustomException("Error Deleting User ID:" + userId, e);
		}
		// Execute regardless.
		finally
		{
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(stmt);
		}
	}

}
