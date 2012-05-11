package com.sciencegadgets.client.algebramanipulation;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.DragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasDragStartHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Log;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.AbstractMathDropController;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropControllerAddition;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropControllerMultiplication;
import com.sciencegadgets.client.equationtree.DropControllAssigner;
import com.sciencegadgets.client.equationtree.JohnTree;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

/**
 * This Widget is used to wrap elementary tags so mouse handlers can be attached
 * to them. Mainly used for MathML tags so equations can be manipulated
 * 
 * @author John Gralyan
 * 
 */
public class MLElementWrapper extends HTML implements HasMouseOutHandlers,
		HasMouseOverHandlers, HasDragStartHandlers {

	private WrapDragController dragController = null;
	private Element element = null;
	private Boolean isDraggable;
	private MLElementWrapper joinedWrapper;
	private JohnNode johnNode;

	/**
	 * Construct that can explicitly state weather default handlers will be
	 * available. Use the static method {@link MLElementWrapper.wrapperFactory}
	 * to automate this process
	 * <p>
	 * <b>Note - this widget can only be draggable if it's attached to an
	 * {@link AbsolutePanel}</b>
	 * </p>
	 * 
	 * @param theElement
	 *            - the element to wrap in widget
	 * @param isDraggable
	 *            - if true, adds a default drag {@link ElementDragController}
	 *            for the parent {@link AbsolutePanel} it is currently in
	 * @param isJoined
	 *            - If true, there will be two wrappers made which point to one
	 *            another by the joinedWrapper field. This allows for multiple
	 *            instances of the same wrapper that communicate in different
	 *            views
	 */
	public MLElementWrapper(JohnNode jNode, Boolean isDraggable,
			Boolean isJoined) {
		this.element = (Element) jNode.getDomNode();
		this.johnNode = jNode;
		this.isDraggable = isDraggable;
		addMouseOverHandler();
		addMouseOutHandler();
		addClickHandler();

		if (isJoined == true) {
			this.joinedWrapper = new MLElementWrapper(jNode, isDraggable, false);
			this.joinedWrapper.joinedWrapper = this;
		}
	}

	public Element getElementWrapped() {
		return element;
	}

	public void setElementWrapped(Element el) {
		element = el;
	}

	public MLElementWrapper getJoinedWrapper() {
		return joinedWrapper;
	}

	public JohnTree.JohnNode getJohnNode() {
		return johnNode;
	}

	public DragController getDragControl() {
		return dragController;
	}

	/**
	 * Called when attaching Widget. If it is draggable, it can only be attached
	 * to an {@link AbsolutePanel}
	 */
	public void onAttach() {
		super.onAttach();
		addDragController();
	}

	// /////////////////////////////
	// add handler methods
	// /////////////////////////////
	public HandlerRegistration addMouseOverHandler() {
		return addDomHandler(new ElementOverHandler(), MouseOverEvent.getType());
	}

	public HandlerRegistration addMouseOutHandler() {
		return addDomHandler(new ElementOutHandler(), MouseOutEvent.getType());
	}
	
	public HandlerRegistration addClickHandler(){
		return addDomHandler(new ElementClickHandler(), ClickEvent.getType());
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

		if (isDraggable) {
			WrapDragController dragC = new WrapDragController(
					(AbsolutePanel) this.getParent(), false);

			dragController = dragC;
			dragController.makeDraggable(this);
		}
		return dragController;
	}

	/**
	 * removes a drag controller from this widget
	 * 
	 * @throws Exception
	 */
	public void removeDragController() {
		if (dragController != null) {
			dragController.makeNotDraggable(this);
			dragController = null;
		}
	}

	public AbstractMathDropController addDropTarget(MLElementWrapper target,
			DropControllAssigner.DropType dropType) {

		AbstractMathDropController dropCtrl = null;

		switch (dropType) {
		case Add:
			dropCtrl = new DropControllerAddition(target);
			break;
		case Multiply:
			dropCtrl = new DropControllerMultiplication(target);
			break;
		}
		dragController.registerDropController(dropCtrl);

		return dropCtrl;
	}

	public void removeDropTargets() {
		dragController.unregisterDropControllers();
		dragController.getDropList().clear();
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// Inner Class Handlers
	// /////////////////////////////////////////////////////////////////////////////////////
	class ElementOverHandler implements MouseOverHandler {
		public void onMouseOver(MouseOverEvent event) {
			select((MLElementWrapper) event.getSource(), true);
		}
	}

	class ElementOutHandler implements MouseOutHandler {
		public void onMouseOut(MouseOutEvent event) {
			select((MLElementWrapper) event.getSource(), false);
		}
	}
	
	class ElementClickHandler implements ClickHandler{
		@Override
		public void onClick(ClickEvent event) {
			select((MLElementWrapper) event.getSource(), true);
		}
	}

	/**
	 * Highlights the selected wrapper and joiner as well as all the drop
	 * targets associated with the selected
	 * 
	 * @param wrapper
	 * @param select
	 *            - selects if true, unselects if false
	 */
	void select(MLElementWrapper wrapper, Boolean select) {
		if (select) {
			// Highlights selected
			wrapper.getElement().setId("selectedWrapper");
			// Highlights joiner
			wrapper.getJoinedWrapper().getElement().setId("selectedWrapper");

			String style = null;
			for (DropController dropC : wrapper.dragController.getDropList()) {

				if ("com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropControllerAddition"
						.equals(dropC.getClass().getName())) {
					style = "selectedDropWrapperAddition";
				} else if ("com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropControllerMultiplication"
						.equals(dropC.getClass().getName())) {
					style = "selectedDropWrapperMultiplication";
				} else {
					style = "selectedDropWrapperDefault";
				}

				// Highlights drop targets
				dropC.getDropTarget().setStyleName(style);
				// Highlights joiner drop targets
				((MLElementWrapper) dropC.getDropTarget()).getJoinedWrapper()
						.setStyleName(style);
			}

		} else {
			wrapper.getElement().removeAttribute("id");
			wrapper.getJoinedWrapper().getElement().removeAttribute("id");

			for (DropController dropC : wrapper.dragController.getDropList()) {

				String[] styles = dropC.getDropTarget().getStyleName()
						.split(" ");

				for (String style : styles) {
					if (style.startsWith("selectedDropWrapper")) {
						dropC.getDropTarget().removeStyleName(style);
						((MLElementWrapper) dropC.getDropTarget())
								.getJoinedWrapper().removeStyleName(style);
					}
				}

			}
		}
	}
}
