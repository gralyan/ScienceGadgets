package com.sciencegadgets.client.AlgebraManipulation;

import java.util.List;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
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
import com.sciencegadgets.client.EquationTree.MathMLParser;

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
	private MLElementWrapper joinedWrapper;

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
	 * @param mouseOverStyle
	 *            - the slyle that will be added to element for
	 *            {@link MouseOverHandler} and {@link MouseOutHandler}
	 * @param isDraggable
	 *            - if true, adds a default drag {@link PickupDragController}
	 *            for the parent {@link AbsolutePanel} it is currently in
	 * @param isLeft
	 *            - Arbitrarily chosen distinction between sides. True if the
	 *            wrapper is on the left side of the equation, false if on the
	 *            right
	 */
	public MLElementWrapper(Element theElement, String mouseOverStyle,
			Boolean isDraggable, MLElementWrapper joinedWrapper) {
		this.element = theElement;
		this.isDraggable = isDraggable;
		addMouseOverHandlerDefault(mouseOverStyle);
		addMouseOutHandlerDefault(mouseOverStyle);

		if (joinedWrapper == null) {
			this.joinedWrapper = new MLElementWrapper(null,
					mouseOverStyle, isDraggable, this);
		} else {
			this.joinedWrapper = joinedWrapper;
		}
	}

	/**
	 * Construct that can explicitly state weather default handlers will be
	 * available.
	 * <p>
	 * <b>Note - this widget can only be draggable if it's attached to an
	 * {@link AbsolutePanel}</b>
	 * </p>
	 * 
	 * @param theElement
	 *            - the element to wrap in widget
	 * @param isDraggable
	 *            - if true, adds a default drag {@link PickupDragController}
	 *            for the parent {@link AbsolutePanel} it is currently in
	 * @param isLeft
	 *            - Arbitrarily chosen distinction between sides. True if the
	 *            wrapper is on the left side of the equation, false if on the
	 *            right
	 */
	public MLElementWrapper(Element theElement, Boolean isDraggable,
			 MLElementWrapper joinedWrapper) {
		// setElement(theElement);
		// onAttach();
		// this.setHTML(theElement.getInnerText());
		this.element = theElement;
		this.isDraggable = isDraggable;

		if (joinedWrapper == null) {
			this.joinedWrapper = new MLElementWrapper(null, isDraggable,
					 this);
		} else {
			this.joinedWrapper = joinedWrapper;
		}
	}

	public static MLElementWrapper wrapperFactory(Element element) {
		String tag = element.getNodeName();
		// TODO parse the mathML to apply the wrappers appropriately
		
		if(tag.equalsIgnoreCase("mn") | tag.equalsIgnoreCase("mi")){
		return new MLElementWrapper(element, "mouseOverlayNumber", true,
				 null);
		}
		return null;
		// if (tag.equalsIgnoreCase("mn")) {
		// return new MLElementWrapper(element, "mouseOverlayNumber", true,
		// isLeft);
		// } else if (tag.equalsIgnoreCase("mo") &
		// !element.getInnerText().equals("=")) {
		// return new MLElementWrapper(element, "mouseOverlayDefault", false,
		// isLeft);
		// }

		// return null;

	}

	public Element getElementWrapped() {
		return element;
	}

	public MLElementWrapper getJoinedWrapper() {
		return joinedWrapper;
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
	public HandlerRegistration addMouseOverHandlerDefault(String style) {
		return addMouseOverHandler(new ElementOverHandler(style));
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	// /////////////////////
	// addMouse-OUT-Handler handler methods
	// /////////////////////////////
	public HandlerRegistration addMouseOutHandlerDefault(String style) {
		return addMouseOutHandler(new ElementOutHandler(style));
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
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
		String styleOnOver = "mouseOverlayDefault";

		/**
		 * MouseOverHandler for elements with default highlight background color
		 */
		ElementOverHandler() {
		}

		/**
		 * MouseOverHandler for elements
		 */
		ElementOverHandler(String colorOnOver) {
			this.styleOnOver = colorOnOver;
		}

		public void onMouseOver(MouseOverEvent event) {
			((MLElementWrapper) event.getSource()).setStyleName(styleOnOver,
					true);
			((MLElementWrapper) event.getSource()).getJoinedWrapper()
					.setStyleName(styleOnOver, true);
			// DOM.setElementAttribute(((Widget)
			// event.getSource()).getElement(),
			// "mathbackground", colorOnOver);
		}
	}

	class ElementOutHandler implements MouseOutHandler {
		String styleOnOver = "mouseOverlayDefault";

		/**
		 * MouseOutHandler for elements with default return background color
		 * (white)
		 */
		ElementOutHandler() {
		}

		/**
		 * MouseOutHandler for elements
		 */
		ElementOutHandler(String styleOnOver) {
			this.styleOnOver = styleOnOver;
		}

		public void onMouseOut(MouseOutEvent event) {
			((MLElementWrapper) event.getSource()).setStyleName(styleOnOver,
					false);
			((MLElementWrapper) event.getSource()).getJoinedWrapper()
					.setStyleName(styleOnOver, false);
			// DOM.setElementAttribute(((Widget)
			// event.getSource()).getElement(),
			// "mathbackground", colorOnOver);
		}
	}

	class MLWrappingParser extends MathMLParser {

		private List<MLElementWrapper> wrappersLeft;
		private List<MLElementWrapper> wrappersRight;
		private MLElementWrapper wrap;

		public MLWrappingParser(HTML mathMLequation) {
			super(mathMLequation);

		}

		@Override
		protected void onRootsFound(Node nodeLeft, Node nodeEquals,
				Node nodeRight) {

			MLElementWrapper wrapLeft = MLElementWrapper.wrapperFactory(
					(Element) nodeLeft);
			MLElementWrapper wrapRight = MLElementWrapper.wrapperFactory(
					(Element) nodeRight);

			wrappersLeft.add(wrapLeft);
			wrappersRight.add(wrapRight);
		}

		@Override
		protected void onVisitNode(Node currentNode, Boolean isLeft,
				int indexOfSiblings) {
			wrap = MLElementWrapper.wrapperFactory((Element) currentNode);

			if (wrap != null) {
				if (isLeft) {
					wrappersLeft.add(wrap);
				} else {
					wrappersRight.add(wrap);
				}

			}

		}

		@Override
		protected void onGoingToNextChild(Node currentNode) {

		}
	}
}
