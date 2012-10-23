package com.sciencegadgets.client.equationtree;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.algebramanipulation.EquationTransporter;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class EditWrapper extends HTML{

	private MathMLBindingNode node;

	public EditWrapper(MathMLBindingNode node, String width, String height){
		setWidth(width);
		setHeight(height);
		this.node = node;
		addClickHandler(new EditWrapperClickHandler());
		addMouseOverHandler(new EditWrapperMouseOverHandler());
		addMouseOutHandler(new EditWrapperMouseOutHandler());
	}
	

	private class EditWrapperClickHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			
			if("mfenced".equals(node.getTag())){
			node.add("mi", Type.Number, "aaa");
				Window.alert("clicked");
			}
			EquationTransporter.selectEquation(node.getTree().getMathML(), "");
		}
	}
	
	private class EditWrapperMouseOverHandler implements MouseOverHandler{
		@Override
		public void onMouseOver(MouseOverEvent event) {
			((EditWrapper)event.getSource()).getElement().setAttribute("id", "selectedWrapper");
		}
	}
	private class EditWrapperMouseOutHandler implements MouseOutHandler{
		@Override
		public void onMouseOut(MouseOutEvent event) {
			((EditWrapper)event.getSource()).getElement().removeAttribute("id");
		}
	}
}
