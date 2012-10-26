package com.sciencegadgets.client.equationtree;

import java.util.NoSuchElementException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
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

		for (MathMLBindingTree.Type type : types) {
			if(Type.Operation.equals(type))
				continue;
			
			ChangeNodeHandler handler = new ChangeNodeHandler(type);
			Button button = new Button(type.toString(), handler);

			int buttonHeight = height / (types.length - 1);
			button.setHeight(buttonHeight + "px");
			button.setWidth(width + "px");

			this.add(button);
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
				MathMLBindingNode node = selectedWrapper.node;
				MathMLBindingNode parent = node.getParent();
				MathMLBindingTree tree = node.getTree();

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
						if(operator == null)
						operator = Operators.getMultiply();
					case Exponential:
					case Fraction:
						MathMLBindingNode encasing = Moderator.jTree.NEW_NODE(
								type, "");
						encasing.add(-1, Type.Variable, NOT_SET);
						if (operator != null)
							encasing.add(-1, Type.Operation, operator.getSign());
						encasing.add(-1, Type.Variable, NOT_SET);
						parent.add(node.getIndex(), encasing);
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
