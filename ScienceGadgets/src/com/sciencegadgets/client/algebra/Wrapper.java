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
package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.AssociativeDropController;
import com.sciencegadgets.client.conversion.ConversionWrapper;
import com.sciencegadgets.client.conversion.ReorderDropController;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.TypeSGET;

public class Wrapper extends HTML implements HasClickHandlers,
		HasTouchStartHandlers, HasTouchEndHandlers {
	
	protected EquationNode node;
	protected AbsolutePanel parentPanel;
	protected WrapDragController dragController = null;
	public boolean moved = false;
	private EquationLayer equationLayer;

	public Wrapper(EquationNode node, final AbsolutePanel parentPanel,
			Element element) {
		super(element);

		this.node = node;
		this.parentPanel = parentPanel;

		onAttach();

		node.wrap(this);

		// zIndex eqPanel=1 wrapper=2 menu=3
//		this.getElement().getStyle().setZIndex(2);

		if (Moderator.isTouch) {
			addTouchEndHandler(new WrapperTouchEndHandler());
		} else {
			addClickHandler(new WrapperClickHandler());
		}

	}

	public EquationNode getNode() {
		return node;
	}

	public Wrapper getNextSiblingWrapper() throws IndexOutOfBoundsException {
		return node.getNextSibling().getWrapper();
	}

	public Wrapper getPrevSiblingWrapper() throws IndexOutOfBoundsException {
		return node.getPrevSibling().getWrapper();
	}

	public Wrapper getParentWrapper() {
		return node.getParent().getWrapper();
	}
	
	public void setLayer(EquationLayer equationLayer) {
		this.equationLayer = equationLayer;
	}
	
	public EquationLayer getLayer() {
		return equationLayer;
	}

	public void select() {
//		Moderator.SOUNDS.WRAPPER_SELECT.play();
		
		this.addStyleName(CSS.SELECTED_WRAPPER);
	}

	public void unselect() {
		this.removeStyleName(CSS.SELECTED_WRAPPER);
	}

	@Override
	protected void onUnload() {
		removeDropTargets();
		removeDragController();
		dragController = null;
		super.onUnload();
	}

	// /////////////////////////////////////////////////////////////////////
	// Drag & Drop
	// ////////////////////////////////////////////////////////////////////

	public void addAssociativeDragDrop() {

		// Add associative drag and drop
		if ((TypeSGET.Sum.equals(node.getParentType()) || TypeSGET.Term.equals(node
				.getParentType())) && !TypeSGET.Operation.equals(node.getType())) {

			addDragController();

			LinkedList<EquationNode> siblings = node.getParent().getChildren();
			siblings.remove(node);
			for (EquationNode dropNode : siblings) {
				if (!TypeSGET.Operation.equals(dropNode.getType())) {
					DropController controller = null;
					Wrapper dropWraper = dropNode.getWrapper();
					if (dropWraper instanceof ConversionWrapper) {
						controller = new ReorderDropController(
								(ConversionWrapper) dropWraper);
					} else if (dropWraper instanceof EquationWrapper) {
						controller = new AssociativeDropController(
								(EquationWrapper) dropWraper);
					} else {
						continue;
					}
					dragController.registerDropController(controller);
				}
			}
		}
	}

	public WrapDragController getDragControl() {
		return dragController;
	}

	public WrapDragController addDragController() {

		if (dragController == null) {
			dragController = new WrapDragController(parentPanel, false);
			dragController.makeDraggable(this);
		}

		return dragController;
	}

	public void removeDragController() {
		if (dragController != null) {
			dragController.makeNotDraggable(this);
			dragController = null;
		}
	}

	public void removeDropTargets() {
		if (dragController != null) {
			dragController.unregisterDropControllers();
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// Inner Classes
	// ////////////////////////////////////////////////////////////////////
	public class WrapperClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			event.stopPropagation();
			select();
		}
	}
	
	class WrapperTouchEndHandler implements TouchEndHandler {
		@Override
		public void onTouchEnd(TouchEndEvent event) {
			event.stopPropagation();
			if(!moved) {
				select();
			}
			moved = false;
		}
	}

}
