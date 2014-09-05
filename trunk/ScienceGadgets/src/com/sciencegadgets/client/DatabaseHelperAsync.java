package com.sciencegadgets.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.entities.Unit;
import com.sciencegadgets.client.entities.users.Badge;

public interface DatabaseHelperAsync {
	void getAlgebraEquations(AsyncCallback<Equation[]> asyncCallback);
	void saveEquation(String mathML, String html, AsyncCallback<Equation> callback)
			throws IllegalArgumentException;
	void getEquationsWithQuantities(ArrayList<String> quantityKinds, AsyncCallback<Equation[]> callback)
			throws IllegalArgumentException;
	void getUnitsByQuantity(String quantityKind, AsyncCallback<LinkedList<Unit>> callback)
			throws IllegalArgumentException;
	void getQuantityKinds(AsyncCallback<LinkedList<String>> callback);
	
	void getBlobURL(AsyncCallback<String> callback);
	void getUnit(String unitName, AsyncCallback<Unit> callback);
	void reCreateUnits(AsyncCallback<Void> callback);
	void getProblemsByBadge(Badge badge, AsyncCallback<ArrayList<Problem>> asyncCallback);
	void getProblemsByBadges(HashSet<Badge> badges, AsyncCallback<ArrayList<Problem>> asyncCallback);
	void saveProblem(
			String title,
			String description,
			Badge requiredBadge, Equation equation,
			AsyncCallback<Problem> asyncCallback);

	void saveEntity(Problem entity,
			AsyncCallback<Void> asyncCallback);

}
