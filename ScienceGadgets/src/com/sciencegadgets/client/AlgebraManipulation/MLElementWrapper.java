package com.sciencegadgets.client.AlgebraManipulation;

import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.HasDragHandle;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.HasDragStartHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This Widget is used to wrap elementary tags so mouse handlers can be attached
 * to them. Mainly used for MathML tags so equations can be manipulated
 * 
 * @author John Gralyan
 * 
 */
public class MLElementWrapper extends HTML implements HasMouseOutHandlers,
		HasMouseOverHandlers, HasDragStartHandlers {

	PickupDragController dragController = null;
	private Element element = null;
	private Boolean isDraggable;

	/**
	 * Construct that can explicitly state weather default handlers will be
	 * available
	 * <p>
	 * <b>Note - this widget can only be draggable if it's attached to an
	 * {@link AbsolutePanel}</b>
	 * </p>
	 * 
	 * @param theElement
	 *            - the element to wrap in widget
	 * @param addDefaultMouseOverOut
	 *            - if true, add the default {@link MouseOverHandler} and
	 *            {@link MouseOutHandler}
	 * @param isDraggable
	 *            - if true, adds a default drag handler
	 */
	public MLElementWrapper(Element theElement,
			Boolean addDefaultMouseOverOutHandler, Boolean isDraggable) {
		// setElement(theElement);
		// onAttach();
		this.setHTML(theElement.getInnerText());
		element = theElement;
		this.isDraggable = isDraggable;
		if (addDefaultMouseOverOutHandler) {
			addMouseOverHandlerDefault();
			addMouseOutHandlerDefault();
		}

	}

	public static MLElementWrapper getWrapByElementsType(Element element) {
		String tag = element.getNodeName();

		if (tag.equalsIgnoreCase("mi")) {
			return new MLElementWrapper(element, true, true);
		} else if (tag.equalsIgnoreCase("mo")) {
			return new MLElementWrapper(element, false, false);
		}
		return null;
	}

	public Element getElementWrapped() {
		return element;
	}

	/**
	 * Wraps all elements of a given tag name within an HTML widget in their own
	 * widgets in order to add handlers. Argument HTML widget must be added to
	 * the document before making this call
	 * 
	 * @param mathML
	 *            - the equation to be wrapped as mathML in an {@link HTML}
	 *            widget
	 */
	public static List<MLElementWrapper> wrapAll(HTML html, String Tag) {
		List<MLElementWrapper> wrappers = new LinkedList<MLElementWrapper>();

		NodeList<com.google.gwt.dom.client.Element> elementList = html
				.getElement().getElementsByTagName(Tag);

		for (int i = 0; i < elementList.getLength(); i++) {
			MLElementWrapper wrap = new MLElementWrapper(
					elementList.getItem(i), true, true);
			wrappers.add(wrap);
		}
		return wrappers;
	}

	/**
	 * Called when attaching Widget. If it is draggable, it can only be attached
	 * to an {@link AbsolutePanel}
	 */
	public void onAttach() {
		super.onAttach();

		if (isDraggable) {
			try {
				PickupDragController dragC = new PickupDragController(
						(AbsolutePanel) this.getParent(), true);
				addDragController(dragC);
			} catch (Exception e) {
			}
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

	// TODO experimental

	public HandlerRegistration addDragStartHandlerDefault() {
		return addDragStartHandler(new ElementDragStartHandler());
	}

	@Override
	public HandlerRegistration addDragStartHandler(DragStartHandler handler) {
		return addDomHandler(handler, DragStartEvent.getType());
	}

	/**
	 * Add a drag controller to this widget, can be a subclass of
	 * {@link AbstractDragController} such as {@link PickupDragController}
	 * <p>
	 * If a drag controller already exists, it is overridden
	 * </p>
	 * 
	 * @param dragCtrl
	 * @return
	 * @throws DragControllerException
	 */
	public PickupDragController addDragController(PickupDragController dragCtrl) {
		// if (dragController != null) {
		// throw new DragControllerException(
		// "There is already a drag controller, the old one is overridden");
		// }
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
	public void removeDragController(PickupDragController dragCtrl)
			throws Exception {
		if (dragController != null) {
			dragController.makeNotDraggable(this);
			dragController = null;
		} else {
			throw new DragControllerException(
					"There is no drag controller to remove");
		}
	}

	// TODO addDropContoller

	// public MathMLDropController addDropTarget(Widget target) throws
	// DragControllerException {
	// MathMLDropController dropCtrl = new MathMLDropController(target);
	// if(dragController != null){
	// dragController.registerDropController(dropCtrl);
	// }else{
	// throw new
	// DragControllerException("Must add a drag controller before a drop controller/target");
	// }
	// return dropCtrl;
	// }

	// ////////////////////////////////////////////////////////////////////////////////////
	// Inner Class Handlers
	// /////////////////////////////////////////////////////////////////////////////////////
	class ElementOverHandler implements MouseOverHandler {
		String colorOnOver = "blue";

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
			((Widget) event.getSource()).setStyleName("draggableOverlay", true);
			//DOM.setElementAttribute(((Widget) event.getSource()).getElement(),
			//		"mathbackground", colorOnOver);
		}
	}

	class ElementOutHandler implements MouseOutHandler {
		String colorOnOver = "white";

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
			((Widget) event.getSource()).setStyleName("draggableOverlay", false);
			//DOM.setElementAttribute(((Widget) event.getSource()).getElement(),
			//		"mathbackground", colorOnOver);
		}
	}

	class ElementDragStartHandler implements DragStartHandler {

		@Override
		public void onDragStart(DragStartEvent event) {
			// TODO just checking
			Window.alert("drag started");

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
