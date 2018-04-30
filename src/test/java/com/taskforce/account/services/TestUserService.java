package com.taskforce.account.services;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import com.account.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

/**
 * Integration testing for RestAPI Test data are initialised from
 * src/test/resources/demo.sql 
 */
public class TestUserService extends TestService
{

	/**
	 * Test to retrieve user by given user name return 200 OK
	 */
	@Test
	public void testGetUser() throws IOException, URISyntaxException
	{
		URI uri = builder.setPath("/user/samuel").build();

		HttpGet request = new HttpGet(uri);

		HttpResponse response = client.execute(request);

		int statusCode = response.getStatusLine().getStatusCode();

		assertTrue(statusCode == 200);

		// check the content
		String jsonString = EntityUtils.toString(response.getEntity());

		User user = mapper.readValue(jsonString, User.class);

		assertTrue(user.getUserName().equals("samuel"));
		assertTrue(user.getEmailAddress().equals("samuel@gmail.com"));

	}

	/**
	 * Test to retrieve all users return 200 OK
	 */
	@Test
	public void testGetAllUsers() throws IOException, URISyntaxException
	{
		URI uri = builder.setPath("/user/all").build();
		
		HttpGet request = new HttpGet(uri);
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		assertTrue(statusCode == 200);
		
		// check the content
		String jsonString = EntityUtils.toString(response.getEntity());
		
		User[] users = mapper.readValue(jsonString, User[].class);
		
		assertTrue(users.length > 0);
	}

	/**
	 * Test to create user using JSON return 200 OK
	 */
	@Test
	public void testCreateUser() throws IOException, URISyntaxException
	{
		URI uri = builder.setPath("/user/create").build();
		
		User user = new User("frank", "frank@gmail.com");
		
		String jsonInString = mapper.writeValueAsString(user);
		StringEntity entity = new StringEntity(jsonInString);
		
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		assertTrue(statusCode == 200);
		
		String jsonString = EntityUtils.toString(response.getEntity());
		
		User uAfterCreation = mapper.readValue(jsonString, User.class);
		
		assertTrue(uAfterCreation.getUserName().equals("frank"));
		assertTrue(uAfterCreation.getEmailAddress().equals("frank@gmail.com"));
	}

	/**
	 * Test to create user already existed using JSON return 400 BAD REQUEST
	 */
	@Test
	public void testCreateExistingUser() throws IOException, URISyntaxException
	{
		URI uri = builder.setPath("/user/create").build();
		
		User user = new User("test1", "test1@gmail.com");
		
		String jsonInString = mapper.writeValueAsString(user);
		StringEntity entity = new StringEntity(jsonInString);
		
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		assertTrue(statusCode == 400);

	}

	/**
	 * Test to update Existing User using JSON provided from client return 200 OK
	 */
	@Test
	public void testUpdateUser() throws IOException, URISyntaxException
	{
		URI uri = builder.setPath("/user/2").build();
		
		User user = new User(2L, "test1", "test1123@gmail.com");
		
		String jsonInString = mapper.writeValueAsString(user);
		StringEntity entity = new StringEntity(jsonInString);
		
		HttpPut request = new HttpPut(uri);
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		assertTrue(statusCode == 200);
	}

	/**
	 * Test to update non existing User using JSON provided from client return
	 * 404 NOT FOUND
	 */
	@Test
	public void testUpdateNonExistingUser() throws IOException,
					URISyntaxException
	{
		URI uri = builder.setPath("/user/100").build();
		User user = new User(2L, "test1", "test1123@gmail.com");
		
		String jsonInString = mapper.writeValueAsString(user);
		StringEntity entity = new StringEntity(jsonInString);
		
		HttpPut request = new HttpPut(uri);
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		assertTrue(statusCode == 404);
	}

	/**
	 * Test to delete user return 200 OK
	 */
	@Test
	public void testDeleteUser() throws IOException, URISyntaxException
	{
		URI uri = builder.setPath("/user/3").build();
		HttpDelete request = new HttpDelete(uri);
		
		request.setHeader("Content-type", "application/json");
		
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		assertTrue(statusCode == 200);
	}

	/**
	 * Test to delete non-existing user return 404 NOT FOUND
	 */
	@Test
	public void testDeleteNonExistingUser() throws IOException,
					URISyntaxException
	{
		URI uri = builder.setPath("/user/300").build();
		
		HttpDelete request = new HttpDelete(uri);
		
		request.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(request);
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		assertTrue(statusCode == 404);
	}

}
