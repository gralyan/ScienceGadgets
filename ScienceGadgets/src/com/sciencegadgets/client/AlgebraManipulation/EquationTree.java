package com.sciencegadgets.client.AlgebraManipulation;

import java.util.LinkedList;

import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
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
	 * Wraps the equation in widgets with handlers. Argument HTML widget must be
	 * added to the document before making this call
	 * 
	 * @param mathML
	 *            - the equation to be wrapped as mathML in an {@link HTML}
	 *            widget
	 */
	public static void wrapEquation(HTML mathML) {

		NodeList<com.google.gwt.dom.client.Element> elementList = mathML
				.getElement().getElementsByTagName("mrow");
		for (int i = 0; i < elementList.getLength(); i++) {
			MLElementWrapper wrapper = new MLElementWrapper(
					elementList.getItem(i), true);
		}
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
