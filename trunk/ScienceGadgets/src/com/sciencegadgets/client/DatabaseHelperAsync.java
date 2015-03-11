package com.sciencegadgets.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.objectify.Key;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.entities.QuantityKind;
import com.sciencegadgets.client.entities.Unit;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.shared.Diagram;

public interface DatabaseHelperAsync {
	void getAlgebraEquations(AsyncCallback<Equation[]> asyncCallback);

	void saveEquation(String mathML, String html,
			AsyncCallback<Equation> callback) throws IllegalArgumentException;

	void getEquationsWithQuantities(ArrayList<String> quantityKinds,
			AsyncCallback<Equation[]> callback) throws IllegalArgumentException;

	void getUnitsAll(AsyncCallback<LinkedList<Unit>> callback)
			throws IllegalArgumentException;
	void getUnitsByQuantity(String quantityKind,
			AsyncCallback<LinkedList<Unit>> callback)
			throws IllegalArgumentException;

	void getQuantityKinds(AsyncCallback<LinkedList<String>> callback);

	void getBlobURL(AsyncCallback<String> callback);

	void getUnitQuantityKindName(String unitName, AsyncCallback<String> callback);

	void reCreateUnits(AsyncCallback<Void> callback);

	void getProblemsByBadge(Badge badge,
			AsyncCallback<ArrayList<Problem>> asyncCallback);

	void getProblemsByBadges(HashSet<Badge> badges,
			AsyncCallback<ArrayList<Problem>> asyncCallback);

	void newProblem(String title, String description, Badge requiredBadge,
			Diagram diagram, Equation equation, String toSolveID,
			AsyncCallback<String> asyncCallback);

	void saveProblem(Problem problem, AsyncCallback<String> asyncCallback);
	void getProblem(long id, AsyncCallback<Problem> asyncCallback);

}
