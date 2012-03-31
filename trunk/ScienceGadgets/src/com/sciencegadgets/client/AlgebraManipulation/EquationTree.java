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
		MLElementWrapper wrap = new MLElementWrapper(firstMLN, true, true);
		TreeItem firstENT = this.addItem( "$" + wrap.getElement().getInnerText() + "$");//firstMLN.toString());

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
		//LinkedList<MLElementWrapper> wrapps = new LinkedList<MLElementWrapper>();
		if (fromMLchildren.getLength() == 0) {
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
			if (fromMLchildrenEl.get(i).getTagName() != null
					&& fromMLchildrenEl.get(i).getTagName().equals("mi")) {

				wrap = new MLElementWrapper(fromMLchildrenEl.get(i), true, true);
				wrappers.add(wrap);
				toETNchild = toETN.addItem( "$" + wrap.getElement().getInnerText() + "$");
				//MLElementWrapper wrap2 = new MLElementWrapper(toETNchild.getElement(), true);
			}
			// Recursively call this method for each child
			addChildren(fromMLchildrenEl.get(i), toETNchild);
		}
	}
}
