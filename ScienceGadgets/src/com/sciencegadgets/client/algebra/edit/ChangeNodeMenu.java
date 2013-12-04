package com.sciencegadgets.client.algebra.edit;

import java.util.HashMap;
import java.util.NoSuchElementException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationPanel;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class ChangeNodeMenu extends FlowPanel {

	public static final String NOT_SET = "\u25A1";
	private HashMap<TypeML, Button> menuMap = new HashMap<TypeML, Button>();
	private Button removeButton;

	public ChangeNodeMenu() {

		setSize("100%", "100%");

		TypeML[] types = TypeML.values();

		double buttonWidthPercent = 100.0 / types.length;

		// Change buttons
		for (TypeML type : types) {
			if (TypeML.Operation.equals(type))
				continue;

			ChangeNodeHandler changeHandler = new ChangeNodeHandler(type);
			Button changeButton = new Button(type.toString(), changeHandler);
			changeButton.setSize(buttonWidthPercent + "%", "100%");
			changeButton.setStyleName("changeNodeButton");
			menuMap.put(type, changeButton);
			this.add(changeButton);
		}

		// Remove button
		RemoveNodeHandler removeHandler = new RemoveNodeHandler();
		Button removeButton = new Button("Remove", removeHandler);
		removeButton.setSize(buttonWidthPercent + "%", "100%");
		removeButton.setStyleName("changeNodeButton");
		removeButton.getElement().getStyle().setColor("red");
		this.removeButton = removeButton;
		this.add(removeButton);
	}

	public Button getButton(TypeML menuOption) {
		Button button;

		if (menuOption == null) {
			button = removeButton;
		} else {
			button = menuMap.get(menuOption);
		}
		return button;
	}

	public void setEnable(TypeML menuOption, boolean isEnabled) {
		Button button = getButton(menuOption);
		if (button != null) {
			button.setEnabled(isEnabled);
		}
	}

	public boolean isEnabled(TypeML menuOption) {
		Button button = getButton(menuOption);
		return button.isEnabled();
	}

	private class RemoveNodeHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			if (EquationPanel.selectedWrapper == null) {
				return;
			}

			MathNode node = EquationPanel.selectedWrapper.getNode();
			MathNode parent = node.getParent();

			switch (parent.getType()) {
			case Term:
			case Sum:
				if (node.getIndex() == 0) {
					MathNode nextOp = node.getNextSibling();
					if (nextOp != null
							&& TypeML.Operation.equals(nextOp.getType())
							&& !Operator.MINUS.getSign().equals(
									nextOp.getSymbol()))
						nextOp.remove();
				} else {
					MathNode prevOp = node.getPrevSibling();
					if (TypeML.Operation.equals(prevOp.getType()))
						prevOp.remove();
				}
				node.remove();
				parent.decase();
				AlgebraActivity.reloadEquationPanel(null, null);
				break;
			case Exponential:
			case Fraction:
				MathNode newParent = parent.getParent();

				switch (node.getIndex()) {
				case 0:
					newParent.addBefore(parent.getIndex(),
							node.getNextSibling());
					break;
				case 1:
					newParent.addBefore(parent.getIndex(),
							node.getPrevSibling());
					break;
				}
				parent.remove();
				AlgebraActivity.reloadEquationPanel(null, null);
				break;
			}
		}
	}

	private class ChangeNodeHandler implements ClickHandler {
		TypeML type;

		ChangeNodeHandler(TypeML type) {
			this.type = type;
		}

		@Override
		public void onClick(ClickEvent event) {

			if (EquationPanel.selectedWrapper != null) {
				MathNode node = EquationPanel.selectedWrapper.getNode();
				MathNode parent = node.getParent();
				int index = node.getIndex();

				try {
					TypeML.Operator operator = null;

					switch (type) {
					case Number:
						parent.addBefore(node.getIndex(), type, "1");
						node.remove();
						break;
					case Variable:
						parent.addBefore(node.getIndex(), type, "x");
						node.remove();
						break;

					case Sum:
						operator = TypeML.Operator.PLUS;
					case Term:
						if (operator == null)
							operator = TypeML.Operator.getMultiply();
					case Exponential:
					case Fraction:
						MathNode newNode = Moderator.mathTree
								.NEW_NODE(type, "");

						//Sum, Term, Exponential and Fraction
						newNode.append(node);
						if (operator != null) {
							newNode.addBefore(-1, TypeML.Operation,
									operator.getSign());
						}
						newNode.addBefore(-1, TypeML.Variable, NOT_SET);
						parent.addBefore(index, newNode);

					}
					AlgebraActivity.reloadEquationPanel(null, null);

				} catch (NoSuchElementException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
