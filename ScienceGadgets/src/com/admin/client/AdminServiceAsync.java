package com.admin.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface AdminServiceAsync {
	void saveEquation(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
