package com.sciencegadgets.client.equationtree;

import java.util.NoSuchElementException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.algebramanipulation.Moderator;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Operators;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class ChangeNodeMenu extends VerticalPanel {

	public EquationList eqList;
	public static final String NOT_SET = "???";

	public ChangeNodeMenu(EquationList eqList) {
		this.eqList = eqList;

		// Make this menu a fraction of the screens width, same height
		int width = eqList.mainPanel.getOffsetWidth() / 10;
		int height = eqList.mainPanel.getOffsetHeight();

		Type[] types = MathMLBindingTree.Type.values();
		int buttonHeight = height / (types.length);

		// Change buttons
		for (MathMLBindingTree.Type type : types) {
			if (Type.Operation.equals(type))
				continue;

			ChangeNodeHandler changeHandler = new ChangeNodeHandler(type);
			Button changeButton = new Button(type.toString(), changeHandler);
			changeButton.setHeight(buttonHeight + "px");
			changeButton.setWidth(width + "px");
			this.add(changeButton);
		}

		// Remove button
		RemoveNodeHandler removeHandler = new RemoveNodeHandler();
		Button removeButton = new Button("Remove", removeHandler);
		removeButton.setHeight(buttonHeight + "px");
		removeButton.setWidth(width + "px");
		removeButton.setStyleName("removeNodeButton");
		this.add(removeButton);
	}

	private class RemoveNodeHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			EditWrapper selectedWrapper = EditWrapper.selectedWrapper;

			if (selectedWrapper != null) {
				MathMLBindingNode node = selectedWrapper.getNode();
				MathMLBindingNode parent = node.getParent();

				switch (parent.getType()) {
				case Term:
				case Sum:
					if (parent.getChildCount() > 3) {
						node.remove();
						try {
							MathMLBindingNode prevSib = node.getPrevSibling();
							if (Type.Operation.equals(prevSib.getTag()))
								prevSib.remove();
						} catch (IndexOutOfBoundsException e) {
						}
						Moderator.reload("");
					} else {
						Label display = new Label(
								"You should just change the parent");
						selectedWrapper.getEditMenu().setDisplay(display);
					}
					break;
				case Exponential:
				case Fraction:
					switch(node.getIndex()){
					case 0:
						node.getNextSibling().remove();
					case 1:
						node.getPrevSibling().remove();
					}

					MathMLBindingNode newParent = parent.getParent();
					newParent.add(parent.getIndex(), node);
					parent.remove();
					Moderator.reload("");
					break;
				}
			}
		}

	}

	private class ChangeNodeHandler implements ClickHandler {
		MathMLBindingTree.Type type;

		ChangeNodeHandler(MathMLBindingTree.Type type) {
			this.type = type;
		}

		@Override
		public void onClick(ClickEvent event) {
			EditWrapper selectedWrapper = EditWrapper.selectedWrapper;

			if (selectedWrapper != null) {
				MathMLBindingNode node = selectedWrapper.getNode();
				MathMLBindingNode parent = node.getParent();

				Operators operator = null;
				try {
					switch (type) {
					case Number:
						parent.add(node.getIndex(), type, "1");
						break;
					case Variable:
						parent.add(node.getIndex(), type, "x");
						break;

					case Sum:
						operator = Operators.PLUS;
					case Term:
						if (operator == null)
							operator = Operators.getMultiply();
					case Exponential:
					case Fraction:
						MathMLBindingNode newNode = Moderator.jTree.NEW_NODE(
								type, "");
						newNode.add(-1, Type.Variable, node.getSymbol());
						if (operator != null)
							newNode.add(-1, Type.Operation, operator.getSign());
						newNode.add(-1, Type.Variable, NOT_SET);
						parent.add(node.getIndex(), newNode);
					}
					node.remove();

					Moderator.reload("");
				} catch (NoSuchElementException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
