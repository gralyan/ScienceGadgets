package com.sciencegadgets.server;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import com.mysql.jdbc.Driver;

import com.sciencegadgets.client.GreetingService;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.matheclipse.core.eval.MathMLUtilities;
import org.matheclipse.core.eval.EvalEngine;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		String mathMLString = stringToMathML(input);

		// String s=openDatabase();

		return mathMLString;
		// *s+*/"\nHia, " + input + "!I am running " + serverInfo
		// + " It looks like you are using: " + userAgent;
	}

	String stringToMathML(String strEval) {
		StringWriter stw = new StringWriter();
		MathMLUtilities mathUtil = new MathMLUtilities(EvalEngine.get(), false);
		try {
			mathUtil.toMathML(strEval, stw);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// assertEquals(stw.toString(),
		// "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		// + "<math:math xmlns=\"http://www.w3.org/1998/Math/MathML\">\n" +
		// strResult + "\n</math:math>");

		return stw.toString();
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
			returnStatement = returnStatement + "\n"
					+ "Database connection established";
		} catch (Exception e) {
			returnStatement = returnStatement + "\n"
					+ "Cannot connect to database server";
			e.printStackTrace(System.out);
		} finally {
			if (conn != null) {
				try {
					conn.close();
					returnStatement = returnStatement + "\n"
							+ "Database connection terminated";
				} catch (Exception e) { /* ignore close errors */
				}
			}
		}
		return returnStatement;
	}
}