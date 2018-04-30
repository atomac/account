package com.account.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class UserTransaction
{

	// Currency Code
	@JsonProperty( required = true )
	private String currencyCode;

	// Account amount.
	@JsonProperty( required = true )
	private BigDecimal amount;

	// From account id.
	@JsonProperty( required = true )
	private Long fromAccountId;

	// To account id.
	@JsonProperty( required = true )
	private Long toAccountId;

	/**
	 * Transaction constructor.
	 */
	public UserTransaction()
	{
	}

	/**
	 * Transaction constructor with details.
	 * 
	 * @param currencyCode - currency code.
	 * @param amount - amount
	 * @param fromAccountId - from account id.
	 * @param toAccountId - to account id.
	 */
	public UserTransaction( String currencyCode, BigDecimal amount,
					Long fromAccountId, Long toAccountId )
	{
		this.currencyCode = currencyCode;
		this.amount = amount;
		this.fromAccountId = fromAccountId;
		this.toAccountId = toAccountId;
	}

	/**
	 * Retrieve currency code.
	 * 
	 * @return - currency code.
	 */
	public String getCurrencyCode()
	{
		return currencyCode;
	}

	/**
	 * Retrieve account amount.
	 * 
	 * @return - amount
	 */
	public BigDecimal getAmount()
	{
		return amount;
	}

	/**
	 * Retrieve from account id.
	 * 
	 * @return - from account id.
	 */
	public Long getFromAccountId()
	{
		return fromAccountId;
	}

	/**
	 * Retrieve to account id.
	 * 
	 * @return - to account id.
	 */
	public Long getToAccountId()
	{
		return toAccountId;
	}

	/**
	 * Override the meaning of equals in comparing transaction details.
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

		// Instantiate the user transaction object.
		UserTransaction that = (UserTransaction) o;

		// If the currency codes do not match, return false.
		if (!currencyCode.equals(that.currencyCode))
		{
			return false;
		}
		
		// If the amounts do not match, return false.
		if (!amount.equals(that.amount))
		{
			return false;
		}
		
		// If the from account ids do not match, return false.
		if (!fromAccountId.equals(that.fromAccountId))
		{
			return false;
		}
		
		// If the to account ids do not match, return false.
		if (!toAccountId.equals(that.toAccountId))
		{
			return false;
		}		
		
		return true;
	}

	/**
	 * Override the hash for the transaction object details.
	 */
	@Override
	public int hashCode()
	{
		int result = currencyCode.hashCode();
		
		result = ((31*result) + amount.hashCode());
		result = ((31*result) + fromAccountId.hashCode());
		result = ((31*result) + toAccountId.hashCode());
		
		return result;
	}

	/**
	 * Print user transaction details.
	 */
	@Override
	public String toString()
	{
		return "UserTransaction{" + "currencyCode='" + currencyCode + '\''
						+ ", amount=" + amount + ", fromAccountId="
						+ fromAccountId + ", toAccountId=" + toAccountId + '}';
	}

}
