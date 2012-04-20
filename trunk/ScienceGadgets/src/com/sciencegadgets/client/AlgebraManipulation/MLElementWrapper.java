package com.sciencegadgets.client.AlgebraManipulation;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.DragController;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Element;
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
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.EquationTree.JohnTree;
import com.sciencegadgets.client.EquationTree.JohnTree.JohnNode;

/**
 * This Widget is used to wrap elementary tags so mouse handlers can be attached
 * to them. Mainly used for MathML tags so equations can be manipulated
 * 
 * @author John Gralyan
 * 
 */
public class MLElementWrapper extends HTML implements HasMouseOutHandlers,
		HasMouseOverHandlers, HasDragStartHandlers {

	private ElementDragController dragController = null;
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
		addMouseOverHandlerDefault();
		addMouseOutHandlerDefault();

		if (isJoined == true) {
			this.joinedWrapper = new MLElementWrapper(jNode, isDraggable,
					this);
		}
	}

	/**
	 * Private constructor for making a joined wrapper. Joined wrappers
	 * represent the same element in different widgets eg. the same variable in
	 * the equation manipulator view and the tree view
	 * 
	 * @param theElement
	 * @param isDraggable
	 * @param joinedWrapper
	 */
	private MLElementWrapper(JohnNode jNode, Boolean isDraggable,
			MLElementWrapper joinedWrapper) {
		this.element = (Element) jNode.getDomNode();
		this.johnNode = jNode;
		this.isDraggable = isDraggable;
		addMouseOverHandlerDefault();
		addMouseOutHandlerDefault();

		this.joinedWrapper = joinedWrapper;
	}


	public Element getElementWrapped() {
		return element;
	}
	
	public void setElementWrapped(Element el){
		element = el;
	}

	public MLElementWrapper getJoinedWrapper() {
		return joinedWrapper;
	}

	public JohnTree.JohnNode getJohnNode() {
		return johnNode;
	}
	public DragController getDragControl(){
		return dragController;
	}

	/**
	 * Called when attaching Widget. If it is draggable, it can only be attached
	 * to an {@link AbsolutePanel}
	 */
	public void onAttach() {
		super.onAttach();

		if (isDraggable) {
			ElementDragController dragC = new ElementDragController(
					(AbsolutePanel) this.getParent(), true);
			addDragController(dragC);
		}

	}

	// /////////////////////
	// addMouse-OVER-Handler handler methods
	// /////////////////////////////
	public HandlerRegistration addMouseOverHandlerDefault() {
		return addMouseOverHandler(new ElementOverHandler());
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	// /////////////////////
	// addMouse-OUT-Handler handler methods
	// /////////////////////////////
	public HandlerRegistration addMouseOutHandlerDefault() {
		return addMouseOutHandler(new ElementOutHandler());
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	/**
	 * Add a drag controller to this widget, can be a subclass of
	 * {@link AbstractDragController} such as {@link ElementDragController}
	 * <p>
	 * If a drag controller already exists, it is overridden
	 * </p>
	 * 
	 * @param dragCtrl
	 * @return
	 * @throws DragControllerException
	 */
	public ElementDragController addDragController(
			ElementDragController dragCtrl) {
		dragController = dragCtrl;
		dragController.makeDraggable(this);
		return dragController;
	}

	/**
	 * removes a drag controller from this widget
	 * 
	 * @param dragCtrl
	 * @throws Exception
	 */
	public void removeDragController(ElementDragController dragCtrl)
			throws Exception {
		if (dragController != null) {
			dragController.makeNotDraggable(this);
			dragController = null;
		}
	}

	public MathMLDropController addDropTarget(Widget target) {
		MathMLDropController dropCtrl = new MathMLDropController(target);
		dragController.registerDropController(dropCtrl);
		return dropCtrl;
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// Inner Class Handlers
	// /////////////////////////////////////////////////////////////////////////////////////
	class ElementOverHandler implements MouseOverHandler {
		public void onMouseOver(MouseOverEvent event) {
			((MLElementWrapper) event.getSource()).getElement().setId(
					"selectedWrapper");
			((MLElementWrapper) event.getSource()).getJoinedWrapper()
					.getElement().setId("selectedJoinedWrapper");
		}
	}

	class ElementOutHandler implements MouseOutHandler {
		public void onMouseOut(MouseOutEvent event) {
			((MLElementWrapper) event.getSource()).getElement()
					.removeAttribute("id");
			((MLElementWrapper) event.getSource()).getJoinedWrapper()
					.getElement().removeAttribute("id");

		}
	}

	class ElementDragController extends PickupDragController {

		public ElementDragController(AbsolutePanel boundaryPanel,
				boolean allowDroppingOnBoundaryPanel) {
			super(boundaryPanel, allowDroppingOnBoundaryPanel);
		}

		@Override
		public void dragStart() {
			super.dragStart();
			MLElementWrapper wrap = (MLElementWrapper) context.draggable;
			wrap.setText(wrap.getElementWrapped().getInnerText());
		}

		@Override
		public void dragEnd() {
			super.dragEnd();
			MLElementWrapper wrap = (MLElementWrapper) context.draggable;
			wrap.setText("");
		}
	}
}
