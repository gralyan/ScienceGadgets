package com.sciencegadgets.client.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.DatabaseHelperAsync;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.shared.UnitUtil;

public class DataModerator {

	public static final DatabaseHelperAsync database = GWT
			.create(DatabaseHelper.class);

	private static boolean quantityKindsQueried = false;
	private static ArrayList<UnitSelection> toPopulate = new ArrayList<UnitSelection>();

	/**
	 * Holds only previously queried Unit groups by parent QuantityKind
	 */
	private static HashMap<String, LinkedList<Unit>> unitsQuantity = new HashMap<String, LinkedList<Unit>>();
	/**
	 * Holds only previously queried Unit groups by parent QuantityKind
	 */
	private static HashMap<String, Equation[]> equationsQuantity = new HashMap<String, Equation[]>();
	/**
	 * Holds all every QuantityKind once it has been queried for
	 */
	private static LinkedList<String> quantityKinds = null;

	// //////////////////////////////////////////////////////////////////
	// UnitsByQuantity
	// //////////////////////////////////////////////////////////////////
	public static void fill_UnitsByQuantity(final String quantityKind,
			SelectionPanel unitBox, String excludedUnitName) {
		LinkedList<Unit> units = unitsQuantity.get(quantityKind);

		if (units == null || units.size() == 0) {
			// If no local, get and fill local from database by RPC first
			query_UnitsByQuantity(unitBox, quantityKind, excludedUnitName);
		} else {
			populate_UnitsByQuantity(unitBox, units, excludedUnitName);
		}
	}

	private static void query_UnitsByQuantity(final SelectionPanel unitBox,
			final String quantityKind, final String excludedUnitName) {
		database.getUnitsByQuantity(quantityKind,
				new AsyncCallback<LinkedList<Unit>>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(LinkedList<Unit> units) {
						unitsQuantity.put(quantityKind, units);
						populate_UnitsByQuantity(unitBox, units,
								excludedUnitName);
					}
				});
	}

	private static void populate_UnitsByQuantity(SelectionPanel unitBox,
			LinkedList<Unit> units, String excludedUnitName) {
		unitBox.clear();
		for (Unit unit : units) {
			String unitName = unit.getName();
			if (!unitName.equals(excludedUnitName)) {
				unitBox.add(unit.getLabel() + " (" + unit.getSymbol() + ")",
						unitName, unit);
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
		database.getQuantityKinds(new AsyncCallback<LinkedList<String>>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(LinkedList<String> qKinds) {

				// Prefix should be first
				qKinds.remove(UnitUtil.PREFIXBINARY_QUANTITY_KIND);
				qKinds.addFirst(UnitUtil.PREFIXBINARY_QUANTITY_KIND);
				qKinds.remove(UnitUtil.PREFIX_QUANTITY_KIND);
				qKinds.addFirst(UnitUtil.PREFIX_QUANTITY_KIND);

				quantityKinds = qKinds;
				populate_Quantities(unitSelection);
				for (UnitSelection quantityBoxes : toPopulate) {
					populate_Quantities(quantityBoxes);
				}
			}
		});
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
			qBox.getWidget(1).addStyleName("quantityKindPrefix");
			qBox.getWidget(2).addStyleName("quantityKindPrefix");
		}
	}

}
