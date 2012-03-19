package com.sciencegadgets.server;

import com.sciencegadgets.client.GreetingService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	public String greetServer(String input) throws IllegalArgumentException {

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		return "Hello, " + input + "!I am running " + serverInfo
				+ " It looks like you are using: " + userAgent;
	}
}