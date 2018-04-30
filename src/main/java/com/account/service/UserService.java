package com.account.service;

import com.account.dao.DAOFactory;
import com.account.exception.CustomException;
import com.account.model.User;

import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path( "/user" )
@Produces( MediaType.APPLICATION_JSON )
public class UserService
{
	private static Logger log = Logger.getLogger(UserService.class);

	private final DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.DATA);

	/**
	 * Find by userName
	 * 
	 * @param - userName
	 * @return - user details
	 * @throws CustomException
	 */
	@GET
	@Path( "/{userName}" )
	public User getUserByName( @PathParam( "userName" ) String userName )
					throws CustomException
	{
		// If debug is enabled, output message to kog.
		if (log.isDebugEnabled())
		{
			log.debug("Request Received for get User by Name " + userName);
		}
		
		// Retrieve user details.
		final User user = daoFactory.getUserDAO().getUserByName(userName);
		
		// If user is not found, throw exception.
		if (user == null)
		{
			throw new WebApplicationException("User Not Found",
							Response.Status.NOT_FOUND);
		}
		return user;
	}

	/**
	 * Find by all
	 * 
	 * @param userName - user name.
	 * @return - list of all users.
	 * @throws CustomException
	 */
	@GET
	@Path( "/all" )
	public List<User> getAllUsers() throws CustomException
	{
		return daoFactory.getUserDAO().getAllUsers();
	}

	/**
	 * Create User
	 * 
	 * @param user - user details.
	 * @return - returned user details.
	 * @throws CustomException
	 */
	@POST
	@Path( "/create" )
	public User createUser( User user ) throws CustomException
	{
		// If the user details is not return, throw exception.
		if (daoFactory.getUserDAO().getUserByName(user.getUserName()) != null)
		{
			throw new WebApplicationException("User name already exist",
							Response.Status.BAD_REQUEST);
		}
		
		// Insert user details on table and obtain user id
		final long uId = daoFactory.getUserDAO().insertUser(user);
		
		// return User details.
		return daoFactory.getUserDAO().getUserById(uId);
	}

	/**
	 * Find by User Id
	 * 
	 * @param userId - user id
	 * @param user user details.
	 * @return - update response.
	 * @throws CustomException
	 */
	@PUT
	@Path( "/{userId}" )
	public Response updateUser( @PathParam( "userId" ) long userId, User user )
					throws CustomException
	{
		// Get user data access object for update.
		final int updateCount = daoFactory.getUserDAO()
						.updateUser(userId, user);
		
		// Verify update response as OKAY
		if (updateCount == 1)
		{
			return Response.status(Response.Status.OK).build();
		}
		// Otherwise, record is not found.
		else
		{
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	/**
	 * Delete by User Id
	 * 
	 * @param userId - user id.
	 * @return - delete response.
	 * @throws CustomException
	 */
	@DELETE
	@Path( "/{userId}" )
	public Response deleteUser( @PathParam( "userId" ) long userId )
					throws CustomException
	{
		// Determine if user exists for deletion.
		int deleteCount = daoFactory.getUserDAO().deleteUser(userId);
		
		// Determine if record exists fro deletion. Return the response code OKAY.
		if (deleteCount == 1)
		{
			return Response.status(Response.Status.OK).build();
		}
		// Otherwise record is not found.
		else
		{
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

}
