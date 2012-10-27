package com.sciencegadgets.client.equationtree;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class WrapperBackground extends HTML {
	
	public WrapperBackground(MathMLBindingNode node, String width, String height){
		super("");
		this.setWidth(width);
		this.setHeight(height);
		
		switch(node.getType()){
		case Term:
		case Sum:
		case Exponential:
		case Fraction:
		case Number:
		case Variable:
		case Operation:
		}
		this.setStyleName("wrap");
//		this.setUrl("images/images.jpeg");
	}

}
