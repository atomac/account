package com.account.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Currency;

import org.apache.log4j.Logger;

/**
 * Utilities class to operate on money
 */
public enum MoneyUtil 
{
	INSTANCE;

	static Logger log = Logger.getLogger(MoneyUtil.class);

	// Zero amount with scale 4 and financial rounding mode
	public static final BigDecimal zeroAmount = new BigDecimal(0).setScale(4,
					RoundingMode.HALF_EVEN);

	/**
	 * @param inputCcyCode - String Currency code to be validated
	 * @return - true if currency code is valid ISO code, false otherwise
	 */
	public boolean validateCcyCode( String inputCcyCode )
	{
		// Validate currency code.
		try
		{
			Currency instance = Currency.getInstance(inputCcyCode);
			
			// If debug is enabled, output message to log
			if (log.isDebugEnabled())
			{
				log.debug("Validate Currency Code: " + instance.getSymbol());
			}
			
			// Return valid currency code.			
			return instance.getCurrencyCode().equals(inputCcyCode);
		}
		// Currency code failure exception.
		catch (Exception e)
		{
			log.warn("Cannot parse the input Currency Code, Validation Failed: ", e);
		}
		
		return false;
	}

}
