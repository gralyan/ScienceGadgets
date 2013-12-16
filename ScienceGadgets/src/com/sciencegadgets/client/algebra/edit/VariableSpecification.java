package com.sciencegadgets.client.algebra.edit;

import java.util.HashSet;
import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.client.ToggleSlide;
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.edit.QuantitySpecification.UnitSelectionHandler;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.UnitUtil;

public class VariableSpecification extends QuantitySpecification {

	private HashSet<SymbolButton> latinButtons = new HashSet<SymbolButton>();
	private HashSet<SymbolButton> greekButtons = new HashSet<SymbolButton>();

	SymbolClickHandler symbolClick = new SymbolClickHandler();
	
	public VariableSpecification(EditMenu editMenu) {
		super(editMenu);

		// Symbol Selection
		symbolPalette.add(new Label("Latin"));
		for (char i = 0; i < 26; i++) {
			SymbolButton button = new SymbolButton((char) ('a' + i) + "",
					symbolClick);
			latinButtons.add(button);
			symbolPalette.add(button);
		}
		symbolPalette.add(new Label("Greek"));
		for (char i = 0; i < 25; i++) {
			if (i != 17) {
				SymbolButton button = new SymbolButton((char) ('α' + i) + "",
						symbolClick);
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
		UnitSelection quantityBox = new UnitSelection(true, false);
		unitSelectionHolder.add(quantityBox);
		quantityBox.addStyleName("fillParent");
		quantityBox.quantityBox.addSelectionHandler(new UnitSelectionHandler());

	}

	class SymbolButton extends Button {
		public SymbolButton(String string, SymbolClickHandler handler) {
			super(string, handler);
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
