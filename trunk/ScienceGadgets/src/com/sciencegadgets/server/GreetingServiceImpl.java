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

		openDatabase();

		return "Hia, " + input + "!I am running " + serverInfo
				+ " It looks like you are using: " + userAgent;
	}

	void openDatabase() {
		Connection conn = null;
		String ip = "";
		try {
			InetAddress address = InetAddress.getLocalHost();
			ip = address.getHostAddress();

			System.out.println("IP Address = " + ip);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		try {

			String user = "test2";
			String pass = "test2";

			String url = "jdbc:mysql://localhost/jooggr_test2";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(url, user, pass);
			System.out.println("Database connection established");
		} catch (Exception e) {
			System.err.println("Cannot connect to database server");
		} finally {
			if (conn != null) {
				try {
					conn.close();
					System.out.println("Database connection terminated");
				} catch (Exception e) { /* ignore close errors */
				}
			}
		}
	}
}