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

import java.math.BigDecimal;

import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.KeyPadNumerical;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.ui.SymbolDisplay;
import com.sciencegadgets.client.ui.specification.NumberSpecification;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.dimensions.CommonConstants;

public class NumberPrompt extends QuantityPrompt{

	KeyPadNumerical numPad;

	CommonConstants constantSeleced = null;

	public NumberPrompt(EquationNode equationNode, boolean clearDisplays, boolean mustCheckUnits) {
		super(equationNode, clearDisplays, mustCheckUnits);
		spec = new NumberSpecification(clearDisplays, canHaveUnits(equationNode), true);
		reload(equationNode, clearDisplays, mustCheckUnits);
		specPanel.add(spec);

		// Fill Established Selection
		for (CommonConstants constant : CommonConstants.values()) {
			establishedSelection.add(
					constant.getSymbol() + "-" + constant.getName(), null,
					constant);
		}

		// Handle Established Selection
		establishedSelection.addSelectionHandler(new SelectionHandler() {
			@Override
			public void onSelect(Cell selected) {
				constantSeleced = ((CommonConstants) selected.getEntity());

				spec.getSymbolDisplay().setText(constantSeleced.getValue());

				spec.setUnit(constantSeleced.getUnitMap());
			}
		});
	}
	
	@Override
	public void reload(EquationNode mathNode, boolean clearDisplays,
			boolean mustCheckUnits) {
		
		super.reload(mathNode, clearDisplays, mustCheckUnits);
	}
	
	@Override
	protected String extractSymbol() {
		SymbolDisplay symbolDisplay = spec.getSymbolDisplay();
		String inputString = null;
		if (RandomSpecPanel.RANDOM_SYMBOL.equals(symbolDisplay.getText())) {
			symbolDisplay.removeStyleName(CSS.INVALID_INPUT);
			return RandomSpecPanel.RANDOM_SYMBOL;
		} else {
			try {
				BigDecimal value = new BigDecimal(symbolDisplay.getText());
				inputString = value.toString();
				symbolDisplay.removeStyleName(CSS.INVALID_INPUT);
				return inputString;

			} catch (NumberFormatException e) {
				Window.alert("The input must be a number\nyou can also change this to a variable if necessary");
				symbolDisplay.addStyleName(CSS.INVALID_INPUT);
				return null;
			}
		}
	}

	@Override
	protected void setNode(String symbol) {

		node = node.replace(TypeSGET.Number, symbol);

		if (RandomSpecPanel.RANDOM_SYMBOL.equals(symbol)) {
			node.getXMLNode().setAttribute(
					MathAttribute.Randomness.getAttributeName(), ((NumberSpecification)spec).getRandomness());
		} else {
			node.getXMLNode().removeAttribute(
					MathAttribute.Randomness.getAttributeName());
		}

		if (constantSeleced != null
				&& constantSeleced.getValue().equals(spec.getSymbolDisplay().getText())
				&& constantSeleced.getUnitMap().equals(spec.getUnitMap())) {
			node.setConstant(constantSeleced);
		}
	}

}
