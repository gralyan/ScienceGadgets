package com.sciencegadgets.client.algebra.edit;

import java.util.HashSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.UnitSelection;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.dimensions.CommonDerivedUnits;
import com.sciencegadgets.shared.dimensions.CommonVariables;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitMap;

public class VariableSpecification extends QuantitySpecification {

	private HashSet<SymbolButton> latinButtons = new HashSet<SymbolButton>();
	private HashSet<SymbolButton> greekButtons = new HashSet<SymbolButton>();

	SymbolClickHandler symbolClick = new SymbolClickHandler();
	SymbolTouchHandler symbolTouch = new SymbolTouchHandler();

	public VariableSpecification(EquationNode mathNode) {
		super(mathNode, true);
	}
	public VariableSpecification(EquationNode mathNode, boolean clearDisplays) {
		super(mathNode, clearDisplays);

		// Symbol Selection
		symbolPalette.add(new Label("Latin"));
		for (char i = 0; i < 26; i++) {
			SymbolButton button = new SymbolButton((char) ('a' + i) + "");
			latinButtons.add(button);
			symbolPalette.add(button);
		}
		symbolPalette.add(new Label("Greek"));
		for (char i = 0; i < 25; i++) {
			if (i != 17) {
				SymbolButton button = new SymbolButton((char) ('α' + i) + "");
				greekButtons.add(button);
				symbolPalette.add(button);
			}
		}

		symbolCaseToggle.setOptions("a", "A", true);
		symbolCaseToggle.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean isLower = symbolCaseToggle.isFistSelected();
				int latinDiff = isLower ? 'A' - 'a' : 'a' - 'A';
				for (SymbolButton button : latinButtons) {

					int changed = button.getText().toCharArray()[0] + latinDiff;
					button.setText("" + (char) changed);
				}
				int greekDiff = isLower ?  'Α' - 'α': 'α' - 'Α';
				for (SymbolButton button : greekButtons) {

					int changed = button.getText().toCharArray()[0] + greekDiff;
					button.setText("" + (char) changed);
				}
			}
		});

		// QuantityKind Selection
		UnitSelection quantityBox = new UnitSelection(true, false, false);
		unitPalette.add(quantityBox);
		quantityBox.addStyleName(CSS.FILL_PARENT);
		quantityBox.quantityBox.addSelectionHandler(new UnitSelectionHandler());
		
		// Established Selection
		for (CommonVariables var : CommonVariables.values()) {
			establishedSelection.add(
					var.getSymbol() + " - "+var.toString(), null,
					var);
		}
		// Handle Established Selection
		establishedSelection.addSelectionHandler(new SelectionHandler() {
			@Override
			public void onSelect(Cell selected) {
				CommonVariables establishedVariable = ((CommonVariables) selected
						.getEntity());

				symbolDisplay.setText(establishedVariable.getSymbol());

				setUnit(new UnitMap(establishedVariable.getUnitAttribute()));
			}
		});

	}

	class SymbolButton extends Button {
		public SymbolButton(String string) {
			super(string);
			if(Moderator.isTouch) {
				addTouchStartHandler(symbolTouch);
			}else {
				addClickHandler(symbolClick);
			}
			addStyleName(CSS.SMALLEST_BUTTON);
		}
	}

	private class SymbolClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			String oldText = symbolDisplay.getText();
			symbolDisplay.setText(oldText
					+ ((Button) event.getSource()).getText());
		}
	}
	private class SymbolTouchHandler implements TouchStartHandler {
		@Override
		public void onTouchStart(TouchStartEvent event) {
			String oldText = symbolDisplay.getText();
			symbolDisplay.setText(oldText
					+ ((Button) event.getSource()).getText());
		}
	}

	String extractSymbol() {
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
	void setNode(String symbol) {
		node = node.replace(TypeEquationXML.Variable, symbol);
	}
}
