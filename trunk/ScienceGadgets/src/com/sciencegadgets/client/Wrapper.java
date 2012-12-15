package com.sciencegadgets.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.equationtree.EquationLayer;
import com.sciencegadgets.client.equationtree.EquationPanel;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class Wrapper extends HTML /*implements HasClickHandlers,
		HasMouseOverHandlers, HasTouchStartHandlers*/ {

	public static Wrapper selectedWrapper;
	protected MathMLBindingNode node;
	protected EquationPanel eqPanel;
	protected EquationLayer eqLayer;
	public int paddingLeft = 0;
	public int paddingRight = 0;
	public Element element;

	public Wrapper(MathMLBindingNode node, EquationPanel eqPanel,
			EquationLayer eqLayer, Element element) {

		super(element);
		onAttach();
		
		this.setStyleName(node.getType().toString());
		
		this.node = node;
		this.eqPanel = eqPanel;
		this.eqLayer = eqLayer;
		this.element=element;
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
	
	public Wrapper getNextSiblingWrapper() throws IndexOutOfBoundsException{
		return node.getNextSibling().getWrapper();
	}
	
	public Wrapper getPrevSiblingWrapper() throws IndexOutOfBoundsException{
		return node.getPrevSibling().getWrapper();
	}
	
	public Wrapper getParentWrapper(){
		return node.getParent().getWrapper();
	}

	public void select() {
		
		if (selectedWrapper != null) {
			if (this.equals(selectedWrapper) && node.getType().hasChildren()) {
				//If this was already selected, focus in on it
				eqPanel.setFocus(eqLayer);
			} else {
				//If there is another selection, unselect it
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
		return super.getOffsetWidth();//+paddingLeft+paddingRight;
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
			//Select if it wasn't selected before
			if(!((Wrapper)event.getSource()).equals(selectedWrapper)){
				select();
			}
		}
	}

//	@Override
//	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
//		return addDomHandler(handler, TouchStartEvent.getType());
//	}
//
//	@Override
//	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
//		return addDomHandler(handler, MouseOverEvent.getType());
//	}
//
//	@Override
//	public HandlerRegistration addClickHandler(ClickHandler handler) {
//		return addDomHandler(handler, ClickEvent.getType());
//	}

}
