package com.sciencegadgets.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.equationtree.EditWrapper;
import com.sciencegadgets.client.equationtree.EquationLayer;
import com.sciencegadgets.client.equationtree.EquationPanel;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class Wrapper extends HTML {
	
	public static Wrapper selectedWrapper;
	protected MathMLBindingNode node;
	protected EquationPanel eqPanel;
	protected EquationLayer eqLayer;
	
	public Wrapper(MathMLBindingNode node, EquationPanel eqPanel,
			EquationLayer eqLayer, String width, String height){
		setWidth(width);
		setHeight(height);

		this.node = node;
		this.eqPanel = eqPanel;
		this.eqLayer = eqLayer;
		Wrapper.selectedWrapper = EquationPanel.selectedWrapper;
		
		addClickHandler(new EditWrapperClickHandler());
		addMouseOverHandler(new EditWrapperMouseOverHandler());
		addTouchStartHandler(new EditWrapperTouchHandler());
	}
	
	public MathMLBindingNode getNode(){
		return node;
	}
	public EquationLayer getEqLayer(){
		return eqLayer;
	}
	public EquationPanel getEqPanel(){
		return eqPanel;
	}
	
	public void select(){

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
	public void unselect(){

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
//			thisWrapper.setStyleName("hoveredWrapper", true);
			select();
			
		}
	}
}