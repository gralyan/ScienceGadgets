package com.sciencegadgets.client.algebra.edit;

import java.util.HashMap;
import java.util.NoSuchElementException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationPanel;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type;
import com.sciencegadgets.client.algebra.Type.Operator;

public class ChangeNodeMenu extends FlowPanel {

	public static final String NOT_SET = "\u25A1";
	private HashMap<Type, Button> menuMap = new HashMap<Type, Button>();
	private Button removeButton;

	public ChangeNodeMenu() {

		setSize("100%", "100%");

		Type[] types = Type.values();

		double buttonWidthPercent = 100.0 / types.length;

		// Change buttons
		for (Type type : types) {
			if (Type.Operation.equals(type))
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

	public Button getButton(Type menuOption) {
		Button button;

		if (menuOption == null) {
			button = removeButton;
		} else {
			button = menuMap.get(menuOption);
		}
		return button;
	}

	public void setEnable(Type menuOption, boolean isEnabled) {
		Button button = getButton(menuOption);
		if (button != null) {
			button.setEnabled(isEnabled);
		}
	}

	public boolean isEnabled(Type menuOption) {
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
							&& Type.Operation.equals(nextOp.getType())
							&& !Operator.MINUS.getSign().equals(
									nextOp.getSymbol()))
						nextOp.remove();
				} else {
					MathNode prevOp = node.getPrevSibling();
					if (Type.Operation.equals(prevOp.getType()))
						prevOp.remove();
				}
				node.remove();
				parent.decase();
				Moderator.reloadEquationPanel(null, null);
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
				Moderator.reloadEquationPanel(null, null);
				break;
			}
		}
	}

	private class ChangeNodeHandler implements ClickHandler {
		Type type;

		ChangeNodeHandler(Type type) {
			this.type = type;
		}

		@Override
		public void onClick(ClickEvent event) {

			if (EquationPanel.selectedWrapper != null) {
				MathNode node = EquationPanel.selectedWrapper.getNode();
				MathNode parent = node.getParent();
				int index = node.getIndex();

				try {
					Type.Operator operator = null;

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
						operator = Type.Operator.PLUS;
					case Term:
						if (operator == null)
							operator = Type.Operator.getMultiply();
					case Exponential:
					case Fraction:
						MathNode newNode = Moderator.mathTree
								.NEW_NODE(type, "");

						newNode.append(node);
						if (operator != null) {
							newNode.addBefore(-1, Type.Operation,
									operator.getSign());
						}
						newNode.addBefore(-1, Type.Variable, NOT_SET);
						parent.addBefore(index, newNode);

					}
					Moderator.reloadEquationPanel(null, null);

				} catch (NoSuchElementException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
