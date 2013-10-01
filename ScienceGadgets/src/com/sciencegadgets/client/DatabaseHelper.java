package com.sciencegadgets.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface DatabaseHelper extends RemoteService {
	String[][] getVariables() throws IllegalArgumentException;
	String[] getEquationsByVariables(String[] name) throws IllegalArgumentException;
	String[] getAlgebraEquations() throws IllegalArgumentException;
	String saveEquation(String name) throws IllegalArgumentException;
	String saveTestA() throws IllegalArgumentException;
	String saveTestB(String name) throws IllegalArgumentException;
	void saveTestC() throws IllegalArgumentException;
	
}
