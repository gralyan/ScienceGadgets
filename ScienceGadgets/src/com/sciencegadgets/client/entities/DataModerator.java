package com.sciencegadgets.client.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.DatabaseHelperAsync;
import com.sciencegadgets.client.SelectionPanel;

public class DataModerator {

	public static final DatabaseHelperAsync database = GWT
			.create(DatabaseHelper.class);

	private static boolean quantityKindsQueried = false;
	private static ArrayList<SelectionPanel> toPopulate = new ArrayList<SelectionPanel>();

	/**
	 * Holds only previously queried Unit groups by parent QuantityKind
	 */
	private static HashMap<String, Unit[]> unitsQuantity = new HashMap<String, Unit[]>();
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
			SelectionPanel unitBox) {
		Unit[] units = unitsQuantity.get(quantityKind);

		if (units == null || units.length == 0) {
			// If no local, get and fill local from database by RPC first
			query_UnitsByQuantity(unitBox, quantityKind);
		} else {
			populate_UnitsByQuantity(unitBox, units);
		}
	}

	private static void query_UnitsByQuantity(final SelectionPanel unitBox,
			final String quantityKind) {
		database.getUnitsByQuantity(quantityKind, new AsyncCallback<Unit[]>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Unit[] units) {
				for(Unit u : units) {
				System.out.println(u.getName());
				}
				unitsQuantity.put(quantityKind, units);
				populate_UnitsByQuantity(unitBox, units);
			}
		});
	}

	private static void populate_UnitsByQuantity(SelectionPanel unitBox,
			Unit[] units) {
		unitBox.clear();
		for (Unit unit : units) {
			unitBox.add(unit.getLabel() + " (" + unit.getSymbol() + ")",
					unit.getName(), unit);
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
	public static void fill_Quantities(SelectionPanel quantityBox) {

		if (!quantityKindsQueried) {
			// If no local, try to get from database by RPC first
			quantityKindsQueried = true;
			query_Quantities(quantityBox);
		} else {
			if (quantityKinds != null) {// already available
				populate_Quantities(quantityBox);
			} else {// still waiting
				toPopulate.add(quantityBox);
			}
		}
	}

	private static void query_Quantities(final SelectionPanel quantityBox) {
		database.getQuantityKinds(new AsyncCallback<LinkedList<String>>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(LinkedList<String> qKinds) {
				
				//Prefix should be first
				qKinds.remove("Prefix");
				qKinds.addFirst("Prefix");
				
				quantityKinds = qKinds;
				populate_Quantities(quantityBox);
				for (SelectionPanel quantityBoxes : toPopulate) {
					populate_Quantities(quantityBoxes);
				}
			}
		});
	}

	private static void populate_Quantities(SelectionPanel quantityBox) {
		quantityBox.clear();
		for (String quantityKind : quantityKinds) {
			quantityBox.add(quantityKind, quantityKind);
		}

		//The Prefix quantity is special, should stand out
		quantityBox.getWidget(1).addStyleName("quantityKindPrefix");
	}


}
