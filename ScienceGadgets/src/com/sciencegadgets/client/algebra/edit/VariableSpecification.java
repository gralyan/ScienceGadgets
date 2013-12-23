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
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.client.algebra.MathTree.MathNode;

public class VariableSpecification extends QuantitySpecification {

	private HashSet<SymbolButton> latinButtons = new HashSet<SymbolButton>();
	private HashSet<SymbolButton> greekButtons = new HashSet<SymbolButton>();

	SymbolClickHandler symbolClick = new SymbolClickHandler();
	SymbolTouchHandler symbolTouch = new SymbolTouchHandler();

	public VariableSpecification(MathNode mathNode) {
		super(mathNode);

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
				int latinDiff = isLower ? 'a' - 'A' : 'A' - 'a';
				for (SymbolButton button : latinButtons) {

					int changed = button.getText().toCharArray()[0] + latinDiff;
					button.setText("" + (char) changed);
				}
				int greekDiff = isLower ? 'α' - 'Α' : 'Α' - 'α';
				for (SymbolButton button : greekButtons) {

					int changed = button.getText().toCharArray()[0] + greekDiff;
					button.setText("" + (char) changed);
				}
			}
		});

		// QuantityKind Selection
		UnitSelection quantityBox = new UnitSelection(true, false, false);
		unitSelectionHolder.add(quantityBox);
		quantityBox.addStyleName("fillParent");
		quantityBox.quantityBox.addSelectionHandler(new UnitSelectionHandler());

	}

	class SymbolButton extends Button {
		public SymbolButton(String string) {
			super(string);
			if(Moderator.isTouch) {
				addTouchStartHandler(symbolTouch);
			}else {
				addClickHandler(symbolClick);
			}
			addStyleName("smallestButton");
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
			// symbolDisplay.getElement().getStyle().setBackgroundColor("red");
			Window.alert("Variable cannot be empty");
			return null;
		} else if (inputString.matches(".*\\d.*")) {
			// symbolDisplay.getElement().getStyle().setBackgroundColor("red");
			Window.alert("Variable cannot contain numbers");
			return null;
		} else {
			// symbolDisplay.getElement().getStyle().clearBackgroundColor();
			return inputString;
		}
	}

	@Override
	void setSymbol(String symbol) {
		node.setSymbol(symbol);
	}
}
