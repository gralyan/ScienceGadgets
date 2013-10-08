package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.UIObject;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.edit.EditWrapper;

public class Wrapper extends HTML {

	protected MathNode node;
	protected EquationPanel eqPanel;
	// protected EquationLayer eqLayer;
	public int paddingLeft = 0;
	public int paddingRight = 0;
	public Element element;
	protected FlowPanel menu;

	public Wrapper(MathNode node, final EquationPanel eqPanel, Element element) {
		super(element);

		this.node = node;
		this.eqPanel = eqPanel;
		this.element = element;

		onAttach();

		node.wrap(this);
		node.getWrapper();

		this.addStyleName("displayWrapper");

		// zIndex eqPanel=1 wrapper=2 menu=3
		this.getElement().getStyle().setZIndex(2);

		addClickHandler(new WrapperClickHandler());
		addTouchEndHandler(new WrapperTouchHandler());
		addTouchStartHandler(new WrapperTouchStartHandler());
		

	}

	public MathNode getNode() {
		return node;
	}

	public EquationLayer getEqLayer() {
		return eqPanel.eqLayerMap.get(node);
	}

	public EquationPanel getEqPanel() {
		return eqPanel;
	}

	// public VerticalPanel getMenu(){
	// return menu;
	// }

	public Wrapper getNextSiblingWrapper() throws IndexOutOfBoundsException {
		return node.getNextSibling().getWrapper();
	}

	public Wrapper getPrevSiblingWrapper() throws IndexOutOfBoundsException {
		return node.getPrevSibling().getWrapper();
	}

	public Wrapper getParentWrapper() {
		return node.getParent().getWrapper();
	}

	// Public selection methods, calls subclasses approprimathNodeately
	public void select(boolean inEditMode) {

		if (this.equals(EquationPanel.selectedWrapper)) {

			// If this was already selected, focus in on it
			if (node.getType().hasChildren()) {
				this.unselect(Moderator.inEditMode);
				eqPanel.setFocus(getEqLayer());
			}
		} else {

			// If there is another selection, unselect it
			if (EquationPanel.selectedWrapper != null) {
				EquationPanel.selectedWrapper.unselect(Moderator.inEditMode);
			}

			Moderator.contextMenuArea.add(menu);

			if (inEditMode) {
				((EditWrapper) this).select();
			} else {
				((MathWrapper) this).select();
			}
		}
	}

	public void unselect(boolean inEditMode) {

		Moderator.contextMenuArea.clear();

		if (inEditMode) {
			((EditWrapper) this).unselect();
		} else {
			((MathWrapper) this).unselect();
		}
	}

	protected void select() {

			EquationPanel.selectedWrapper = this;
			this.getElement().addClassName("selectedWrapper");
	}

	protected void unselect() {

			EquationPanel.selectedWrapper = null;
			this.getElement().removeClassName("selectedWrapper");
	}

	// /////////////////////////////////////////////////////////////////////
	// Inner Classes
	// ////////////////////////////////////////////////////////////////////
	public class WrapperClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			event.preventDefault();
			event.stopPropagation();
			select(Moderator.inEditMode);
		}
	}

	class WrapperTouchHandler implements TouchEndHandler {

		@Override
		public void onTouchEnd(TouchEndEvent event) {
			event.preventDefault();
			event.stopPropagation();
			select(Moderator.inEditMode);
		}
	}
	
	// Sole purpose is to detect touchability, then unsink click and itself
	class WrapperTouchStartHandler implements TouchStartHandler {
		@Override
		public void onTouchStart(TouchStartEvent event) {
			eqPanel.unsinkClicks();
		}
	}
	

}
