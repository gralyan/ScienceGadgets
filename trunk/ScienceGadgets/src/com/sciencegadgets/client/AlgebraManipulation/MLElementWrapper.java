package com.sciencegadgets.client.AlgebraManipulation;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This Widget is used to wrap elementary tags so mouse handlers can be attached
 * to them. Mainly used for MathML tags so equations can be manipulated
 * 
 * @author John Gralyan
 * 
 */
public class MLElementWrapper extends Widget implements HasMouseOutHandlers,
		HasMouseOverHandlers {

	PickupDragController dragController = null;

	/**
	 * Construct that takes care of all the nuisances of wrapping an element in
	 * a widget
	 * 
	 * @param theElement
	 *            - the element to wrap in widget
	 * @param addDefaultMouseOverOut
	 *            - if true, add the default {@link MouseOverHandler} and
	 *            {@link MouseOutHandler}
	 */
	public MLElementWrapper(Element theElement, Boolean addDefaultMouseOverOut) {
		setElement(theElement);
		onAttach();
		if (addDefaultMouseOverOut) {
			addMouseOverHandlerDefault();
			addMouseOutHandlerDefault();
		}
	}

	/**
	 * Must be called to attach widget. Already taken care of in constructors
	 */
	public void onAttach() {
		super.onAttach();
		RootPanel.detachOnWindowClose(this);
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
	 * {@link AbstractDragController} such as {@link PickupDragController}
	 * <p>If a drag controller already exists, it is overridden</p>
	 * 
	 * @param dragCtrl
	 * @return
	 * @throws DragControllerException 
	 */
	public PickupDragController addDragController(
			PickupDragController dragCtrl) throws DragControllerException {
		if(dragController !=null){
			throw new DragControllerException("There is already a drag controller, the old one is overridden");
		}
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
	public void removeDragController(PickupDragController dragCtrl) throws Exception {
		if (dragController != null) {
			dragController.makeNotDraggable(this);
			dragController = null;
		}else{
			throw new DragControllerException("There is no drag controller to remove");
			}
	}
	//TODO addDropContoller

//	public MathMLDropController addDropTarget(Widget target) throws DragControllerException {
//		MathMLDropController dropCtrl = new MathMLDropController(target);
//		if(dragController != null){
//		dragController.registerDropController(dropCtrl);
//		}else{
//			throw new DragControllerException("Must add a drag controller before a drop controller/target");
//		}
//		return dropCtrl;
//	}

	// //////////////////////////
	// Inner Class Handlers
	// //////////////////////
	class ElementOverHandler implements MouseOverHandler {
		String colorOnOver = "#9999ff";

		/**
		 * MouseOverHandler for elements with default highlight background color
		 */
		ElementOverHandler() {
		}

		/**
		 * MouseOverHandler for elements
		 */
		ElementOverHandler(String colorOnOver) {
			this.colorOnOver = colorOnOver;
		}

		public void onMouseOver(MouseOverEvent event) {
			DOM.setElementAttribute(((Widget) event.getSource()).getElement(),
					"mathbackground", colorOnOver);
		}
	}

	class ElementOutHandler implements MouseOutHandler {
		String colorOnOver = "#ffffff";

		/**
		 * MouseOutHandler for elements with default return background color
		 * (white)
		 */
		ElementOutHandler() {
		}

		/**
		 * MouseOutHandler for elements
		 */
		ElementOutHandler(String colorOnOver) {
			this.colorOnOver = colorOnOver;
		}

		public void onMouseOut(MouseOutEvent event) {
			DOM.setElementAttribute(((Widget) event.getSource()).getElement(),
					"mathbackground", colorOnOver);
		}
	}
	/**
	 * 
	 * Exception to be thrown when there is no drag controller on widget
	 * 
	 */
	class DragControllerException extends Exception {
		private static final long serialVersionUID = 3936417184515771756L;

		DragControllerException() {
		super();
	    }

		DragControllerException(String s) {
		super(s);
	    }
	}
}
