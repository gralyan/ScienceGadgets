/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
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
