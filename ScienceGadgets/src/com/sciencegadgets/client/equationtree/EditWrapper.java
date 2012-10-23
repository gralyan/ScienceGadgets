package com.sciencegadgets.client.equationtree;

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
import com.sciencegadgets.client.algebramanipulation.EquationTransporter;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class EditWrapper extends HTML {

	private MathMLBindingNode node;
	private static HTML selectedWrapper;

	public EditWrapper(MathMLBindingNode node, HTML selectedWrapper, String width, String height) {
		setWidth(width);
		setHeight(height);
		this.node = node;
		this.selectedWrapper = selectedWrapper;
		
		addClickHandler(new EditWrapperClickHandler());
		addMouseOverHandler(new EditWrapperMouseOverHandler());
//		addMouseOutHandler(new EditWrapperMouseOutHandler());
		addTouchStartHandler(new EditWrapperTouchHandler());
	}

	private class EditWrapperClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {

			if ("mfenced".equals(node.getTag())) {
				node.add("mi", "aaa");
				Window.alert("clicked");
			}
			EquationTransporter.selectEquation(node.getTree().getMathML(), "");
		}
	}


	void select(Boolean select) {

		if (select) {
			selectedWrapper = this;
			this.getElement().setAttribute("id", "selectedWrapper");

		} else {// unselect
			selectedWrapper = null;
			this.getElement().removeAttribute("id");
		}
	}
	
	///////////////////////////////////////////////////////////////////////
	// Inner Classes
	//////////////////////////////////////////////////////////////////////
	private class EditWrapperMouseOverHandler implements MouseOverHandler {
		@Override
		public void onMouseOver(MouseOverEvent event) {
			if (selectedWrapper != null) {
				((EditWrapper)selectedWrapper).select(false);
			}
			select(true);
		}
	}
	
//	private class EditWrapperMouseOutHandler implements MouseOutHandler {
//		@Override
//		public void onMouseOut(MouseOutEvent event) {
////			select(false);
//		}
//	}
	private class EditWrapperTouchHandler implements TouchStartHandler {

		@Override
		public void onTouchStart(TouchStartEvent arg0) {
			if (selectedWrapper != null) {
				((EditWrapper)selectedWrapper).select(false);
			}
			select(true);
		}
	}
}
