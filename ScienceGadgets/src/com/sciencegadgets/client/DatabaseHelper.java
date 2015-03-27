package com.sciencegadgets.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.googlecode.objectify.Key;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.entities.QuantityKind;
import com.sciencegadgets.client.entities.Unit;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.shared.Diagram;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface DatabaseHelper extends RemoteService {
	Equation[] getAlgebraEquations() throws IllegalArgumentException;
	LinkedList<Unit> getUnitsAll() throws IllegalArgumentException;
	LinkedList<Unit> getUnitsByQuantity(String quantityKind)
			throws IllegalArgumentException;
	LinkedList<String> getQuantityKinds();
	Equation saveEquation(String mathML, String html);
	Equation[] getEquationsWithQuantities(ArrayList<String> quantityKinds)
			throws IllegalArgumentException;
	String getUnitQuantityKindName(String unitName);
	
	String getBlobURL();
	void reCreateUnits();
	ArrayList<Problem> getProblemsByBadge(Badge badge);
	ArrayList<Problem> getProblemsByBadges(HashSet<Badge> badges);
	String newProblem(String title, String description, Badge requiredBadge,
			Diagram diagram,
			Equation equation, String toSolveID);
	String saveProblem(Problem problem);
	Problem getProblem(long id);
}