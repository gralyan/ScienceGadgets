package com.sciencegadgets.client.AlgebraManipulation;

import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class EquationTree extends Tree {
	
	public LinkedList<MLElementWrapper> wrappers = new LinkedList<MLElementWrapper>();

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
		MLElementWrapper wrap = new MLElementWrapper(firstMLN, true);
		TreeItem firstENT = this.addItem( "$" + wrap.getElementWrapped().getInnerText() + "$");//firstMLN.toString());

		wrappers.add(wrap);
		addChildren(firstMLN, firstENT);
	}

	/**
	 * Copies the node structure from mathML to an equation tree
	 * 
	 * @param fromMLN
	 * @param toETN
	 */
	private void addChildren(Element fromMLN, TreeItem toETN) {
		NodeList<Node> fromMLchildren = fromMLN.getChildNodes();

		if (fromMLchildren == null) {
			return;
		}
		LinkedList<Element> fromMLchildrenEl = new LinkedList<Element>();
		TreeItem toETNchild = toETN;
		MLElementWrapper wrap;

		// First get all the child nodes and cast them as Elements
		for (int i = 0; i < fromMLchildren.getLength(); i++) {
			fromMLchildrenEl.add((Element) fromMLchildren.getItem(i));
		}
		// Add each child to the tree and wrap them in a MLElementWrapper widget
		for (int i = 0; i < fromMLchildrenEl.size(); i++) {
		//	TODO condition for fromMLchildrenEl.get(i) to restrict what is wrapped 

				wrap = new MLElementWrapper(fromMLchildrenEl.get(i), true);
				wrappers.add(wrap);
				toETNchild = toETN.addItem( "$" + wrap.getElementWrapped().getInnerText() + "$");
			
			// Recursively call this method for each child
			addChildren(fromMLchildrenEl.get(i), toETNchild);
		}
	}
}
