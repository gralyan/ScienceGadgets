package com.sciencegadgets.client.equationtree;

import java.util.HashMap;
import java.math.BigDecimal;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.algebramanipulation.Moderator;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Operator;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class EditMenu extends HTMLPanel {

	EditWrapper editWrapper;
	MathMLBindingNode node;
	Focusable focusable = null;
	Widget display = null;

	public EditMenu(EditWrapper editWrapper, String width) {
		super("");

		this.editWrapper = editWrapper;
		this.node = editWrapper.getNode();

		switch (node.getType()) {
		case Sum:
		case Term:
			Button extendSumTerm = new Button("...",new ExtendSumTermHandler());
			this.add(extendSumTerm);
			break;
		case Fraction:
			break;
		case Exponential:
			break;
		case Variable:
			TextBox variableInput = new TextBox();
			variableInput
					.addChangeHandler(new InputChangeHandler(variableInput));
			variableInput.setWidth(width);
			variableInput.setText(node.getSymbol());
			focusable = variableInput;
			this.add(variableInput);

			Button insertSymbolButton = new Button("α",
					new InsertSymbolHandler());
			this.add(insertSymbolButton);
			break;
		case Number:
			DoubleBox numberInput = new DoubleBox();
			numberInput.addChangeHandler(new InputChangeHandler(numberInput));
			numberInput.setText(node.getSymbol());
			focusable = numberInput;
			this.add(numberInput);
			break;
		case Operation:
			HashMap<Operator, Boolean> opMap = new HashMap<Operator, Boolean>();
			Operator operation = node.getOperation();
			if (operation == null) {
				break;
			}

			operationMenu: switch (operation) {
			case CROSS:
				opMap.put(Operator.CROSS, false);
				opMap.put(Operator.DOT, true);
				opMap.put(Operator.SPACE, true);
				break operationMenu;
			case DOT:
				opMap.put(Operator.CROSS, true);
				opMap.put(Operator.DOT, false);
				opMap.put(Operator.SPACE, true);
				break operationMenu;
			case SPACE:
				opMap.put(Operator.CROSS, true);
				opMap.put(Operator.DOT, true);
				opMap.put(Operator.SPACE, false);
				break operationMenu;
			case MINUS:
				opMap.put(Operator.PLUS, true);
				opMap.put(Operator.MINUS, false);
				break operationMenu;
			case PLUS:
				opMap.put(Operator.PLUS, false);
				opMap.put(Operator.MINUS, true);
				break operationMenu;
			}
			for (Operator op : opMap.keySet()) {
				Button signButton = new Button(op.getSign());
				if (opMap.get(op)) {
					signButton.addClickHandler(new SignChangeHandler(op));
				} else {
					signButton.setEnabled(false);
				}
				this.add(signButton);
			}
			break;
		}
	}

	public void setFocus() {
		if (focusable != null) {
			focusable.setFocus(true);
		}
	}

	public void setDisplay(Widget display) {
		this.display = display;
		this.add(display);
		this.setFocus();
	}

	public Widget getDisplay() {
		return display;
	}

	private class InputChangeHandler implements ChangeHandler {

		FocusWidget input;

		public InputChangeHandler(FocusWidget input) {
			this.input = input;
		}

		@Override
		public void onChange(ChangeEvent event) {
			String extraction = null;

			if (input instanceof DoubleBox) {
				extraction = extractNumber();
			} else if (input instanceof TextBox) {
				extraction = extractVariable();
			}

			if (extraction != null) {
				node.setSymbol(extraction);
				HTML mathML = new HTML();
				mathML.getElement().appendChild(node.getTree().getMathML());

				Moderator.reload("");
			}
		}

		private String extractNumber() {
			String inputString = null;
			Double inputValue = ((DoubleBox) input).getValue();

			if (inputValue == null) {
				Window.alert("The input must be a number\nchange this to a variable node if you want a variable");
			} else {
				inputString = String.valueOf(inputValue).replaceAll(
						(String) "\\.0$", "");
				;
			}
			return inputString;
		}

		private String extractVariable() {
			String inputString = ((TextBox) input).getText();

			if (inputString == null) {
				Window.alert("That's some crazy input");
			}
			return inputString;
		}
	}

	private class SignChangeHandler implements ClickHandler {

		Operator operator;

		SignChangeHandler(Operator operator) {
			this.operator = operator;
		}

		@Override
		public void onClick(ClickEvent event) {
			node.setSymbol(operator.getSign());

			Moderator.reload("");
		}

	}

	private class InsertSymbolHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {

			SymbolPalette symbolPopup;
			if (Moderator.symbolPopup == null) {
				symbolPopup = new SymbolPalette(node);
				AbsolutePanel mainPanel = editWrapper.eqList.mainPanel;

				symbolPopup.setPixelSize(mainPanel.getOffsetWidth(),
						mainPanel.getOffsetHeight());
				symbolPopup.setPopupPosition(mainPanel.getAbsoluteLeft(),
						mainPanel.getAbsoluteTop());
				symbolPopup.getElement().getStyle().setZIndex(10);
				
				Moderator.symbolPopup = symbolPopup;
			} else {
				symbolPopup = Moderator.symbolPopup;
				symbolPopup.setNode(node);
			}

			symbolPopup.show();
		}
	}

	private class ExtendSumTermHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			switch(node.getType()){
			case Sum:
				node.add(Type.Operation, "+");
				break;
			case Term:
				node.add(Type.Operation, Operator.getMultiply().getSign());
				break;
			}
			node.add(Type.Variable, ChangeNodeMenu.NOT_SET);
			Moderator.reload("");
		}
		
	}
}
