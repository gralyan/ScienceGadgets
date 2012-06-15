package com.sciencegadgets.client.algebramanipulation;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.AbstractMathDropController;
import com.sciencegadgets.client.equationtree.JohnTree;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

/**
 * This Widget is used to wrap elementary tags so mouse handlers can be attached
 * to them. Mainly used for MathML tags so equations can be manipulated
 * 
 * @author John Gralyan
 * 
 */
public class MLElementWrapper extends HTML {

	private WrapDragController dragController = null;
	private Element element = null;
	private Boolean isDraggable;
	private MLElementWrapper joinedWrapper;
	private JohnNode johnNode;
	private HTML dropDescriptor = new HTML();

	private static MLElementWrapper selectedWrapper;
	private static MLElementWrapper SelectedWrapperJoiner;

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

	public HTML getDropDescriptor() {
		return dropDescriptor;
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

	public WrapDragController getDragControl() {
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

	public HandlerRegistration addClickHandler() {
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

	public void removeDropTargets() {
		dragController.unregisterDropControllers();
		dragController.getDropList().clear();
	}

	// ////////////////////////////////////////////////////////////////////////////////////
	// Inner Class Handlers
	// /////////////////////////////////////////////////////////////////////////////////////
	class ElementOverHandler implements MouseOverHandler {
		public void onMouseOver(MouseOverEvent event) {
			select(true);
		}
	}

	class ElementOutHandler implements MouseOutHandler {
		public void onMouseOut(MouseOutEvent event) {
			select(false);
		}
	}

	class ElementClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			if (selectedWrapper != null) {
				selectedWrapper.select(false);
			}
			select(true);
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
	void select(Boolean select) {
		MLElementWrapper wrapper = this;

		if (select) {
			// Highlights selected
			wrapper.getElement().setId("selectedWrapper");
			wrapper.getJoinedWrapper().getElement().setId("selectedWrapper");

			// Save selected statically
			selectedWrapper = this;
			SelectedWrapperJoiner = this.getJoinedWrapper();

			String path = "com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropController_";
			String changeDesc = "";
			
			for (DropController dropC : wrapper.dragController.getDropList()) {

				String className = dropC.getClass().getName();
				String change = ((AbstractMathDropController) dropC)
						.findChange(getJohnNode());

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
					changeDesc = "<b>Divide &nbsp; " + change
							+ "</b><br/>on both sides";

				} else {
					changeDesc = "???";
				}

				// Highlights drop targets
				MLElementWrapper dropCwrap = ((MLElementWrapper) dropC
						.getDropTarget());
				dropC.getDropTarget().setStyleName("selectedDropWrapper");
				dropCwrap.getJoinedWrapper().setStyleName("selectedDropWrapper");

				// Descriptors
				HTML dropDesc = dropCwrap.getDropDescriptor();
				dropDesc.setHTML(changeDesc);
				dropDesc.setStyleName("dropDescriptor");
			}

		} else {
			// Removes style - selectedWrapper
			wrapper.getElement().removeAttribute("id");
			wrapper.getJoinedWrapper().getElement().removeAttribute("id");

			// Removes static reference to this as selected
			selectedWrapper = null;
			SelectedWrapperJoiner = null;

			for (DropController dropC : wrapper.dragController.getDropList()) {

				String[] styles = dropC.getDropTarget().getStyleName()
						.split(" ");

				MLElementWrapper dropCwrap = (MLElementWrapper) dropC
						.getDropTarget();

				for (String style : styles) {
					if (style.startsWith("selectedDropWrapper")) {
						dropCwrap.removeStyleName(style);
						dropCwrap.getJoinedWrapper().removeStyleName(style);
					}

					HTML dropDesc = dropCwrap.getDropDescriptor();
					dropDesc.setText("");
					dropDesc.removeStyleName("dropDescriptor");
				}
			}
		}
	}
}
