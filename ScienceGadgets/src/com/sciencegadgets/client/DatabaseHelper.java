package com.sciencegadgets.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sciencegadgets.client.entities.Unit;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface DatabaseHelper extends RemoteService {
	String[][] getVariables() throws IllegalArgumentException;
	String[] getEquationsByVariables(String[] name) throws IllegalArgumentException;
	String[] getAlgebraEquations() throws IllegalArgumentException;
	String saveEquation(String name) throws IllegalArgumentException;
	String[] getUnits() throws IllegalArgumentException;
	String[] saveUnit(Unit unit) throws IllegalArgumentException;
	String[] getUnitsFromOwl() throws IllegalArgumentException;
	
}
