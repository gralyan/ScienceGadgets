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
package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.ui.SymbolDisplay;
import com.sciencegadgets.client.ui.specification.VariableSpecification;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.dimensions.CommonVariables;
import com.sciencegadgets.shared.dimensions.UnitMap;

public class VariablePrompt extends QuantityPrompt {

	public VariablePrompt(EquationNode equationNode, boolean clearDisplays,
			boolean mustCheckUnits) {
		super(equationNode, clearDisplays, mustCheckUnits);

		// Variable Specification
		spec = new VariableSpecification(clearDisplays,
				canHaveUnits(equationNode));
		reload(equationNode, clearDisplays, mustCheckUnits);
		specPanel.add(spec);

		// Established Selection
		for (CommonVariables var : CommonVariables.values()) {
			establishedSelection.add(var.getSymbol() + "-" + var.toString(),
					null, var);
		}
		// Handle Established Selection
		establishedSelection.addSelectionHandler(new SelectionHandler() {
			@Override
			public void onSelect(Cell selected) {
				CommonVariables establishedVariable = ((CommonVariables) selected
						.getEntity());
				spec.getSymbolDisplay()
						.setText(establishedVariable.getSymbol());
				spec.setUnit(new UnitMap(establishedVariable.getUnitAttribute()));
			}
		});

	}

	@Override
	protected String extractSymbol() {
		SymbolDisplay symbolDisplay = spec.getSymbolDisplay();
		String inputString = symbolDisplay.getText();

		if (inputString == null || inputString.equals("")) {
			symbolDisplay.addStyleName(CSS.INVALID_INPUT);
			Window.alert("Variable cannot be empty");
			return null;
		} else if (inputString.matches(".*\\d.*")) {
			symbolDisplay.addStyleName(CSS.INVALID_INPUT);
			Window.alert("Variable cannot contain numbers");
			return null;
		} else {
			symbolDisplay.removeStyleName(CSS.INVALID_INPUT);
			return inputString;
		}
	}

	@Override
	protected void setNode(String symbol) {
		node = node.replace(TypeSGET.Variable, symbol);
	}
}
