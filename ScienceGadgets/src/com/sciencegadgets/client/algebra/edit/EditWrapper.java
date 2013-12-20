package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationPanel;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.ZoomWrapper;
import com.sciencegadgets.shared.TypeML;

public class EditWrapper extends ZoomWrapper {

	public ChangeNodeMenu changeNodeMenu = null;

	public EditWrapper(MathNode node, EquationPanel eqPanel, Element element) {
		super(node, eqPanel, element);
		
		
		if(TypeML.Operation.equals(node.getType())) {
			addClickHandler(new OperationClickHandler());
		}else{
			changeNodeMenu = new ChangeNodeMenu(node);
			AlgebraActivity.lowerEqArea.add(changeNodeMenu);
			changeNodeMenu.setVisible(false);
		}
	}

	@Override
	public void onUnload() {
		if (changeNodeMenu != null) {
		changeNodeMenu.removeFromParent();
		changeNodeMenu = null;
		}
		super.onUnload();
	}

	public void select() {
		super.select();

		if (this.equals(EquationPanel.selectedWrapper)) {
			if (changeNodeMenu != null) {
				changeNodeMenu.setVisible(true);
			}
		}
	}

	public void unselect() {

		if (changeNodeMenu != null) {
		changeNodeMenu.setVisible(false);
		}
		
		// for (TypeML type : TypeML.values()) {
		// changeNodeMenu.setEnable(type, true);
		// }
		super.unselect();
	}
	
	private class OperationClickHandler implements ClickHandler{
		@Override
		public void onClick(ClickEvent event) {
			TypeML.Operator operation = node.getOperation();
			if (operation == null) {
				return;
			}
			switch (operation) {
			case CROSS:
				node.setSymbol(TypeML.Operator.DOT.getSign());
				break;
			case DOT:
				node.setSymbol(TypeML.Operator.CROSS.getSign());
				break;
			case MINUS:
				node.setSymbol(TypeML.Operator.PLUS.getSign());
				break;
			case PLUS:
				node.setSymbol(TypeML.Operator.MINUS.getSign());
				break;
			}
				// Moderator.reloadEquationPanel(null, null);
		}
		
	}
}
