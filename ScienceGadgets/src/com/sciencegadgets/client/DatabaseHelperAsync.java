package com.sciencegadgets.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DatabaseHelperAsync {
	void getVariables(AsyncCallback<String[][]> callback)
			throws IllegalArgumentException;
	void getEquationsByVariables(String[] input, AsyncCallback<String[]> callback)
			throws IllegalArgumentException;
	void getAlgebraEquations(AsyncCallback<String[]> asyncCallback);
	void saveEquation(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void saveTestA(AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void saveTestB(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void saveTestC( AsyncCallback callback)
			throws IllegalArgumentException;
}
