package com.sciencegadgets.client;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Unit;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DatabaseHelperAsync {
	void getAlgebraEquations(AsyncCallback<Equation[]> asyncCallback);
	void saveEquation(String mathML, String html, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void getEquationsWithQuantities(List<String> quantityKinds, AsyncCallback<Equation[]> callback)
			throws IllegalArgumentException;
	void getUnitsByQuantity(String quantityKind, AsyncCallback<Unit[]> callback)
			throws IllegalArgumentException;
	void getQuantityKinds(AsyncCallback<Set<String>> callback);
	
	void getBlobURL(AsyncCallback<String> callback);

}
