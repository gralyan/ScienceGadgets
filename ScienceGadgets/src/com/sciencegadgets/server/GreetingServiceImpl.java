package com.sciencegadgets.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
		String ip = "";
		try {
			InetAddress address = InetAddress.getLocalHost();
			ip = address.getHostAddress();

			System.out.println("IP Address = " + ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			returnStatement = returnStatement + "\n"+ "couldn't get address";
		}

		try {

			String user = "test2";
			String pass = "test2";
			String url = "jdbc:mysql://"+ip+":3306/jooggr_test2";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, user, pass);
			returnStatement = returnStatement + "\n"+"Database connection established";
		} catch (Exception e) {
			returnStatement = returnStatement + "\n"+ "Cannot connect to database server";
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