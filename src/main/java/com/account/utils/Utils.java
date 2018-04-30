package com.account.utils;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils
{
	private static Logger log = Logger.getLogger(Utils.class);
	
    private static Properties properties = new Properties();
	
	/**
	 * Load configuration details from properties file.
	 * 
	 * @param fileName - properties file name.
	 */
	public static void loadConfig( String fileName )
	{
		// If the file name is null, log warning.
		if (fileName == null)
		{
			log.warn("loadConfig: config file name cannot be null");
		}
		// Otherwise properties file exists.
		else
		{
			// Load the properties file
			try
			{
				log.info("loadConfig(): Loading config file: " + fileName);
				
				final InputStream fis = Thread.currentThread()
								.getContextClassLoader()
								.getResourceAsStream(fileName);
				
				properties.load(fis);

			}
			// Catch exception if file not found.
			catch (FileNotFoundException fne)
			{
				log.error("loadConfig(): file name not found " + fileName, fne);
			}
			// Catch IO Exception.
			catch (IOException ioe)
			{
				log.error("loadConfig(): error when reading the config "
								+ fileName, ioe);
			}
		}

	}

	/**
	 * Retrieve the value from the properties file according to the key.
	 * 
	 * @param key - property key.
	 * @return - value.
	 */
	public static String getStringProperty( String key )
	{
		// Retrieve the value by key
		String value = properties.getProperty(key);
		
		// If the value is null, obtain the system value.
		if (value == null)
		{
			value = System.getProperty(key);
		}
		
		return value;
	}

	/**
	 * Retrieve the value from the properties file according to the key.
	 * If value is null, set to the default value.
	 *  
	 * @param key - property key
	 * @param defaultVal - default value if the key not present in config file
	 * @return - string property based on lookup key
	 */
	public static String getStringProperty( String key, String defaultVal )
	{
		// Retrieve the property value by key.
		String value = getStringProperty(key);
		
		// If returned value is null, set return to default.
		if (value == null)
		{
			value = defaultVal;
		}
		
		return value;
	}

	/**
	 * Retrieve the value from the properties file according to the key.
	 * If value is null, set to the default value.
	 *  
	 * @param key - property key
	 * @param defaultVal - default value if the key not present in config file
	 * @return - string property based on lookup key
	 */
	public static int getIntegerProperty( String key, int defaultVal )
	{
		// Retrieve the value according to the key.
		String valueStr = getStringProperty(key);
		
		// If the property value is null, return the default value.
		if (valueStr == null)
		{
			return defaultVal;
		}
		// Otherwise if not null, cast to integer.
		else
		{
			// Parse value to an integer.
			try
			{
				return Integer.parseInt(valueStr);
			}
			// Catch numeric parse exception.
			catch (Exception e)
			{
				log.warn("getIntegerProperty(): cannot parse integer from properties file for: "
								+ key
								+ "fail over to default value: "
								+ defaultVal, e);
				
				return defaultVal;
			}
		}
	}

	/**
	 * Initialise the configuration file for keyed access.
	 */
	static
	{
		// Determine the configuration file.
		String configFileName = System.getProperty("application.properties");

		// If the configuration is null, set the default.
		if (configFileName == null)
		{
			configFileName = "application.properties";
		}
		
		// Load the configuration file for paired key-values.
		loadConfig(configFileName);
	}

}
