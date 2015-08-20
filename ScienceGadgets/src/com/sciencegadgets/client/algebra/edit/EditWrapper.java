/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
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
			changeNodeMenu = null;
		}else{
			changeNodeMenu.setNode(node);
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
		if(!TypeSGET.Operation.equals(node.getType())) {
		super.select();

		if (this.equals(eqPanel.getSelectedWrapper())) {
			if (changeNodeMenu != null) {
				changeNodeMenu.updatePaste();
				eqPanel.getAlgebraActivity().lowerEqArea.add(changeNodeMenu);
			}
		}
		}
	}

	public void unselect() {

		if (changeNodeMenu != null) {
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
//		case CROSS:
//			node.setSymbol(TypeSGET.Operator.DOT.getSign());
//			break;
//		case DOT:
//			node.setSymbol(TypeSGET.Operator.CROSS.getSign());
//			break;
		case MINUS:
			node.setSymbol(TypeSGET.Operator.PLUS.getSign());
			getAlgebraActivity().reloadEquationPanel(null, null, true, node.getId());
			break;
		case PLUS:
			node.setSymbol(TypeSGET.Operator.MINUS.getSign());
			getAlgebraActivity().reloadEquationPanel(null, null, true, node.getId());
			break;
		}
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
