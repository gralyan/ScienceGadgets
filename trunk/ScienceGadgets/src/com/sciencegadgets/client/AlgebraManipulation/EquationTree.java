package com.sciencegadgets.client.AlgebraManipulation;

import java.util.LinkedList;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class EquationTree extends Tree {

	public EquationTree() {
		super();
	}

	/**
	 * Makes an equationTree from mathML
	 * @param mathML
	 */
	public EquationTree(HTML mathML) {
		super();

		Node firstMLN = mathML.getElement().getFirstChild();
		TreeItem firstENT = this.addItem(firstMLN.getNodeName() + "-"
				+ firstMLN.getNodeValue());

		addChildren(firstMLN, firstENT);
	}

	/**
	 * Recursively copies the node structure from mathML to an equation tree 
	 * @param fromMLN
	 * @param toETN
	 */
	private void addChildren(Node fromMLN, TreeItem toETN) {
		NodeList<Node> fromMLchildren = fromMLN.getChildNodes();
		TreeItem curItem;

		for (int i = 0; i < fromMLchildren.getLength(); i++) {
			curItem = toETN.addItem(nodeToString(fromMLchildren.getItem(i)));
			addChildren(fromMLchildren.getItem(i), curItem);
		}
	}
	private static String nodeToString(Node node) {
		return "<span>" + node.getNodeName() + "-" + node.getNodeValue()+"-"+node.getNodeType()
				+ "</span>";
	}



}
