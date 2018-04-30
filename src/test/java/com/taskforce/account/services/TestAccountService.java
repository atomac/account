package com.taskforce.account.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.account.model.Account;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

/**
 * Integration testing for RestAPI Test data are initialised from
 * src/test/resources/demo.sql
 */

public class TestAccountService extends TestService
{

	/**
	 * Test to retrieve user account by user name and return 200 OK
	 */
	@Test
	public void testGetAccountByUserName() throws IOException,
					URISyntaxException
	{
		URI uri = builder.setPath("/account/1").build();
		HttpGet request = new HttpGet(uri);
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();

		assertTrue(statusCode == 200);

		// check the content
		String jsonString = EntityUtils.toString(response.getEntity());
		Account account = mapper.readValue(jsonString, Account.class);

		assertTrue(account.getUserName().equals("frederick"));
	}

	/**
	 * Test to retrieve all user accounts return 200 OK
	 */
	@Test
	public void testGetAllAccounts() throws IOException, URISyntaxException
	{
		URI uri = builder.setPath("/account/all").build();
		HttpGet request = new HttpGet(uri);
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();

		assertTrue(statusCode == 200);

		// check the content
		String jsonString = EntityUtils.toString(response.getEntity());

		Account[] accounts = mapper.readValue(jsonString, Account[].class);
		assertTrue(accounts.length > 0);
	}

	/**
	 * Test to retrieve account balance given account ID return 200 OK
	 */
	@Test
	public void testGetAccountBalance() throws IOException, URISyntaxException
	{
		URI uri = builder.setPath("/account/1/balance").build();
		HttpGet request = new HttpGet(uri);
		HttpResponse response = client.execute(request);

		int statusCode = response.getStatusLine().getStatusCode();
		
		assertTrue(statusCode == 200);

		// check the content, assert user test2 have balance 100
		String balance = EntityUtils.toString(response.getEntity());
		
		BigDecimal res = new BigDecimal(balance).setScale(4,
						RoundingMode.HALF_EVEN);
		BigDecimal db = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
		assertTrue(res.equals(db));
	}

	/**
	 * Test to create new user account return 200 OK
	 */
	@Test
	public void testCreateAccount() throws IOException, URISyntaxException
	{
		URI uri = builder.setPath("/account/create").build();
		BigDecimal balance = new BigDecimal(10).setScale(4,
						RoundingMode.HALF_EVEN);
		Account acc = new Account("test2", balance, "CNY");
		
		String jsonInString = mapper.writeValueAsString(acc);
		StringEntity entity = new StringEntity(jsonInString);
		
		HttpPut request = new HttpPut(uri);
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		assertTrue(statusCode == 200);
		
		String jsonString = EntityUtils.toString(response.getEntity());
		Account aAfterCreation = mapper.readValue(jsonString, Account.class);
		
		assertTrue(aAfterCreation.getUserName().equals("test2"));
		assertTrue(aAfterCreation.getCurrencyCode().equals("CNY"));
	}

	/**
	 * Test to create user account already existed. return 500 INTERNAL SERVER ERROR
	 */
	@Test
	public void testCreateExistingAccount() throws IOException,
					URISyntaxException
	{
		URI uri = builder.setPath("/account/create").build();
		
		Account acc = new Account("frederick", new BigDecimal(0), "USD");
		
		String jsonInString = mapper.writeValueAsString(acc);
		StringEntity entity = new StringEntity(jsonInString);
		
		HttpPut request = new HttpPut(uri);
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();

		assertTrue(statusCode == 500);
	}

	/**
	 * Test to delete valid user account return 200 OK
	 */
	@Test
	public void testDeleteAccount() throws IOException, URISyntaxException
	{
		URI uri = builder.setPath("/account/3").build();
		
		HttpDelete request = new HttpDelete(uri);
		request.setHeader("Content-type", "application/json");
		
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		assertTrue(statusCode == 200);
	}

	/**
	 * Test to delete non-existent account. return 404 NOT FOUND return 404 NOT FOUND
	 */
	@Test
	public void testDeleteNonExistingAccount() throws IOException,
					URISyntaxException
	{
		URI uri = builder.setPath("/account/300").build();
		
		HttpDelete request = new HttpDelete(uri);
		request.setHeader("Content-type", "application/json");
		
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		assertTrue(statusCode == 404);
	}

}
