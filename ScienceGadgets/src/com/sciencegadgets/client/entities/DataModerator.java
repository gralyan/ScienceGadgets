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
package com.sciencegadgets.client.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.objectify.Key;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.DatabaseHelperAsync;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.UnitSelection;
import com.sciencegadgets.shared.dimensions.QuantityKindEnum;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitEnum;
import com.sciencegadgets.shared.dimensions.UnitName;

public class DataModerator {

	public static final DatabaseHelperAsync database = GWT
			.create(DatabaseHelper.class);

	private static boolean quantityKindsQueried = false;
	private static ArrayList<UnitSelection> toPopulate = new ArrayList<UnitSelection>();

	// /**
	// * Holds only previously queried Unit groups by parent QuantityKind
	// */
	// private static HashMap<String, LinkedList<Unit>> unitsQuantity = new
	// HashMap<String, LinkedList<Unit>>();
	/**
	 * Holds only previously queried Unit groups by parent QuantityKind
	 */
	private static LinkedList<Unit> unitsAll = null;
	private static String updatedUnitQuery = null;
	private static HashMap<Unit, UnitName> unitToBeSetWaitingList = null;
	/**
	 * Holds only previously queried Unit groups by parent QuantityKind
	 */
	private static HashMap<String, Equation[]> equationsQuantity = new HashMap<String, Equation[]>();
	/**
	 * Holds all every QuantityKind once it has been queried for
	 */
	private static LinkedList<String> quantityKinds = null;

	private static void loadUnits() {
		unitsAll = new LinkedList<Unit>();
		for (UnitEnum unit : UnitEnum.values()) {
			unitsAll.add(new Unit(unit.getUnitName(), unit
					.getQuantityKindName(), unit.getLabel(), unit
					.getDescription(), unit.getConversionMultiplier()));
		}
	}
	
	/**
	 * Uses the unit name to query for the Unit.
	 * 
	 * @param unitToBeSet
	 *            - empty unit will be set to the unit found
	 * @param unitName
	 */
	public static void findUnit(final Unit unitToBeSet, final UnitName unitName) {

		if (unitsAll != null) {
			setUnit(unitToBeSet, unitName);
			return;
		}

		if (unitToBeSetWaitingList != null) {
			unitToBeSetWaitingList.put(unitToBeSet, unitName);
			return;
		}

		unitToBeSetWaitingList = new HashMap<Unit, UnitName>();

		loadUnits();

		setUnit(unitToBeSet, unitName);
		for (Entry<Unit, UnitName> nextInLine : unitToBeSetWaitingList
				.entrySet()) {
			setUnit(nextInLine.getKey(), nextInLine.getValue());
		}
		unitToBeSetWaitingList = null;

		// database.getUnitsAll(new AsyncCallback<LinkedList<Unit>>() {
		// @Override
		// public void onFailure(Throwable caught) {
		// JSNICalls.error("Could not find units");
		// }
		//
		// @Override
		// public void onSuccess(LinkedList<Unit> result) {
		// unitsAll = result;
		// setUnit(unitToBeSet, unitName);
		// for (Entry<Unit, UnitName> nextInLine : unitToBeSetWaitingList
		// .entrySet()) {
		// setUnit(nextInLine.getKey(), nextInLine.getValue());
		// }
		// unitToBeSetWaitingList = null;
		// }
		// });
	}

	private static void setUnit(final Unit unitToBeSet, final UnitName unitName) {
		String unitNameStr = unitName.toString();
		for (Unit u : unitsAll) {
			if (u.name.equals(unitNameStr)) {
				unitToBeSet.conversionMultiplier = u.conversionMultiplier;
				unitToBeSet.description = u.description;
				unitToBeSet.label = u.label;
				unitToBeSet.name = u.name;
				unitToBeSet.quantityKindName = u.getQuantityKindName();
				return;
			}
		}
	}

	// //////////////////////////////////////////////////////////////////
	// Units
	// //////////////////////////////////////////////////////////////////

	public static void fill_UnitsByQuantity(final String quantityKind,
			SelectionPanel unitBox, String excludedUnitName) {
		if (quantityKind == null || "".equals(quantityKind)) {
			return;
		}

		if (unitsAll == null || unitsAll.size() == 0) {
			// If no local, get and fill local from database by RPC first
			query_UnitsByQuantity(unitBox, quantityKind, excludedUnitName,
					false);
		} else {
			populate_UnitsByQuantity(quantityKind, unitBox, excludedUnitName);
		}
	}

	public static void fill_UnitsBySearch(final String searchQ,
			SelectionPanel unitBox, String excludedUnitName) {
		if (searchQ == null || "".equals(searchQ)) {
			return;
		}

		if (unitsAll == null || unitsAll.size() == 0) {
			// If no local, get and fill local from database by RPC first
			if (updatedUnitQuery == null) {
				updatedUnitQuery = null;
				query_UnitsByQuantity(unitBox, searchQ, excludedUnitName, true);
			} else {
				updatedUnitQuery = searchQ;
			}
		} else {
			populate_UnitsBySearch(searchQ, unitBox, excludedUnitName);
		}
	}

	private static void query_UnitsByQuantity(final SelectionPanel unitBox,
			final String searchSpec, final String excludedUnitName,
			final boolean isSearch) {
		
		loadUnits();
		
//		database.getUnitsAll(new AsyncCallback<LinkedList<Unit>>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				JSNICalls.log("getUnitsByQuantity FAILED: "
//						+ caught.getMessage());
//			}
//
//			@Override
//			public void onSuccess(LinkedList<Unit> units) {
//				unitsAll = units;
				if (isSearch) {
					String searchQ = updatedUnitQuery == null ? searchSpec
							: updatedUnitQuery;
					updatedUnitQuery = null;
					populate_UnitsBySearch(searchQ, unitBox, excludedUnitName);
				} else {
					populate_UnitsByQuantity(searchSpec, unitBox,
							excludedUnitName);
				}
//			}
//		});
	}

	private static void populate_UnitsByQuantity(String quantityKind,
			SelectionPanel unitBox, String excludedUnitName) {
		unitBox.clear();
		for (Unit unit : unitsAll) {
			if (unit.getQuantityKindName().equals(quantityKind)) {
				String unitName = unit.getName().toString();
				if (!unitName.equals(excludedUnitName)) {
					unitBox.add(unit.getLabel() + " (<span class=\'" + CSS.UNIT
							+ "\'>" + unit.getSymbol() + "</span>)", unitName,
							unit);
				}
			}
		}
	}

	private static void populate_UnitsBySearch(String searchQ,
			SelectionPanel unitBox, String excludedUnitName) {
		unitBox.clear();
		String searchLowerCase = searchQ.toLowerCase();
		for (Unit unit : unitsAll) {
			String unitLabel = unit.getLabel();
			String unitSymbol = unit.getSymbol();
			if (unitLabel.toLowerCase().contains(searchLowerCase)
					|| unitSymbol.toLowerCase().contains(searchLowerCase)) {
				String unitName = unit.getName().toString();
				if (!unitName.equals(excludedUnitName)) {
					unitBox.add(unit.getLabel() + " (<span class=\'" + CSS.UNIT
							+ "\'>" + unit.getSymbol() + "</span>)", unitName,
							unit);
				}
			}
		}
	}

	// //////////////////////////////////////////////////////////////////
	// EquationsByQuantities
	// //////////////////////////////////////////////////////////////////
	public static void fill_EquationsByQuantities(final String quantityKinds,
			SelectionPanel equationBox) {
		Equation[] equations = equationsQuantity.get(quantityKinds);

		if (equations == null || equations.length == 0) {
			// If no local, get and fill local from database by RPC first
			query_EquationsByQuantities(equationBox, quantityKinds);
		} else {
			populate_EquationsByQuantities(equationBox, equations);
		}
	}

	private static void query_EquationsByQuantities(
			final SelectionPanel equationBox, final String quantityKinds) {
		ArrayList<String> qkinds = new ArrayList<String>();
		qkinds.add(quantityKinds);
		database.getEquationsWithQuantities(qkinds,
				new AsyncCallback<Equation[]>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Equation[] equations) {
						equationsQuantity.put(quantityKinds, equations);
						populate_EquationsByQuantities(equationBox, equations);
					}
				});
	}

	private static void populate_EquationsByQuantities(
			SelectionPanel equationBox, Equation[] equations) {
		equationBox.clear();
		for (Equation equation : equations) {
			equationBox.add(equation.getHtml(), equation.getMathML());
		}
	}

	// //////////////////////////////////////////////////////////////////
	// Quantities
	// //////////////////////////////////////////////////////////////////
	public static void fill_Quantities(UnitSelection unitSelection) {

		if (!quantityKindsQueried) {
			// If no local, try to get from database by RPC first
			quantityKindsQueried = true;
			query_Quantities(unitSelection);
		} else {
			if (quantityKinds != null) {// already available
				populate_Quantities(unitSelection);
			} else {// still waiting
				toPopulate.add(unitSelection);
			}
		}
	}

	private static void query_Quantities(final UnitSelection unitSelection) {

		LinkedList<String> qKinds = new LinkedList<String>();

		for (QuantityKindEnum qk : QuantityKindEnum.values()) {
			qKinds.add(qk.toString());
		}

		// Prefix should be first
		qKinds.remove(UnitAttribute.PREFIXBINARY_QUANTITY_KIND);
		qKinds.addFirst(UnitAttribute.PREFIXBINARY_QUANTITY_KIND);
		qKinds.remove(UnitAttribute.PREFIX_QUANTITY_KIND);
		qKinds.addFirst(UnitAttribute.PREFIX_QUANTITY_KIND);

		quantityKinds = qKinds;
		populate_Quantities(unitSelection);
		for (UnitSelection quantityBoxes : toPopulate) {
			populate_Quantities(quantityBoxes);
		}

		// TODO clean up
		// Migrated from queried data to static enum

		// database.getQuantityKinds(new AsyncCallback<LinkedList<String>>() {
		// @Override
		// public void onFailure(Throwable caught) {
		// }
		//
		// @Override
		// public void onSuccess(LinkedList<String> qKinds) {
		//
		// // Prefix should be first
		// qKinds.remove(UnitAttribute.PREFIXBINARY_QUANTITY_KIND);
		// qKinds.addFirst(UnitAttribute.PREFIXBINARY_QUANTITY_KIND);
		// qKinds.remove(UnitAttribute.PREFIX_QUANTITY_KIND);
		// qKinds.addFirst(UnitAttribute.PREFIX_QUANTITY_KIND);
		//
		// quantityKinds = qKinds;
		// populate_Quantities(unitSelection);
		// for (UnitSelection quantityBoxes : toPopulate) {
		// populate_Quantities(quantityBoxes);
		// }
		// }
		// });
	}

	private static void populate_Quantities(UnitSelection unitSelection) {
		SelectionPanel qBox = unitSelection.quantityBox;
		qBox.clear();
		for (String quantityKind : quantityKinds) {
			qBox.add(quantityKind, quantityKind);
		}

		// The Prefix quantity is special, should stand out
		if (unitSelection.isQuantityOnly()) {
			qBox.getWidget(1).removeFromParent();
			qBox.getWidget(1).removeFromParent();
		} else {
			qBox.getWidget(1).addStyleName(CSS.QUANTITY_KIND_PREFIX);
			qBox.getWidget(2).addStyleName(CSS.QUANTITY_KIND_PREFIX);
		}

	}
	
	public static String getQuantityKindByUnitName(String unitName) {
		if(unitName == null || "".equals(unitName.toString())) {
			return null;
		}
		if(unitsAll == null || unitsAll.isEmpty()) {
			loadUnits();
		}
		for(Unit unit : unitsAll) {
			if(unitName.equals(unit.getName().toString())) {
				return unit.getQuantityKindName();
			}
		}
		return null;
	}

}
