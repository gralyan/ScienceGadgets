package com.sciencegadgets.server;

import java.sql.Connection;
import java.sql.DriverManager;
import com.mysql.jdbc.Driver;

import com.sciencegadgets.client.GreetingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		String s=openDatabase();

		return s+"\nHia, " + input + "!I am running " + serverInfo
				+ " It looks like you are using: " + userAgent;
	}

	String openDatabase() {
		String returnStatement = "";
		Connection conn = null;

		try {

			String user = "jooggr_test123";
			String pass = "test123";
			String url = "jdbc:mysql://204.93.169.176:3306/jooggr_test123";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, user, pass);
			returnStatement = returnStatement + "\n"+"Database connection established";
		} catch (Exception e) {
			returnStatement = returnStatement + "\n"+ "Cannot connect to database server";
			e.printStackTrace(System.out);
		} finally {
			if (conn != null) {
				try {
					conn.close();
					returnStatement = returnStatement + "\n"+ "Database connection terminated";
				} catch (Exception e) { /* ignore close errors */
				}
			}
		}
		return returnStatement;
	}
}