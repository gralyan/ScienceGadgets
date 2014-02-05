package com.sciencegadgets.client.algebra.edit;

import java.math.BigDecimal;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.KeyPadNumerical;
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;

public class NumberSpecification extends QuantitySpecification {

	String randomness = "";
	RandomSpecPanel randSpec = new RandomSpecPanel();
	KeyPadNumerical numPad;

	public NumberSpecification(MathNode mathNode, boolean clearDisplays) {
		super(mathNode, clearDisplays);

		//Number Pad
		numPad = new KeyPadNumerical(symbolDisplay);
		symbolPalette.add(numPad);
		
		//Randomness Spec
		symbolPalette.add(randSpec);
		randSpec.setVisible(false);
		randSpec.addOkClickHandler((new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				randomness = randSpec.getRandomness();
				if (randomness != null) {
					symbolDisplay.setText(RandomSpecPanel.RANDOM_SYMBOL);
				}else {
					symbolDisplay.setText("");
				}
			}
		}));

		//Symbol Toggle - switch Number Pad and Randomness Spec
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
		unitSelectionHolder.add(unitBox);
		unitBox.addStyleName(CSS.FILL_PARENT);
		unitBox.unitBox.addSelectionHandler(new UnitSelectionHandler());

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
		
		node.replace(TypeML.Number, symbol);

		if (RandomSpecPanel.RANDOM_SYMBOL.equals(symbol)) {
			node.getMLNode().setAttribute(MathAttribute.Randomness.getName(),
					randomness);
		} else {
			node.getMLNode()
					.removeAttribute(MathAttribute.Randomness.getName());
		}

	}

}
