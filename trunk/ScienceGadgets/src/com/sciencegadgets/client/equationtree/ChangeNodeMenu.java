package com.sciencegadgets.client.equationtree;

import java.util.NoSuchElementException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.algebramanipulation.Moderator;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class ChangeNodeMenu extends VerticalPanel {

	// public static EditWrapper selectedWrapper;
	public EquationList eqList;

	public ChangeNodeMenu(EquationList eqList) {
		this.eqList = eqList;
		// this.selectedWrapper = (EditWrapper) eqList.selectedWrapper;

		// Make this menu a fraction of the screens width, same height
		int width = eqList.mainPanel.getOffsetWidth() / 10;
		int height = eqList.mainPanel.getOffsetHeight();

		Type[] types = MathMLBindingTree.Type.values();

		for (MathMLBindingTree.Type type : types) {
			ChangeNodeHandler handler = new ChangeNodeHandler(type);
			Button button = new Button(type.toString(), handler);

			int buttonHeight = height / (types.length);
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

				System.out.println("b- "
						+ node.getTree().getMathML().getString());

				try {
					parent.add(node.getIndex(), type, "???");
					node.remove();

					System.out.println("a- "
							+ node.getTree().getMathML().getString());
					Moderator.reload("");
				} catch (NoSuchElementException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
