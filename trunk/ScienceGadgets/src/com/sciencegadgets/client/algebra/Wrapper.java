package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.UIObject;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.edit.EditWrapper;
import com.sciencegadgets.client.algebra.transformations.AssociativeDropController;

public class Wrapper extends HTML implements HasHandlers {

	protected MathNode node;
	protected EquationPanel eqPanel;
	// protected EquationLayer eqLayer;
	public int paddingLeft = 0;
	public int paddingRight = 0;
	protected FlowPanel menu;
	private WrapDragController dragController = null;

	public Wrapper(MathNode node, final EquationPanel eqPanel, Element element) {
		super(element);

		this.node = node;
		this.eqPanel = eqPanel;

		onAttach();

		node.wrap(this);
		node.getWrapper();

		this.addStyleName("displayWrapper");

		// zIndex eqPanel=1 wrapper=2 menu=3
		this.getElement().getStyle().setZIndex(2);

		if (Moderator.isTouch) {
			addTouchStartHandler(new WrapperTouchStartHandler());
		} else {
			addClickHandler(new WrapperClickHandler());
		}

	}

	public MathNode getNode() {
		return node;
	}

	public EquationLayer getEqLayer() {
		return eqPanel.eqLayerMap.get(node);
	}

	public EquationPanel getEqPanel() {
		return eqPanel;
	}

	// public VerticalPanel getMenu(){
	// return menu;
	// }

	public Wrapper getNextSiblingWrapper() throws IndexOutOfBoundsException {
		return node.getNextSibling().getWrapper();
	}

	public Wrapper getPrevSiblingWrapper() throws IndexOutOfBoundsException {
		return node.getPrevSibling().getWrapper();
	}

	public Wrapper getParentWrapper() {
		return node.getParent().getWrapper();
	}

	// Public selection methods, calls subclasses approprimathNodeately
	public void select(boolean inEditMode) {

		if (this.equals(EquationPanel.selectedWrapper)) {

			// If this was already selected, focus in on it
			if (node.getType().hasChildren()) {
				
				this.unselect(AlgebraActivity.inEditMode);
				eqPanel.setFocus(getEqLayer());
			}
		} else {

			// If there is another selection, unselect it
			if (EquationPanel.selectedWrapper != null) {
				EquationPanel.selectedWrapper
						.unselect(AlgebraActivity.inEditMode);
			}

			AlgebraActivity.contextMenuArea.add(menu);

			if (inEditMode) {
				((EditWrapper) this).select();
			} else {
				((MathWrapper) this).select();
			}
		}
	}

	public void unselect(boolean inEditMode) {

		AlgebraActivity.contextMenuArea.clear();

		if (inEditMode) {
			((EditWrapper) this).unselect();
		} else {
			((MathWrapper) this).unselect();
		}
	}

	protected void select() {

		EquationPanel.selectedWrapper = this;
		this.getElement().addClassName("selectedWrapper");
	}

	protected void unselect() {

		EquationPanel.selectedWrapper = null;
		this.getElement().removeClassName("selectedWrapper");
	}

	@Override
	protected void onUnload() {
		removeDropTargets();
		removeDragController();
		super.onUnload();
	}

	// /////////////////////////////////////////////////////////////////////
	// Drag & Drop
	// ////////////////////////////////////////////////////////////////////

	public void addAssociativeDragDrop() {

		// Add associative drag and drop
		if ((TypeML.Sum.equals(node.getParentType()) || TypeML.Term.equals(node
				.getParentType())) && !TypeML.Operation.equals(node.getType())) {

			addDragController();

			LinkedList<MathNode> siblings = node.getParent().getChildren();
			siblings.remove(node);
			for (MathNode dropNode : siblings) {
				if (!TypeML.Operation.equals(dropNode.getType()))
					dragController
							.registerDropController(new AssociativeDropController(
									dropNode.getWrapper()));
			}
		}
	}

	public WrapDragController getDragControl() {
		return dragController;
	}

	public WrapDragController addDragController() {

		if (dragController == null) {
			dragController = new WrapDragController(eqPanel, false);
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
		dragController.unregisterDropControllers();
	}

	// /////////////////////////////////////////////////////////////////////
	// Inner Classes
	// ////////////////////////////////////////////////////////////////////
	public class WrapperClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			event.preventDefault();
			event.stopPropagation();
			select(AlgebraActivity.inEditMode);
		}
	}

	class WrapperTouchStartHandler implements TouchStartHandler {
		@Override
		public void onTouchStart(TouchStartEvent event) {
			event.preventDefault();
			event.stopPropagation();
			
			select(AlgebraActivity.inEditMode);
		}
	}


}
