package com.account.dao;

/**
 * Data Access object factory.
 * 
 * @author mccormam
 */
public abstract class DAOFactory
{
    // DATA factory type enumeration.
	public static final int DATA = 1;

	/**
	 *  Retrieve the user Data Access Object.
	 *  
	 * @return - User Data Access Object.
	 */
	public abstract UserDAO getUserDAO();

	/**
	 *  Retrieve account Data Access Objects.
	 *  
	 * @return - Account Data Access Object.
	 */
	public abstract AccountDAO getAccountDAO();

	/**
	 *  Populate the data for testing.
	 */
	public abstract void populateTestData();

	/**
	 * Establish the Data Access Object factory
	 * 
	 * @param factoryCode - Data source index
	 * @returne - Data Access Object factory dource.
	 */
	public static DAOFactory getDAOFactory( int factoryCode )
	{
		// Check on the factory code enumeration, for source of data.
		switch (factoryCode)
		{
			// Case for DATA
			case DATA:
			{
				return new DataDAOFactory();
			}
			// Default Data Factory
			default:
			{
				// by default using Data in memory database
				return new DataDAOFactory();
			}
		}
	}
}
