package com.sciencegadgets.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.sciencegadgets.client.equationtree.EditWrapper;
import com.sciencegadgets.client.equationtree.EquationLayer;
import com.sciencegadgets.client.equationtree.EquationPanel;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class Wrapper extends HTML implements HasClickHandlers,
		HasMouseOverHandlers, HasTouchStartHandlers {

	public static Wrapper selectedWrapper;
	protected MathMLBindingNode node;
	protected EquationPanel eqPanel;
	protected EquationLayer eqLayer;
	protected Element svg;

	public Wrapper(MathMLBindingNode node, EquationPanel eqPanel,
			EquationLayer eqLayer, String width, String height, Element svg) {
		setWidth(width);
		setHeight(height);

		this.svg = svg;
		this.node = node;
		this.eqPanel = eqPanel;
		this.eqLayer = eqLayer;
		Wrapper.selectedWrapper = EquationPanel.selectedWrapper;

		addClickHandler(new EditWrapperClickHandler());
		addMouseOverHandler(new EditWrapperMouseOverHandler());
		addTouchStartHandler(new EditWrapperTouchHandler());
	}
	
	public Element getSVG(){
		return svg;
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

	public void select() {

		if (selectedWrapper != null) {
			if (this.equals(selectedWrapper) && node.getType().hasChildren()) {
				eqPanel.setFocus(eqLayer);
			} else {
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

	// /////////////////////////////////////////////////////////////////////
	// Inner Classes
	// ////////////////////////////////////////////////////////////////////
	class EditWrapperClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			event.preventDefault();
			event.stopPropagation();
			select();
		}
	}

	class EditWrapperTouchHandler implements TouchStartHandler {

		@Override
		public void onTouchStart(TouchStartEvent event) {
			event.preventDefault();
			event.stopPropagation();
			select();
		}
	}

	class EditWrapperMouseOverHandler implements MouseOverHandler {
		@Override
		public void onMouseOver(MouseOverEvent event) {
			// thisWrapper.setStyleName("hoveredWrapper", true);
			select();

		}
	}

	@Override
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return addDomHandler(handler, TouchStartEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

}
