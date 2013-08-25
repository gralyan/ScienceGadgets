package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.algebra.MathMLBindingTree.MathMLBindingNode;

public class Wrapper extends HTML {

	public static Wrapper selectedWrapper;
	protected MathMLBindingNode node;
	protected EquationPanel eqPanel;
	protected EquationLayer eqLayer;
	public int paddingLeft = 0;
	public int paddingRight = 0;
	public Element element;
	protected VerticalPanel menu;

	public Wrapper(MathMLBindingNode node, EquationPanel eqPanel,
			EquationLayer eqLayer, Element element) {

		super(element);

		this.node = node;
		this.eqPanel = eqPanel;
		this.eqLayer = eqLayer;
		this.element = element;

		onAttach();
		
		node.wrap(this);
		node.getWrapper();

		this.setStylePrimaryName(node.getType().toString());

		Wrapper.selectedWrapper = EquationPanel.selectedWrapper;

		addClickHandler(new WrapperClickHandler());
		addMouseOverHandler(new WrapperMouseOverHandler());
		addTouchStartHandler(new WrapperTouchHandler());

	}

	public MathMLBindingNode getNode() {
		return node;
	}

	public EquationLayer getEqLayer() {
		return eqLayer;
	}

	public EquationPanel getEqPanel() {
		return eqPanel;
	}
	
	public VerticalPanel getMenu(){
		return menu;
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

		if (selectedWrapper != null) {
			if (this.equals(selectedWrapper)) {
				// If this was already selected, focus in on it
				if(node.getType().hasChildren()){
					eqPanel.setFocus(eqLayer);
				}
			} else {
				// If there is another selection, unselect it
				selectedWrapper.unselect();
			}
		}
		selectedWrapper = this;
		this.getElement().setAttribute("id", "selectedWrapper");
	}

	public void unselect() {
		selectedWrapper = null;
		this.getElement().removeAttribute("id");
	}

	@Override
	public int getOffsetWidth() {
		return super.getOffsetWidth();// +paddingLeft+paddingRight;
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

	class WrapperTouchHandler implements TouchStartHandler {

		@Override
		public void onTouchStart(TouchStartEvent event) {
			event.preventDefault();
			event.stopPropagation();
			select();
		}
	}

	class WrapperMouseOverHandler implements MouseOverHandler {
		@Override
		public void onMouseOver(MouseOverEvent event) {
			// Select if it wasn't selected before
			if (!((Wrapper) event.getSource()).equals(selectedWrapper)) {
				select();
			}
		}
	}
	
	public class WrapperMouseOutHandler implements MouseOutHandler {
		@Override
		public void onMouseOut(MouseOutEvent event) {
			// Unselect when mouse moves out
			if (((Wrapper) event.getSource()).equals(selectedWrapper)) {
				unselect();
			}
		}
	}

	// @Override
	// public HandlerRegistration addTouchStartHandler(TouchStartHandler
	// handler) {
	// return addDomHandler(handler, TouchStartEvent.getType());
	// }
	//
	// @Override
	// public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
	// {
	// return addDomHandler(handler, MouseOverEvent.getType());
	// }
	//
	// @Override
	// public HandlerRegistration addClickHandler(ClickHandler handler) {
	// return addDomHandler(handler, ClickEvent.getType());
	// }

}
