package com.sciencegadgets.client;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Unit;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface DatabaseHelper extends RemoteService {
	Equation[] getAlgebraEquations() throws IllegalArgumentException;
	LinkedList<Unit> getUnitsByQuantity(String quantityKind)
			throws IllegalArgumentException;
	LinkedList<String> getQuantityKinds();
	String saveEquation(String mathML, String html)
			throws IllegalArgumentException;
	Equation[] getEquationsWithQuantities(ArrayList<String> quantityKinds)
			throws IllegalArgumentException;
	Unit getUnit(String unitName);
	
	String getBlobURL();
}
