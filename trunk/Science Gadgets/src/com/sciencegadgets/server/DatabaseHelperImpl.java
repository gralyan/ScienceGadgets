package com.sciencegadgets.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sciencegadgets.client.DatabaseHelper;

public class DatabaseHelperImpl extends RemoteServiceServlet implements
		DatabaseHelper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1150183648258406201L;

	@Override
	public String greetServer(String name) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public String fetchAllUnits() {
		return "hello";//Equation.getAllUnits();
	}
}
