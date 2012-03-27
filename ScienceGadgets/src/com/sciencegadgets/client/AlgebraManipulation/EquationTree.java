package com.sciencegadgets.client.AlgebraManipulation;

import com.google.gwt.user.client.ui.Widget;

public class EquationTree extends Widget {
	EquationTreeNode firstNode;

	public EquationTreeNode getFirstNode() {
		return firstNode;
	}

	EquationTree(EquationTreeNode firstNode) {
		this.firstNode = firstNode;

	}
}
