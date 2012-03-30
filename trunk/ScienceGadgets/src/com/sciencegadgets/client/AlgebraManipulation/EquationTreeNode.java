package com.sciencegadgets.client.AlgebraManipulation;

import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;

public class EquationTreeNode extends TreeItem {
	Label value;
	EquationTreeNode nextSibling = null;
	EquationTreeNode firstChild = null;
	
	EquationTreeNode(String value){
		this.value = new Label(value);
	}
	EquationTreeNode(Node node){
		this(node.getNodeName()+"-"+node.getNodeValue());
	}

	EquationTreeNode(Label value){
		this.value = value;
	}

}
