package com.sciencegadgets.client.AlgebraManipulation;

import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.HTML;


public class Equation {

	public void mathMLtoEquationTree(HTML mathML){
		
		Node a = mathML.getElement().getFirstChild();
		while(a.getNextSibling() != null){
		Node nextSib = a.getNextSibling();
		}
	}
}
