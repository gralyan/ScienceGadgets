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
import com.sciencegadgets.client.algebra.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.algebra.MathMLBindingTree.Type;

/**
 * This Widget is used to wrap elementary tags so mouse handlers can be attached
 * to them. Mainly used for MathML tags so equations can be manipulated
 * 
 * @author John Gralyan
 * 
 */
public class MLElementWrapper extends Wrapper {

	private WrapDragController dragController = null;
	private MathMLBindingNode mathMLBindingNode;
	private HTML dropDescriptor = new HTML();
	private NodeMenu bothSidesMenu;

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
	public MLElementWrapper(MathMLBindingNode node, EquationPanel eqPanel,
			EquationLayer eqLayer, Element element) {
		super(node, eqPanel, eqLayer, element);

		bothSidesMenu = new NodeMenu(this, element.getOffsetWidth() + "px");
		// addMouseOutHandler(new Wrapper.WrapperMouseOutHandler());
	}

	/**
	 * Called when attaching Widget. If it is draggable, it can only be attached
	 * to an {@link AbsolutePanel}
	 */
	public void onAttach() {
		super.onAttach();
		if (!Type.Operation.equals(node.getType()))
			addDragController();
	}

	// public NodeMenu getContextMenu() {
	// return (NodeMenu) menu;
	// }

	public HTML getDropDescriptor() {
		return dropDescriptor;
	}

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
		Moderator.lowerEqArea.add(bothSidesMenu);
		super.select();
	}
	public void unselect() {
		Moderator.lowerEqArea.clear();
		super.unselect();
	}
}
