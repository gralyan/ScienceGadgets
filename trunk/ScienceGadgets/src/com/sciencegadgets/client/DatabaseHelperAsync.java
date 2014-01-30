package com.sciencegadgets.client;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Unit;

public interface DatabaseHelperAsync {
	void getAlgebraEquations(AsyncCallback<Equation[]> asyncCallback);
	void saveEquation(String mathML, String html, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void getEquationsWithQuantities(ArrayList<String> quantityKinds, AsyncCallback<Equation[]> callback)
			throws IllegalArgumentException;
	void getUnitsByQuantity(String quantityKind, AsyncCallback<LinkedList<Unit>> callback)
			throws IllegalArgumentException;
	void getQuantityKinds(AsyncCallback<LinkedList<String>> callback);
	
	void getBlobURL(AsyncCallback<String> callback);
	void getUnit(String unitName, AsyncCallback<Unit> callback);
	void reCreateUnits(AsyncCallback<Void> callback);

}
