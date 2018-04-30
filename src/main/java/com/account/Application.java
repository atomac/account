package com.account;

/**
 * Main Application - This is the main application class.
 */
import com.account.dao.DAOFactory;
import com.account.service.AccountService;
import com.account.service.ServiceExceptionMapper;
import com.account.service.TransactionService;
import com.account.service.UserService;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Main Class (Starting point)
 */
public class Application
{
	private static Logger log = Logger.getLogger(Application.class);

	public static void main( String[] args ) throws Exception
	{
		// Initialize database with demo data
		log.info("Initialize demo .....");
		
		DAOFactory dataDaoFactory = DAOFactory.getDAOFactory(DAOFactory.DATA);
		dataDaoFactory.populateTestData();
		
		log.info("Initialisation Complete....");
		
		// Host service on jetty
		startService();
	}

	/**
	 * Service provisioning.
	 * 
	 * @throws Exception
	 */
	private static void startService() throws Exception
	{
		Server server = new Server(8080);
		
		ServletContextHandler context = new ServletContextHandler(
						ServletContextHandler.SESSIONS);
		
		context.setContextPath("/");
		
		server.setHandler(context);
		
		ServletHolder servletHolder = context.addServlet(ServletContainer.class, "/*");
		servletHolder.setInitParameter(
						"jersey.config.server.provider.classnames",
						UserService.class.getCanonicalName() + "," +
						AccountService.class.getCanonicalName()	+ "," +
						ServiceExceptionMapper.class.getCanonicalName() + ","+
						TransactionService.class.getCanonicalName());
		/*
		 * Start server. Provide for closure
		 */
		try
		{
			server.start();
			server.join();
		}
		finally
		{
			server.destroy();
		}
	}

}
