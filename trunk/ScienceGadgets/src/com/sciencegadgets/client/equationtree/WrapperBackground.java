package com.sciencegadgets.client.equationtree;

import java.util.HashMap;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class WrapperBackground extends SimplePanel {

	public WrapperBackground(MathMLBindingNode node, String width, String height) {
		this.setWidth(width);
		this.setHeight(height);

		this.setStyleName(node.getType().toString());
		
	}

}
