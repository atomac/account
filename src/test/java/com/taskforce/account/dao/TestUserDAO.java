package com.taskforce.account.dao;

import com.account.dao.DAOFactory;
import com.account.exception.CustomException;
import com.account.model.User;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Test suite for user Data Access Object functionality.
 * 
 * @author mccormam
 */
public class TestUserDAO
{
	private static Logger log = Logger.getLogger(TestUserDAO.class);

	private static final DAOFactory DataDAOFactory = DAOFactory
					.getDAOFactory(DAOFactory.DATA);

	@BeforeClass
	public static void setup()
	{
		// prepare test database and test data by executing sql script demo.sql
		log.debug("setting up test database and sample data....");
		DataDAOFactory.populateTestData();
	}

	@After
	public void tearDown()
	{

	}

	/** 
	 * Test to retrieve all users in the database.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testGetAllUsers() throws CustomException
	{
		List<User> allUsers = DataDAOFactory.getUserDAO().getAllUsers();
		
		assertTrue(allUsers.size() > 1);
	}

	/**
	 * Test to retrieve a user record by user id.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testGetUserById() throws CustomException
	{
		User u = DataDAOFactory.getUserDAO().getUserById(2L);

		assertTrue(u.getUserName().equals("george"));
	}

	/**
	 * Test to retrieve a non-exiting record by id. Failure expected.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testGetNonExistingUserById() throws CustomException
	{
		User u = DataDAOFactory.getUserDAO().getUserById(500L);
		
		assertTrue(u == null);
	}

	/**
	 * Test to retrieve a non-exiting user record by name - failure expected.
	 * @throws CustomException
	 */
	@Test
	public void testGetNonExistingUserByName() throws CustomException
	{
		User u = DataDAOFactory.getUserDAO().getUserByName("abcdeftg");
		
		assertTrue(u == null);
	}

	/**
	 * Test to create a new user record on the user table.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testCreateUser() throws CustomException
	{
		User u = new User("bertrande", "bertrande@gmail.com");
		
		long id = DataDAOFactory.getUserDAO().insertUser(u);
		
		User uAfterInsert = DataDAOFactory.getUserDAO().getUserById(id);
		
		assertTrue(uAfterInsert.getUserName().equals("bertrande"));
		assertTrue(u.getEmailAddress().equals("bertrande@gmail.com"));
	}

	/**
	 * Test to update existing user record.
	 * @throws CustomException
	 */
	@Test
	public void testUpdateUser() throws CustomException
	{
		User u = new User(1L, "test2", "test2@gmail.com");

		int rowCount = DataDAOFactory.getUserDAO().updateUser(1L, u);

		// assert one row(user) updated
		assertTrue(rowCount == 1);
		assertTrue(DataDAOFactory.getUserDAO().getUserById(1L)
						.getEmailAddress().equals("test2@gmail.com"));
	}

	/**
	 * Test to update non-exiting user - failure expected.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testUpdateNonExistingUser() throws CustomException
	{
		User u = new User(500L, "test2", "test2@gmail.com");
		
		int rowCount = DataDAOFactory.getUserDAO().updateUser(500L, u);
		
		// assert one row(user) updated
		assertTrue(rowCount == 0);
	}

	/**
	 * Test to delete user record from user table.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testDeleteUser() throws CustomException
	{
		int rowCount = DataDAOFactory.getUserDAO().deleteUser(1L);
		
		// assert one row(user) deleted
		assertTrue(rowCount == 1);
		
		// assert user no longer there
		assertTrue(DataDAOFactory.getUserDAO().getUserById(1L) == null);
	}

	/**
	 * Test to delete non-exiting user record.
	 * 
	 * @throws CustomException
	 */
	@Test
	public void testDeleteNonExistingUser() throws CustomException
	{
		int rowCount = DataDAOFactory.getUserDAO().deleteUser(500L);
		
		// assert no row(user) deleted
		assertTrue(rowCount == 0);

	}

}
