package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Element;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationPanel;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.EquationWrapper;
import com.sciencegadgets.shared.TypeML;

public class EditWrapper extends EquationWrapper {

	public ChangeNodeMenu changeNodeMenu = new ChangeNodeMenu();

	public EditWrapper(MathNode node, AlgebraActivity algebraActivity, Element element) {
		super(node, algebraActivity, element);
		
		
		if(TypeML.Operation.equals(node.getType())) {
			if(Moderator.isTouch) {
				addTouchStartHandler(new OperationTouchHandler());
			}else {
				addClickHandler(new OperationClickHandler());
			}
		}else{
			changeNodeMenu.setNode(node);
			Moderator.getCurrentAlgebraActivity().lowerEqArea.add(changeNodeMenu);
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

		if (this.equals(eqPanel.selectedWrapper)) {
			if (changeNodeMenu != null) {
				changeNodeMenu.setVisible(true);
			}
		}
	}

	public void unselect() {

		if (changeNodeMenu != null) {
		changeNodeMenu.setVisible(false);
		}
		
		super.unselect();
	}
	
	private void changeOperator() {
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
	private class OperationTouchHandler implements TouchStartHandler{
		@Override
		public void onTouchStart(TouchStartEvent event) {
			changeOperator();
		}
	}
	private class OperationClickHandler implements ClickHandler{
		@Override
		public void onClick(ClickEvent event) {
			changeOperator();
		}
	}
}
