package com.sciencegadgets.client.AlgebraManipulation;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Element;
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
	 * 
	 * @param mathML
	 */
	public EquationTree(HTML mathML) {
		super();

		Element firstMLN = mathML.getElement().getFirstChildElement();
		TreeItem firstENT = this.addItem(firstMLN.toString());

		addChildren(firstMLN, firstENT);
	}

	/**
	 * Copies the node structure from mathML to an equation tree
	 * 
	 * @param fromMLN
	 * @param toETN
	 */
	private List<MLElementWrapper> addChildren(Element fromMLN, TreeItem toETN) {
		NodeList<Node> fromMLchildren = fromMLN.getChildNodes();
		LinkedList<Element> fromMLchildrenEl = new LinkedList<Element>();
		List<MLElementWrapper> wrappers = new LinkedList<MLElementWrapper>();
				// If this child doesn't get added to the tree, add the new
				// children to the last TreeItem
		TreeItem toETNchild = toETN;
		MLElementWrapper wrap;

		// First get all the child nodes and cast them as Elements
		for (int i = 0; i < fromMLchildren.getLength(); i++) {
			fromMLchildrenEl.add((Element) fromMLchildren.getItem(i));
		}
		// Then add each child to the tree and wrap them in a MLElementWrapper
		// widget
		for (int i = 0; i < fromMLchildrenEl.size(); i++) {
			
			//if (fromMLchildrenEl.get(i).getTagName().equals("mrow")) {
				wrap = new MLElementWrapper(fromMLchildrenEl.get(i), true);
				wrappers.add(wrap);
				toETNchild = toETN.addItem(wrap);// "$" + wrap.toString() +
													// "$");
			//}
			// Recursively call this method for each child
			addChildren(fromMLchildrenEl.get(i), toETNchild);
		}
		return wrappers;
	}
	/*
	 * private static String nodeToString(Node node) { return "<span>" +
	 * node.getNodeName() + "-" + node.getNodeValue()+"-"+node.getNodeType() +
	 * "</span>"; }
	 */

}
