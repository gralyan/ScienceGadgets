package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.shared.MathAttribute;

class EditSpecHandler implements ClickHandler {

	/**
	 * 
	 */
	private final EditMenu editMenu;
	@SuppressWarnings("rawtypes")
	ValueBoxBase input;
	SelectionPanel quantityOrUnitBox;

	@SuppressWarnings("rawtypes")
	public EditSpecHandler(EditMenu editMenu, ValueBoxBase input, SelectionPanel quantityOrUnitBox) {
		this.editMenu = editMenu;
		this.input = input;
		this.quantityOrUnitBox =quantityOrUnitBox;
	}

	@Override
	public void onClick(ClickEvent event) {
		String extraction = null;

		if (input instanceof DoubleBox) {
			extraction = extractNumber();
		} else if (input instanceof TextBox) {
			extraction = extractVariable();
		}

		if (extraction != null) {

			if (RandomSpecification.RANDOM_SYMBOL.equals(extraction)) {
				String rand = input.getTitle();
				if(!rand.contains(RandomSpecification.DELIMITER)){
					Window.alert("Please specify the randomness");
					return;
				}
				this.editMenu.node.getMLNode().setAttribute(MathAttribute.Randomness.getName(), rand);
			} else {
				this.editMenu.node.getMLNode().removeAttribute(MathAttribute.Randomness.getName());
			}
			this.editMenu.node.setSymbol(extraction);
			
			String quantityOrUnit = quantityOrUnitBox.getSelectedValue();
			if(quantityOrUnit != null && !quantityOrUnit.equals("")){
			this.editMenu.node.getMLNode().setAttribute(MathAttribute.Unit.getName(), quantityOrUnit);
			}
			AlgebraActivity.reloadEquationPanel(null, null);
		}
	}

	private String extractNumber() {
		String inputString = null;
		if (RandomSpecification.RANDOM_SYMBOL.equals(input.getText())) {
			return RandomSpecification.RANDOM_SYMBOL;
		} else {
			Double inputValue = ((DoubleBox) input).getValue();

			if (inputValue == null) {
				input.getElement().getStyle().setBackgroundColor("red");
				Window.alert("The input must be a number\nyou can also change this to a variable if necessary");
			} else {
				input.getElement().getStyle().clearBackgroundColor();
				//no need for trailing 0's
				inputString = String.valueOf(inputValue).replaceAll(
						(String) "\\.0$", "");
				;
			}
			return inputString;
		}
	}

	private String extractVariable() {
		String inputString = ((TextBox) input).getText();

		if (inputString == null || inputString.equals("")) {
			input.getElement().getStyle().setBackgroundColor("red");
			Window.alert("Variable cannot be empty");
			return null;
		}else if(inputString.matches(".*\\d.*")){
			input.getElement().getStyle().setBackgroundColor("red");
			Window.alert("Variable cannot contain numbers");
			return null;
		}else{
			input.getElement().getStyle().clearBackgroundColor();
			return inputString;
		}
	}
}