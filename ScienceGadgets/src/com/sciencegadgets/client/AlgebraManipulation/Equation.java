package com.sciencegadgets.client.AlgebraManipulation;

import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.HTML;

public class Equation {

	private EquationTreeNode curETNSib;

	
	//TODO recursive tree copy failure
	public EquationTree mathMLtoEquationTree(HTML mathML) {

		Node curMLSib = mathML.getElement().getFirstChild();
		curETNSib = new EquationTreeNode(
				curMLSib.getNodeValue());

		EquationTree eqTree = new EquationTree(curETNSib);

		while (curMLSib.getNextSibling() != null) {

			checkNext(curMLSib);

			curMLSib = curMLSib.getNextSibling();
			curETNSib.setNextSibling(curMLSib.getNodeValue());
		}

		return eqTree;
	}

	private void checkNext(Node curMLSib) {
		if (curMLSib.getFirstChild() != null) {
			checkNext(curMLSib.getFirstChild());

		}else{
			curETNSib.setNextSibling(curMLSib.getNodeValue());
		}
	}
}
