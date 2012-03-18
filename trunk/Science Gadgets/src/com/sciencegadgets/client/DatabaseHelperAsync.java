package com.sciencegadgets.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sciencegadgets.server.Equation;
/**
 * The async counterpart of <code>DatabaseHelper</code>.
 */
public interface DatabaseHelperAsync {

	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	
	public void fetchAllUnits(AsyncCallback<String> callback); 
}
 