package com.sciencegadgets.client.algebra.edit;

import java.util.HashMap;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.Activity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type;

public class EditMenu extends FlowPanel {

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

		this.editWrapper = editWrapper;
		this.node = editWrapper.getNode();

		// zIndex eqPanel=1 wrapper=2 menu=3
		this.getElement().getStyle().setZIndex(3);

		switch (node.getType()) {
		case Sum:
			Button extendSum = new Button("+" + ChangeNodeMenu.NOT_SET,
					new ExtendSumTermHandler());
			extendSum.setTitle(SUM_EXTEND);
			this.place(extendSum);
			break;
		case Term:
			Button extendTerm = new Button("x" + ChangeNodeMenu.NOT_SET,
					new ExtendSumTermHandler());
			extendTerm.setTitle(TERM_EXTEND);
			this.place(extendTerm);
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
			variableInput.addClickHandler(new FocusOnlyClickHandler());
			variableInput.addTouchStartHandler(new FocusOnlyTouchHandler());
			variableInput.setText(node.getSymbol());
			variableInput.setTitle(VARIABLE_INPUT);
			focusable = variableInput;
			this.place(variableInput);

			Button insertSymbolButton = new Button("α",
					new InsertSymbolHandler());
			insertSymbolButton.setWidth(width);
			insertSymbolButton.setTitle(VARIABLE_INSERT);
			this.place(insertSymbolButton);
			break;
		case Number:
			DoubleBox numberInput = new DoubleBox();
			numberInput.addChangeHandler(new InputChangeHandler(numberInput));
			numberInput.setWidth(width);
			numberInput.addClickHandler(new FocusOnlyClickHandler());
			numberInput.addTouchStartHandler(new FocusOnlyTouchHandler());
			numberInput.setText(node.getSymbol());
			numberInput.setTitle(NUMBER_INPUT);
			focusable = numberInput;
			this.place(numberInput);

			Button randomNumberButton = new Button("random",
					new RandomNumberHandler());
			randomNumberButton.setWidth(width);
			randomNumberButton.setTitle(NUMBER_RANDOM_SPEC);
			this.place(randomNumberButton);
			break;
		case Operation:
			HashMap<Type.Operator, Boolean> opMap = new HashMap<Type.Operator, Boolean>();
			Type.Operator operation = node.getOperation();
			if (operation == null) {
				break;
			}

			operationMenu: switch (operation) {
			case CROSS:
				opMap.put(Type.Operator.CROSS, false);
				opMap.put(Type.Operator.DOT, true);
				opMap.put(Type.Operator.SPACE, true);
				break operationMenu;
			case DOT:
				opMap.put(Type.Operator.CROSS, true);
				opMap.put(Type.Operator.DOT, false);
				opMap.put(Type.Operator.SPACE, true);
				break operationMenu;
			case SPACE:
				opMap.put(Type.Operator.CROSS, true);
				opMap.put(Type.Operator.DOT, true);
				opMap.put(Type.Operator.SPACE, false);
				break operationMenu;
			case MINUS:
				opMap.put(Type.Operator.PLUS, true);
				opMap.put(Type.Operator.MINUS, false);
				break operationMenu;
			case PLUS:
				opMap.put(Type.Operator.PLUS, false);
				opMap.put(Type.Operator.MINUS, true);
				break operationMenu;
			}
			for (Type.Operator op : opMap.keySet()) {
				Button signButton = new Button(op.getSign());
				if (opMap.get(op)) {
					signButton.addClickHandler(new SignChangeHandler(op));
				} else {
					signButton.setVisible(false);
				}
				this.place(signButton);
				signButton.setTitle(OPERATION_CHANGE);
			}
			break;
		}
	}

	public void setFocus() {
		if (focusable != null) {
			focusable.setFocus(true);
		}
	}

	public void place(Widget w) {
		w.setHeight("100%");
		w.setWidth("100%");
		FlowPanel container = new FlowPanel();
		this.add(container);
		container.add(w);

		int count = this.getWidgetCount();
		int height = 100 / count;
		for (int i = 0; i < count; i++) {
			getWidget(i).setHeight(height + "%");
			getWidget(i).setWidth("100%");
		}
	}

	public void setResponse(Widget responseNotes) {
		this.responseNotes = responseNotes;
		this.place(responseNotes);
		this.setFocus();
	}

	public Widget getResponse() {
		return responseNotes;
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
				node.getMLNode().removeAttribute("data-randomness");

				Moderator.reloadEquationPanel(null, null);
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

		Type.Operator operator;

		SignChangeHandler(Type.Operator operator) {
			this.operator = operator;
		}

		@Override
		public void onClick(ClickEvent event) {
			node.setSymbol(operator.getSign());

			Moderator.reloadEquationPanel(null, null);
		}

	}

	private class InsertSymbolHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			event.preventDefault();
			event.stopPropagation();

			SymbolPalette symbolPopup;
			if (Moderator.symbolPopup == null) {
				symbolPopup = new SymbolPalette(node);
//				AbsolutePanel mainPanel = editWrapper.getEqPanel();
				AbsolutePanel mainPanel = Moderator.scienceGadgetArea;

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

			Moderator.setActivity(Activity.insert_symbol);
			symbolPopup.show();
		}
	}

	private class RandomNumberHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {

			RandomSpecification randomSpec;
			if (Moderator.randomSpec == null) {
				randomSpec = new RandomSpecification(node);
//				AbsolutePanel mainPanel = editWrapper.getEqPanel();
				AbsolutePanel mainPanel = Moderator.scienceGadgetArea;

				randomSpec.setPixelSize(mainPanel.getOffsetWidth(),
						mainPanel.getOffsetHeight());
				randomSpec.setPopupPosition(mainPanel.getAbsoluteLeft(),
						mainPanel.getAbsoluteTop());
				randomSpec.getElement().getStyle().setZIndex(10);

				Moderator.randomSpec = randomSpec;
			} else {
				randomSpec = Moderator.randomSpec;
				randomSpec.setNode(node);
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
				node.append(Type.Operation, "+");
				break;
			case Term:
				node.append(Type.Operation, Type.Operator.getMultiply().getSign());
				break;
			}
			node.append(Type.Variable, ChangeNodeMenu.NOT_SET);
			Moderator.reloadEquationPanel(null, null);
		}

	}
}
