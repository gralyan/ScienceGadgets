package com.sciencegadgets.client.AlgebraManipulation;

import com.google.gwt.user.client.ui.Label;

public class EquationTreeNode {
	Label value;
	EquationTreeNode nextSibling = null;
	EquationTreeNode firstChild = null;
	
	EquationTreeNode(String value){
		this.value = new Label(value);
	}

	EquationTreeNode(Label value){
		this.value = value;
	}
	
	EquationTreeNode getNextSibling(){
		return nextSibling;
	}
	
	EquationTreeNode getFirstChild(){
		return firstChild;
	}
	void setNextSibling(String nextSiblingValue){
		this.nextSibling = new EquationTreeNode(nextSiblingValue);
	}
	void setNextSibling(Label nextSiblingValue){
		this.nextSibling = new EquationTreeNode(nextSiblingValue);
	}
	
	void setFirstChild(String firstChildValue){
		this.firstChild = new EquationTreeNode(firstChildValue);
	}
	void setFirstChild(Label firstChildValue){
		this.firstChild = new EquationTreeNode(firstChildValue);
	}
}
