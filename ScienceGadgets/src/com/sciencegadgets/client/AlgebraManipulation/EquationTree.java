package com.sciencegadgets.client.AlgebraManipulation;

import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

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
		splitSides(mathML);

		// Element firstMLN = mathML.getElement().getFirstChildElement();
		MLElementWrapper wrapLeft = new MLElementWrapper(htmlLeft.getElement(),
				false, true);
		MLElementWrapper wrapRight = new MLElementWrapper(
				htmlRight.getElement(), false, false);
		TreeItem rootLeftSide = this.addItem("$"
				+ wrapLeft.getElementWrapped().getInnerText() + "$");
		TreeItem rootRightSide = this.addItem("$"
				+ wrapRight.getElementWrapped().getInnerText() + "$");

		wrappersLeft.add(wrapLeft);
		wrappersRight.add(wrapRight);
		addChildren(htmlLeft.getElement(), rootLeftSide, true);
		addChildren(htmlRight.getElement(), rootRightSide, false);
	}

	private void splitSides(HTML wholeEq) {
		String divTag = wholeEq.getElement().toString().split(">", 2)[0] + ">";
		String mathTag = wholeEq.getElement().toString().split(">", 3)[1] + ">";

		String[] sideStrings = wholeEq.toString().split("<mo>=</mo>");

		String leftString = sideStrings[0].replaceFirst(divTag, "")
				+ "</mrow></math>";
		String rightString = mathTag + "<mrow>"
				+ sideStrings[1].replaceFirst("</div>", "");

		htmlLeft = new HTML(leftString);
		htmlRight = new HTML(rightString);
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

			wrap = MLElementWrapper.getWrapByElementType(fromMLchildrenEl
					.get(i), isLeft);

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
