package com.sciencegadgets.client.AlgebraManipulation;

import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.sciencegadgets.client.ScienceGadgets;
import com.sun.java.swing.plaf.windows.resources.windows;

public class EquationTree extends Tree {

	public LinkedList<MLElementWrapper> wrappersLeft = new LinkedList<MLElementWrapper>();
	public LinkedList<MLElementWrapper> wrappersRight = new LinkedList<MLElementWrapper>();
	public TreeItem rootLeftSide;
	public TreeItem rootRightSide;
	public HTML htmlLeft;
	public HTML htmlRight;

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

		NodeList<Node> side_Eq_Side = mathML.getElement().getFirstChild()
				.getFirstChild().getChildNodes();
		Element elLeft = (Element) side_Eq_Side.getItem(0);
		Element elRight = (Element) side_Eq_Side.getItem(2);
		
		
		/*Window.alert("\nmathMLHeight" + mathML.getOffsetHeight() + "\nmathMLHeight"
				+ mathML.getOffsetHeight() +"\nLeftHeight" + elLeft.getOffsetHeight() + "\nRightHeight"
				+ elRight.getOffsetHeight() + "\nLeftWidth"
				+ elLeft.getOffsetWidth() + "\nRightWidth"
				+ elRight.getOffsetWidth());
		*/// Window.alert(elLeft.getInnerHTML()+" is "+elRight.getInnerHTML());

		MLElementWrapper wrapLeft = MLElementWrapper.wrapperFactory(elLeft, true);
		MLElementWrapper wrapRight = MLElementWrapper.wrapperFactory(elRight, false);
		TreeItem rootLeftSide = this.addItem("$" + elLeft.getInnerText() + "$");
		TreeItem rootRightSide = this.addItem("$" + elRight.getInnerText()
				+ "$");

		wrappersLeft.add(wrapLeft);
		wrappersRight.add(wrapRight);
		addChildren(elLeft, rootLeftSide, true);
		addChildren(elRight, rootRightSide, false);
	}

	/**
	 * Copies the node structure from mathML to an equation tree
	 * 
	 * @param fromMLN
	 * @param toETN
	 */
	private void addChildren(Element fromMLN, TreeItem toETN, Boolean isLeft) {
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

			wrap = MLElementWrapper.wrapperFactory(
					fromMLchildrenEl.get(i), isLeft);

			if (wrap != null) {
				if (isLeft) {
					wrappersLeft.add(wrap);
				} else {
					wrappersRight.add(wrap);
				}
				toETNchild = toETN.addItem("$"
						+ wrap.getElementWrapped().getInnerText() + "$"
						+ "       " + wrap.getElementWrapped().getNodeName());
			}
			// Recursively call this method for each child
			addChildren(fromMLchildrenEl.get(i), toETNchild, isLeft);
		}
	}

}
