package com.sciencegadgets.client.equationtree;

import java.util.HashMap;
import java.util.NoSuchElementException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.algebramanipulation.Moderator;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Operator;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class ChangeNodeMenu extends VerticalPanel {

	public static final String NOT_SET = "???";
	public EquationList eqList;
	private HashMap<Type, Button> menuMap = new HashMap<Type, Button>();
	private Button removeButton;

	public ChangeNodeMenu(EquationList eqList) {
		this.setVisible(false);
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
			menuMap.put(type, changeButton);
			this.add(changeButton);
		}

		// Remove button
		RemoveNodeHandler removeHandler = new RemoveNodeHandler();
		Button removeButton = new Button("Remove", removeHandler);
		removeButton.setHeight(buttonHeight + "px");
		removeButton.setWidth(width + "px");
		removeButton.setStyleName("removeNodeButton");
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
		if(button != null){
		button.setEnabled(isEnabled);
	}}

	public boolean isEnabled(Type menuOption) {
		Button button = getButton(menuOption);
		return button.isEnabled();
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
					if (parent.getChildCount() > 4) {
						try {
							MathMLBindingNode prevSib = node.getPrevSibling();
							if (Type.Operation.equals(prevSib.getTag()))
								prevSib.remove();
						} catch (IndexOutOfBoundsException e) {
						}
						node.remove();
						Moderator.reload("");
					} else {
						Label display = new Label(
								"You should just change the parent");
						selectedWrapper.getEditMenu().setDisplay(display);
					}
					break;
				case Exponential:
				case Fraction:
					MathMLBindingNode newParent = parent.getParent();
					
					switch (node.getIndex()) {
					case 0:
						newParent.add(parent.getIndex(), node.getNextSibling());
						break;
					case 1:
						newParent.add(parent.getIndex(), node.getPrevSibling());
						break;
					}
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
				int index = node.getIndex();

				// TODO
				System.out.println("1: "
						+ node.getTree().getMathML().getString());

				try {
					Operator operator = null;

					switch (type) {
					case Number:
						parent.add(node.getIndex(), type, "1");
						node.remove();
						break;
					case Variable:
						parent.add(node.getIndex(), type, "x");
						node.remove();
						break;

					case Sum:
						operator = Operator.PLUS;
					case Term:
						if (operator == null)
							operator = Operator.getMultiply();
					case Exponential:
					case Fraction:
						MathMLBindingNode newNode = Moderator.jTree.NEW_NODE(
								type, "");

						newNode.add(node);
						if (operator != null)
							newNode.add(-1, Type.Operation, operator.getSign());
						newNode.add(-1, Type.Variable, NOT_SET);

						parent.add(index, newNode);
					}
					// node.remove();

					Moderator.reload("");
				} catch (NoSuchElementException e) {
					e.printStackTrace();
				}
				// TODO
				System.out.println("2: "
						+ node.getTree().getMathML().getString());
			}
		}
	}
}
