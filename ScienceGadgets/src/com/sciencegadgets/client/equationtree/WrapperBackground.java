package com.sciencegadgets.client.equationtree;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class WrapperBackground extends SimplePanel {
	
	public WrapperBackground(MathMLBindingNode node, String width, String height){
//		super("");
		this.setWidth(width);
		this.setHeight(height);
		
//		String color = "black";
//		switch(node.getType()){
//		case Term:
//			color = "blue";
//			break;
//		case Sum:
//			color = "green";
//			break;
//		case Exponential:
//			color = "red";
//			break;
//		case Fraction:
//			color = "yellow";
//			break;
//		case Number:
//			color = "pink";
//			break;
//		case Variable:
//			color = "orange";
//			break;
//		case Operation:
//			color = "violet";
//			break;
//		}
		this.setStyleName(node.getType().toString());
//		this.setStyleName("wrap");
//		this.setUrl("images/images.jpeg");
	}

}
