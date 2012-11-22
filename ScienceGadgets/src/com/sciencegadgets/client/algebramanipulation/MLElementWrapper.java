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
package com.sciencegadgets.client.algebramanipulation;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.Wrapper;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.AbstractMathDropController;
import com.sciencegadgets.client.equationtree.EquationLayer;
import com.sciencegadgets.client.equationtree.EquationPanel;
import com.sciencegadgets.client.equationtree.MathMLBindingTree;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

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
	private VerticalPanel contextMenu = new VerticalPanel();

	/**
	 * Wrapper for symbols which allow for user interaction
	 * 
	 * <p>
	 * <b>Note - this widget can only be draggable if it's attached to an
	 * {@link AbsolutePanel}</b>
	 * </p>
	 * @param svg 
	 * 
	 * @param theElement
	 *            - the element to wrap in widget
	 */
	public MLElementWrapper(MathMLBindingNode node, EquationPanel eqPanel,
			EquationLayer eqLayer, String width, String height, com.google.gwt.user.client.Element svg) {
		super(node, eqPanel, eqLayer, width, height, svg);
	
	}

	/**
	 * Called when attaching Widget. If it is draggable, it can only be attached
	 * to an {@link AbsolutePanel}
	 */
	public void onAttach() {
		super.onAttach();
		addDragController();
	}
	
	public void setSelectedWrapper(MLElementWrapper selectedWrapper) {
		this.selectedWrapper = selectedWrapper;
	}
	
	public VerticalPanel getContextMenu(){
		return contextMenu;
	}

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

			WrapDragController dragC = new WrapDragController(
					(AbsolutePanel) this.getParent(), false);

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
		super.select();
		MLElementWrapper wrapper = this;

		String path = "com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropController_";
		String changeDesc = "";

		for (DropController dropC : wrapper.dragController.getDropControllers()) {

			String className = dropC.getClass().getName();
			String change = ((AbstractMathDropController) dropC)
					.findChange(getNode());

			// Style of highlight on potential targets
			if ((path + "Simplify_Add").equals(className)) {
				changeDesc = "<b>Simplify</b> <br/>" + change;

			} else if ((path + "Simplify_Multiply").equals(className)) {
				changeDesc = "<b>Simplify</b> <br/>" + change;

			} else if ((path + "Simplify_Divide").equals(className)) {
				changeDesc = "<b>Simplify</b> <br/>" + change;

			} else if ((path + "BothSides_Add").equals(className)) {
				changeDesc = "<b>Add &nbsp; " + change
						+ "</b><br/>to both sides";

			} else if ((path + "BothSides_Multiply").equals(className)) {
				changeDesc = "<b>Multiply &nbsp; " + change
						+ "</b><br/>with both sides";

			} else if ((path + "BothSides_Divide").equals(className)) {
				changeDesc = "<b>Divide by &nbsp; " + change
						+ "</b><br/>on both sides";
			}

			// Highlights drop targets
			MLElementWrapper dropCwrap = ((MLElementWrapper) dropC
					.getDropTarget());
			dropC.getDropTarget().setStyleName("selectedDropWrapper");
			// dropCwrap.getJoinedWrapper()
			// .setStyleName("selectedDropWrapper");

			// Descriptors
			HTML dropDesc = dropCwrap.getDropDescriptor();
			dropDesc.setHTML(changeDesc);
			dropDesc.setStyleName("dropDescriptor");
		}

	}

	public void unselect() {
		super.unselect();
		MLElementWrapper wrapper = this;

		for (DropController dropC : wrapper.dragController.getDropControllers()) {

			String[] styles = dropC.getDropTarget().getStyleName().split(" ");

			MLElementWrapper dropCwrap = (MLElementWrapper) dropC
					.getDropTarget();

			for (String style : styles) {
				if (style.startsWith("selectedDropWrapper")) {
					dropCwrap.removeStyleName(style);
					// dropCwrap.getJoinedWrapper().removeStyleName(style);
				}

				HTML dropDesc = dropCwrap.getDropDescriptor();
				dropDesc.setText("");
				dropDesc.removeStyleName("dropDescriptor");
			}
		}
	}
}
