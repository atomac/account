package com.account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Object for User details.
 * @author mccormam
 *
 */
public class User
{

	// User Id.
	@JsonIgnore
	private long userId;

	// User Name.
	@JsonProperty( required = true )
	private String userName;

	// Email address.
	@JsonProperty( required = true )
	private String emailAddress;

	/**
	 *  User Constructor.
	 */
	public User()
	{
	}

	/**
	 * User constructor with user details.
	 * 
	 * @param userName - user name.
	 * @param emailAddress - email address.
	 */
	public User( String userName, String emailAddress )
	{
		this.userName = userName;
		this.emailAddress = emailAddress;
	}

	/**
	 *  User constructor with user details.
	 *  
	 * @param userId - user id.
	 * @param userName - user name.
	 * @param emailAddress - email address.
	 */
	public User( long userId, String userName, String emailAddress )
	{
		this.userId = userId;
		this.userName = userName;
		this.emailAddress = emailAddress;
	}

	/**
	 * Retrieve user id.
	 * 
	 * @return - user id.
	 */
	public long getUserId()
	{
		return userId;
	}

	/**
	 * Retrieve user name.
	 * 
	 * @return - user name.
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Retrieve user email address.
	 * 
	 * @return - email address.
	 */
	public String getEmailAddress()
	{
		return emailAddress;
	}

	/**
	 * Override the meaning of equals in comparing user details.
	 */
	@Override
	public boolean equals( Object o )
	{
		// If there is an equivalence of this static object, objects are equal.
		if (this == o)
		{
			return true;
		}
		
		// If object is null or not of the same class, return false.
		if ((o == null) || (getClass() != o.getClass()))
		{
			return false;
		}

		// Instantiate the account object.
		User user = (User) o;

		// If the user ids do not match, return false.
		if (userId != user.userId)
		{
			return false;
		}
		
		// If the user name do not match, return false.
		if (!userName.equals(user.userName))
		{
			return false;
		}

		// If the email address do not match, return false.
		if (!emailAddress.equals(user.emailAddress))
		{
			return false;
		}
		
		// User details are equal, return true.
		return true;

	}

	/**
	 * Override the hash for the user object details.
	 */
	@Override
	public int hashCode()
	{
		int result = (int) (userId ^ (userId >>> 32));
		
		result = ((31*result) + userName.hashCode());
		result = ((31*result) + emailAddress.hashCode());
		
		return result;
	}

	/**
	 * Print user details.
	 */
	@Override
	public String toString()
	{
		return "User{" + "userId=" + userId + ", userName='" + userName + '\''
						+ ", emailAddress='" + emailAddress + '\'' + '}';
	}
}
