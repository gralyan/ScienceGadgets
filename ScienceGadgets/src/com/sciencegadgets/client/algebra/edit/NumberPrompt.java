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
	RandomSpecPanel randSpec = new RandomSpecPanel();

	String randomness = "";
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
					MathAttribute.Randomness.getAttributeName(), randomness);
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
