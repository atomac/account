package com.account.dao;

import com.account.exception.CustomException;
import com.account.model.User;

import java.util.List;

/**
 * Interface for the user Data Access Object.
 * @author mccormam
 *
 */
public interface UserDAO
{

	/** 
	 * Retrieve all users
	 * 
	 * @return - list of all users in the user data table
	 * @throws CustomException
	 */
	List<User> getAllUsers() throws CustomException;

	/**
	 * Retrieve the user details by the User Id.
	 * 
	 * @param userId - user id
	 * @return - retrieved user details.
	 * @throws CustomException
	 */
	User getUserById( long userId ) throws CustomException;

	/**
	 * Retrieve the user details by the user name.
	 * 
	 * @param userName - user name
	 * @return - retrieved user details
	 * @throws CustomException
	 */
	User getUserByName( String userName ) throws CustomException;

	/**
	 * Insert the user detail into the user table.
	 * 
	 * @param user - user to be created
	 * @return userId generated from insertion. return -1 on error
	 */
	long insertUser( User user ) throws CustomException;

	/**
	 * Update the user table details.
	 * 
	 * @param userId - user id
	 * @param user - user data object
	 * @return - confirmation of user update.
	 * @throws CustomException
	 */
	int updateUser( Long userId, User user ) throws CustomException;

	/**
	 * Delete the user details from the user table.
	 * 
	 * @param userId - user id to delete.
	 * @return - confirmation of user delete.
	 * @throws CustomException
	 */
	int deleteUser( long userId ) throws CustomException;

}
