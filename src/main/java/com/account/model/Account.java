package com.account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * 
 * Data objects and database columns for the account object.
 * 
 * @author mccormam
 */
public class Account
{

	// Account Id.
	@JsonIgnore
	private long accountId;

	// Account user name
	@JsonProperty( required = true )
	private String userName;

	// Account balance.
	@JsonProperty( required = true )
	private BigDecimal balance;

	// Account currency code.
	@JsonProperty( required = true )
	private String currencyCode;

	/**
	 * Account constructor.
	 */
	public Account()
	{
	}

	/**
	 * Account object constructor for details.
	 * 
	 * @param userName - account user name
	 * @param balance - account balance.
	 * @param currencyCode - account currency code
	 */
	public Account( String userName, BigDecimal balance, String currencyCode )
	{
		this.userName = userName;
		this.balance = balance;
		this.currencyCode = currencyCode;
	}

	/**
	 * Account object constructor for details.
	 * 
	 * @param accountId - account id
	 * @param userName - account user name.
	 * @param balance - account balance.
	 * @param currencyCode - account currency code.
	 */
	public Account( long accountId, String userName, BigDecimal balance,
					String currencyCode )
	{
		this.accountId = accountId;
		this.userName = userName;
		this.balance = balance;
		this.currencyCode = currencyCode;
	}

	/**
	 * Retrieve account is
	 * 
	 * @return - account id.
	 */
	public long getAccountId()
	{
		return accountId;
	}

	/**
	 * Retrieve account user name.
	 * 
	 * @return - account user name.
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Retrieve account balance.
	 * 
	 * @return - account balance.
	 */
	public BigDecimal getBalance()
	{
		return balance;
	}
	
	/**
	 * Retrieve the account currency code.
	 * 
	 * @return - currency code.
	 */
	public String getCurrencyCode()
	{
		return currencyCode;
	}
	
	/**
	 * Override the meaning of equals in comparing account details.
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
		Account account = (Account) o;

		// If the account ids do not match, return false.
		if (accountId != account.accountId)
		{
			return false;
		}
		
		// If the user names are not equal, return false.
		if (!userName.equals(account.userName))
		{
			return false;
		}
		
		// If the balances are not equal, return false.
		if (!balance.equals(account.balance))
		{
			return false;
		}
		
		// If the balances are not equal, return false.
		if (!currencyCode.equals(account.currencyCode))
		{
			return false;
		}
		
		// Account details are equal, return true.
		return true;
	}

	/**
	 * Override the hash for the account object details.
	 */
	@Override
	public int hashCode()
	{
		int result = (int) (accountId ^ (accountId >>> 32));
		
		result = ((31*result) + userName.hashCode());
		result = ((31*result) + balance.hashCode());
		result = ((31*result) + currencyCode.hashCode());
		
		return result;
	}

	/**
	 * Print account details.
	 */
	@Override
	public String toString()
	{
		return "Account{" + "accountId=" + accountId + ", userName='"
						+ userName + '\'' + ", balance=" + balance
						+ ", currencyCode='" + currencyCode + '\'' + '}';
	}
}
