package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.transformations.AssociativeDropController;
import com.sciencegadgets.shared.TypeML;

public class Wrapper extends HTML implements HasHandlers {

	protected MathNode node;
	protected AbsolutePanel parentPanel;
	private WrapDragController dragController = null;

	public Wrapper(MathNode node, final AbsolutePanel parentPanel, Element element) {
		super(element);

		this.node = node;
		this.parentPanel = parentPanel;

		onAttach();

		node.wrap(this);

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


	public Wrapper getNextSiblingWrapper() throws IndexOutOfBoundsException {
		return node.getNextSibling().getWrapper();
	}

	public Wrapper getPrevSiblingWrapper() throws IndexOutOfBoundsException {
		return node.getPrevSibling().getWrapper();
	}

	public Wrapper getParentWrapper() {
		return node.getParent().getWrapper();
	}

	public void select() {
		EquationPanel.selectedWrapper = this;
		this.getElement().addClassName("selectedWrapper");
	}

	public void unselect() {
		EquationPanel.selectedWrapper = null;
		this.getElement().removeClassName("selectedWrapper");
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
		if(dragController != null) {
		dragController.unregisterDropControllers();
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// Inner Classes
	// ////////////////////////////////////////////////////////////////////
	public class WrapperClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			event.preventDefault();
			event.stopPropagation();
			select();
		}
	}

	class WrapperTouchStartHandler implements TouchStartHandler {
		@Override
		public void onTouchStart(TouchStartEvent event) {
			event.preventDefault();
			event.stopPropagation();
			
			select();
		}
	}


}
