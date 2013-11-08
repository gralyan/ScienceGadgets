package com.sciencegadgets.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sciencegadgets.client.entities.Unit;

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
	void getUnits(AsyncCallback<String[]> callback)
			throws IllegalArgumentException;
	void saveUnit(Unit unit, AsyncCallback<String[]> callback)
			throws IllegalArgumentException;
	void getUnitsFromOwl(AsyncCallback<String[]> callback)
			throws IllegalArgumentException;

}
