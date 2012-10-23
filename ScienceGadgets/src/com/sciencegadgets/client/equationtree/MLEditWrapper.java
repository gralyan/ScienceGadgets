package com.sciencegadgets.client.equationtree;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.algebramanipulation.EquationTransporter;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class MLEditWrapper extends HTML{

	private MathMLBindingNode node;

	public MLEditWrapper(MathMLBindingNode node, String width, String height){
		setWidth(width);
		setHeight(height);
		setStyleName("var");
		this.node = node;
		addClickHandler(new MLEditWrapperClickHandler());
	}
	

	private class MLEditWrapperClickHandler implements ClickHandler{

		@Override
		public void onClick(ClickEvent event) {
			
			if("mfenced".equals(node.getTag())){
//			node.add("mi", Type.Number, "aaa");
				Window.alert("clicked");
			}
//			EquationTransporter.selectEquation(node.getTree().getMathML(), "");
		}
		
	}
}
