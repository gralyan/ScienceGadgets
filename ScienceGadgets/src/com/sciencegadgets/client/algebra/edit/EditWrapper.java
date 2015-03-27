package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.client.algebra.EquationWrapper;
import com.sciencegadgets.shared.TypeSGET;

public class EditWrapper extends EquationWrapper {

	public ChangeNodeMenu changeNodeMenu = new ChangeNodeMenu();

	public EditWrapper(EquationNode node, AlgebraActivity algebraActivity, Element element) {
		super(node, algebraActivity, element);
		
		
		if(TypeSGET.Operation.equals(node.getType())) {
			if(Moderator.isTouch) {
				addTouchStartHandler(new OperationTouchHandler());
			}else {
				addClickHandler(new OperationClickHandler());
			}
		}else{
			changeNodeMenu.setNode(node);
//			eqPanel.getAlgebraActivity().lowerEqArea.add(changeNodeMenu);
//			changeNodeMenu.setVisible(false);
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
				changeNodeMenu.updatePaste();
				eqPanel.getAlgebraActivity().lowerEqArea.add(changeNodeMenu);
//				changeNodeMenu.setVisible(true);
			}
		}
	}

	public void unselect() {

		if (changeNodeMenu != null) {
//		changeNodeMenu.setVisible(false);
		changeNodeMenu.removeFromParent();
		}
		
		super.unselect();
	}
	
	private void changeOperator() {
		TypeSGET.Operator operation = node.getOperation();
		if (operation == null) {
			return;
		}
		switch (operation) {
		case CROSS:
			node.setSymbol(TypeSGET.Operator.DOT.getSign());
			break;
		case DOT:
			node.setSymbol(TypeSGET.Operator.CROSS.getSign());
			break;
		case MINUS:
			node.setSymbol(TypeSGET.Operator.PLUS.getSign());
			break;
		case PLUS:
			node.setSymbol(TypeSGET.Operator.MINUS.getSign());
			break;
		}
		getAlgebraActivity().reloadEquationPanel(null, null, true, node.getId());
//		 Moderator.reloadEquationPanel();
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
