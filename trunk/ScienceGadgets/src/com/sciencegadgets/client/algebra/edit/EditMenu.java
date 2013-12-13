package com.sciencegadgets.client.algebra.edit;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.CommunistPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.Activity;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
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
	private final String OPERATION_CHANGE = "Change Sign";

	public EditMenu(EditWrapper editWrapper) {
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
			Button varSpec = new Button();
			varSpec.setHTML("spec");
			final EditMenu editMenu = this;
			varSpec.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Prompt varPrompt = new VariableSpecification(editMenu);
					varPrompt.appear();
				}
			});
			components.add(varSpec);
			break;
		case Number:
			//1st Component - number
			CommunistPanel numberComponent = new CommunistPanel(false);
			components.add(numberComponent);
			
			DoubleBox numberInput = new DoubleBox();
//			numberInput.addClickHandler(new FocusOnlyClickHandler());
//			numberInput.addTouchStartHandler(new FocusOnlyTouchHandler());
			numberInput.setText(node.getSymbol());
			numberInput.setTitle(NUMBER_INPUT);
			focusable = numberInput;
			numberComponent.add(numberInput);

			Button randomNumberButton = new Button("Random",
					new RandomNumberHandler(numberInput));
			randomNumberButton.setTitle(NUMBER_RANDOM_SPEC);
			numberComponent.add(randomNumberButton);

			//2nd Component - Unit
			UnitSelection unitComponent = new UnitSelection();
			components.add(unitComponent);

			//3rd Component - Use Button
//			Button numChangeButton = new Button("Use", new UseHandler(
//					numberInput, unitComponent.unitBox));
//			components.add(numChangeButton);
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
