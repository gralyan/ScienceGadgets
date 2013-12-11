package com.sciencegadgets.client;

import java.util.List;
import java.util.Set;

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
	Unit[] getUnitsByQuantity(String quantityKind)
			throws IllegalArgumentException;
	Set<String> getQuantityKinds();
	String saveEquation(String mathML, String html)
			throws IllegalArgumentException;
	Equation[] getEquationsWithQuantities(List<String> quantityKinds)
			throws IllegalArgumentException;
	Unit getUnit(String unitName);
	
	String getBlobURL();
}
