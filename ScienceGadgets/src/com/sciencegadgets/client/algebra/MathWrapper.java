/*   Copyright 2012 John Gralyan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sciencegadgets.client.algebra;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type.Operator;

/**
 * This Widget is used to wrap elementary tags so mouse handlers can be attached
 * to them. Mainly used for MathML tags so equations can be manipulated
 * 
 * @author John Gralyan
 * 
 */
public class MathWrapper extends Wrapper {

	private WrapDragController dragController = null;
	private BothSidesMenu bothSidesMenu;

	/**
	 * Wrapper for symbols which allow for user interaction
	 * 
	 * <p>
	 * <b>Note - this widget can only be draggable if it's attached to an
	 * {@link AbsolutePanel}</b>
	 * </p>
	 * 
	 * @param svg
	 * 
	 * @param theElement
	 *            - the element to wrap in widget
	 */
	public MathWrapper(MathNode node, EquationPanel eqPanel,
			Element element) {
		super(node, eqPanel, element);

		bothSidesMenu = new BothSidesMenu(this, element.getOffsetWidth() + "px");
		menu = new VerticalPanel();
	}

	/**
	 * Called when attaching Widget. If it is draggable, it can only be attached
	 * to an {@link AbsolutePanel}
	 */
	public void onAttach() {
		super.onAttach();
			switch (node.getType()) {
			case Sum:
			case Term:
			case Exponential:
			case Fraction:
			case Variable:
			case Number:
				addDragController();
			}
	}

	// public NodeMenu getContextMenu() {
	// return (NodeMenu) menu;
	// }

	public WrapDragController getDragControl() {
		return dragController;
	}

	/**
	 * Add a drag controller to this widget, can be a subclass of
	 * {@link AbstractDragController} such as {@link ElementDragController}
	 * <p>
	 * If a drag controller already exists, it is overridden
	 * </p>
	 * 
	 * @return The new drag controller added
	 */
	public WrapDragController addDragController() {

		WrapDragController dragC = new WrapDragController(eqPanel, false);

		dragController = dragC;
		dragController.makeDraggable(this);
		return dragController;
	}

	/**
	 * removes a drag controller from this widget
	 * 
	 */
	public void removeDragController() {
		if (dragController != null) {
			dragController.makeNotDraggable(this);
			dragController = null;
		}
	}

	public void removeDropTargets() {
		dragController.unregisterDropControllers();
		dragController.getDropControllers().clear();
	}

	/**
	 * Highlights the selected wrapper and joiner as well as all the drop
	 * targets associated with the selected
	 * 
	 * @param wrapper
	 * @param select
	 *            - selects if true, unselects if false
	 */
	public void select() {

		Moderator.lowerEqArea.clear();
		Moderator.lowerEqArea.add(bothSidesMenu);
		super.select();
			
		switch (node.getType()) {
		case Equation:
		case Exponential:
		case Fraction:
		case Sum:
		case Term:
			break;
		case Operation:
			break;
		case Number:
		case Variable:
			if (node.getSymbol().startsWith(Type.Operator.MINUS.getSign()) && !node.getSymbol().equals("-1")) {
				MathNode parent = node.getParent();
				node.setSymbol(node.getSymbol().replaceFirst(
						Type.Operator.MINUS.getSign(), ""));
				if (!Type.Term.equals(parent.getType())) {
					parent = node.encase(Type.Term);
				}
				int nodeIndex = node.getIndex();
				parent.add(nodeIndex, Type.Operation, Type.Operator.getMultiply()
						.getSign());
				parent.add(nodeIndex, Type.Number, "-1");
				Moderator.reloadEquationPanel(null);
			}
			break;
		}
	}

	public void unselect() {
		if (node.getType().hasChildren()) {
			Moderator.lowerEqArea.clear();
			super.unselect();
		}
	}
}
