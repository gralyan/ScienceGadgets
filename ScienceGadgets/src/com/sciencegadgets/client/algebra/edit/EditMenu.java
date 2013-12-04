package com.sciencegadgets.client.algebra.edit;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.CommunistPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.Activity;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;

public class EditMenu extends CommunistPanel {

	EditWrapper editWrapper;
	MathNode node;
	Focusable focusable = null;
	Widget responseNotes = null;

	private final String SUM_EXTEND = "Add another term";
	private final String TERM_EXTEND = "Multiply by another term";
	private final String NUMBER_INPUT = "Set Constant";
	private final String NUMBER_RANDOM_SPEC = "Set Random Constant";
	private final String VARIABLE_INPUT = "Set Variable Symbol";
	private final String VARIABLE_INSERT = "Special Symbols";
	private final String OPERATION_CHANGE = "Change Sign";

	public EditMenu(EditWrapper editWrapper, String width) {
		super(true);

		this.editWrapper = editWrapper;
		this.node = editWrapper.getNode();
		
		this.setSize("100%", "100%");

		// zIndex eqPanel=1 wrapper=2 menu=3
		this.getElement().getStyle().setZIndex(3);
		
		ArrayList<Widget> components = new ArrayList<Widget>();

		switch (node.getType()) {
		case Sum:
			Button extendSum = new Button("+" + ChangeNodeMenu.NOT_SET,
					new ExtendSumTermHandler());
			extendSum.setTitle(SUM_EXTEND);
			components.add(extendSum);
			break;
		case Term:
			Button extendTerm = new Button("x" + ChangeNodeMenu.NOT_SET,
					new ExtendSumTermHandler());
			extendTerm.setTitle(TERM_EXTEND);
			components.add(extendTerm);
			break;
		case Fraction:
			break;
		case Exponential:
			break;
		case Variable:
			//1st Component - symbol
			CommunistPanel symbolComponent = new CommunistPanel(false);
			components.add(symbolComponent);
			
			TextBox symbolInput = new TextBox();
			symbolInput.setWidth(width);
			symbolInput.addClickHandler(new FocusOnlyClickHandler());
			symbolInput.addTouchStartHandler(new FocusOnlyTouchHandler());
			symbolInput.setText(node.getSymbol());
			symbolInput.setTitle(VARIABLE_INPUT);
			focusable = symbolInput;
			symbolComponent.add(symbolInput);

			Button insertSymbolButton = new Button("Symbols",
					new InsertSymbolHandler(symbolInput));
			insertSymbolButton.setWidth(width);
			insertSymbolButton.setTitle(VARIABLE_INSERT);
			symbolComponent.add(insertSymbolButton);

			//2nd Component - QuantityKind
			UnitSelection quantityBox = new UnitSelection(true);
			components.add(quantityBox);

			//3rd Component - Use button
			Button varChangeButton = new Button("Use", new UseHandler(
					symbolInput, quantityBox.quantityBox));
			components.add(varChangeButton);
			break;
		case Number:
			//1st Component - number
			CommunistPanel numberComponent = new CommunistPanel(false);
			components.add(numberComponent);
			
			DoubleBox numberInput = new DoubleBox();
			numberInput.setWidth(width);
			numberInput.addClickHandler(new FocusOnlyClickHandler());
			numberInput.addTouchStartHandler(new FocusOnlyTouchHandler());
			numberInput.setText(node.getSymbol());
			numberInput.setTitle(NUMBER_INPUT);
			focusable = numberInput;
			numberComponent.add(numberInput);

			Button randomNumberButton = new Button("random",
					new RandomNumberHandler(numberInput));
			randomNumberButton.setWidth(width);
			randomNumberButton.setTitle(NUMBER_RANDOM_SPEC);
			numberComponent.add(randomNumberButton);

			//2nd Component - Unit
			UnitSelection unitComponent = new UnitSelection();
			components.add(unitComponent);

			//3rd Component - Use Button
			Button numChangeButton = new Button("Use", new UseHandler(
					numberInput, unitComponent.unitBox));
			components.add(numChangeButton);
			break;
		case Operation:
			HashMap<TypeML.Operator, Boolean> opMap = new HashMap<TypeML.Operator, Boolean>();
			TypeML.Operator operation = node.getOperation();
			if (operation == null) {
				break;
			}

			operationMenu: switch (operation) {
			case CROSS:
				opMap.put(TypeML.Operator.CROSS, false);
				opMap.put(TypeML.Operator.DOT, true);
				opMap.put(TypeML.Operator.SPACE, true);
				break operationMenu;
			case DOT:
				opMap.put(TypeML.Operator.CROSS, true);
				opMap.put(TypeML.Operator.DOT, false);
				opMap.put(TypeML.Operator.SPACE, true);
				break operationMenu;
			case SPACE:
				opMap.put(TypeML.Operator.CROSS, true);
				opMap.put(TypeML.Operator.DOT, true);
				opMap.put(TypeML.Operator.SPACE, false);
				break operationMenu;
			case MINUS:
				opMap.put(TypeML.Operator.PLUS, true);
				opMap.put(TypeML.Operator.MINUS, false);
				break operationMenu;
			case PLUS:
				opMap.put(TypeML.Operator.PLUS, false);
				opMap.put(TypeML.Operator.MINUS, true);
				break operationMenu;
			}
			for (TypeML.Operator op : opMap.keySet()) {
				Button signButton = new Button(op.getSign());
				if (opMap.get(op)) {
					signButton.addClickHandler(new SignChangeHandler(op));
				} else {
					signButton.setEnabled(false);
				}
				signButton.setTitle(OPERATION_CHANGE);
				components.add(signButton);
			}
			break;
		}
		System.out.println(components.size()+" "+node.toString());
		if(components.size()>0){
		this.add(components.toArray(new Widget[components.size()]));
		}
	}

	public void setFocus() {
		if (focusable != null) {
			focusable.setFocus(true);
		}
	}

	public void setResponse(Widget responseNotes) {
		this.responseNotes = responseNotes;
		this.add(responseNotes);
		this.setFocus();
	}

	public Widget getResponse() {
		return responseNotes;
	}

	@SuppressWarnings("rawtypes")
	private class UseHandler implements ClickHandler {

		ValueBoxBase input;
		SelectionPanel quantityOrUnitBox;

		public UseHandler(ValueBoxBase input, SelectionPanel quantityOrUnitBox) {
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
					node.getMLNode().setAttribute(MathAttribute.Randomness.getName(), rand);
				} else {
					node.getMLNode().removeAttribute(MathAttribute.Randomness.getName());
				}
				node.setSymbol(extraction);
				
				String quantityOrUnit = quantityOrUnitBox.getSelectedValue();
				if(quantityOrUnit != null && !quantityOrUnit.equals("")){
				node.getMLNode().setAttribute(MathAttribute.Unit.getName(), quantityOrUnit);
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
			}else if(inputString.matches(".*\\d.*")){
				input.getElement().getStyle().setBackgroundColor("red");
				Window.alert("Variable cannot contain numbers");
			}else{
				input.getElement().getStyle().clearBackgroundColor();
			}
			return inputString;
		}
	}

	private class FocusOnlyClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			event.preventDefault();
			event.stopPropagation();
		}
	}

	private class FocusOnlyTouchHandler implements TouchStartHandler {
		@Override
		public void onTouchStart(TouchStartEvent event) {
			event.preventDefault();
			event.stopPropagation();
		}
	}

	private class SignChangeHandler implements ClickHandler {

		TypeML.Operator operator;

		SignChangeHandler(TypeML.Operator operator) {
			this.operator = operator;
		}

		@Override
		public void onClick(ClickEvent event) {
			node.setSymbol(operator.getSign());

//			Moderator.reloadEquationPanel(null, null);
		}

	}

	private class InsertSymbolHandler implements ClickHandler {

		TextBox symbolInput;

		public InsertSymbolHandler(TextBox symbolInput) {
			this.symbolInput = symbolInput;
		}

		@Override
		public void onClick(ClickEvent event) {
			event.preventDefault();
			event.stopPropagation();

			SymbolPalette symbolPopup;
			if (Moderator.symbolPopup == null) {
				symbolPopup = new SymbolPalette(node, symbolInput);
				AbsolutePanel mainPanel = Moderator.scienceGadgetArea;

				symbolPopup.setPixelSize(mainPanel.getOffsetWidth(),
						mainPanel.getOffsetHeight());
				symbolPopup.setPopupPosition(mainPanel.getAbsoluteLeft(),
						mainPanel.getAbsoluteTop());
				symbolPopup.getElement().getStyle().setZIndex(10);

				Moderator.symbolPopup = symbolPopup;
			} else {
				symbolPopup = Moderator.symbolPopup;
				symbolPopup.setContext(node, symbolInput);
			}

			Moderator.setActivity(Activity.insert_symbol);
			symbolPopup.show();
		}
	}

	private class RandomNumberHandler implements ClickHandler {

		DoubleBox numberInput;

		public RandomNumberHandler(DoubleBox numberInput) {
			this.numberInput = numberInput;
		}

		@Override
		public void onClick(ClickEvent event) {

			RandomSpecification randomSpec;
			if (Moderator.randomSpec == null) {
				randomSpec = new RandomSpecification(node, numberInput);
				AbsolutePanel mainPanel = Moderator.scienceGadgetArea;

				randomSpec.setPixelSize(mainPanel.getOffsetWidth(),
						mainPanel.getOffsetHeight());
				randomSpec.setPopupPosition(mainPanel.getAbsoluteLeft(),
						mainPanel.getAbsoluteTop());
				randomSpec.getElement().getStyle().setZIndex(10);

				Moderator.randomSpec = randomSpec;
			} else {
				randomSpec = Moderator.randomSpec;
				randomSpec.setContext(node, numberInput);
			}

			Moderator.setActivity(Activity.random_spec);
			randomSpec.show();
		}
	}

	private class ExtendSumTermHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			switch (node.getType()) {
			case Sum:
				node.append(TypeML.Operation, "+");
				break;
			case Term:
				node.append(TypeML.Operation, TypeML.Operator.getMultiply()
						.getSign());
				break;
			}
			node.append(TypeML.Variable, ChangeNodeMenu.NOT_SET);
			AlgebraActivity.reloadEquationPanel(null, null);
		}

	}
}
