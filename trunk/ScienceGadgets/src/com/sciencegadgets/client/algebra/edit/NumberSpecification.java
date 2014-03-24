package com.sciencegadgets.client.algebra.edit;

import java.math.BigDecimal;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.conversion.Constant;
import com.sciencegadgets.client.conversion.DerivedUnit;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.KeyPadNumerical;
import com.sciencegadgets.client.ui.UnitSelection;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.UnitAttribute;

public class NumberSpecification extends QuantitySpecification {

	KeyPadNumerical numPad;
	RandomSpecPanel randSpec = new RandomSpecPanel();

	String randomness = "";
	Constant constantSeleced = null;

	public NumberSpecification(EquationNode mathNode) {
		super(mathNode, true);
	}
	public NumberSpecification(EquationNode mathNode, boolean clearDisplays) {
		super(mathNode, clearDisplays);

		// Number Pad
		numPad = new KeyPadNumerical(symbolDisplay);
		symbolPalette.add(numPad);

		// Randomness Spec
		symbolPalette.add(randSpec);
		randSpec.setVisible(false);
		randSpec.addOkClickHandler((new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				randomness = randSpec.getRandomness();
				if (randomness != null) {
					symbolDisplay.setText(RandomSpecPanel.RANDOM_SYMBOL);
				} else {
					symbolDisplay.setText("");
				}
			}
		}));

		// Symbol Toggle - switch Number Pad and Randomness Spec
		symbolCaseToggle.setOptions("#", "?", true);
		symbolCaseToggle.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (symbolCaseToggle.isFistSelected()) {
					randSpec.setVisible(true);
					numPad.setVisible(false);

				} else {
					randSpec.setVisible(false);
					numPad.setVisible(true);
				}
			}
		});

		// Unit Selection
		UnitSelection unitBox = new UnitSelection(true);
		unitPalette.add(unitBox);
		unitBox.addStyleName(CSS.FILL_PARENT);
		unitBox.unitBox.addSelectionHandler(new UnitSelectionHandler());

		// Fill Established Selection
		for (Constant constant : Constant.values()) {
			establishedSelection.add(
					constant.getSymbol() + " - " + constant.getName(), null,
					constant);
		}

		// Handle Established Selection
		establishedSelection.addSelectionHandler(new SelectionHandler() {
			@Override
			public void onSelect(Cell selected) {
				constantSeleced = ((Constant) selected.getEntity());

				symbolDisplay.setText(constantSeleced.getValue());

				setUnit(constantSeleced.getUnitMap());
			}
		});
	}

	@Override
	String extractSymbol() {
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
	void setNode(String symbol) {

		node = node.replace(TypeEquationXML.Number, symbol);

		if (RandomSpecPanel.RANDOM_SYMBOL.equals(symbol)) {
			node.getXMLNode().setAttribute(
					MathAttribute.Randomness.getAttributeName(), randomness);
		} else {
			node.getXMLNode().removeAttribute(
					MathAttribute.Randomness.getAttributeName());
		}

		if (constantSeleced != null
				&& constantSeleced.getValue().equals(symbolDisplay.getText())
				&& constantSeleced.getUnitMap().equals(unitMap)) {
			node.setConstant(constantSeleced);
		}
	}

}
