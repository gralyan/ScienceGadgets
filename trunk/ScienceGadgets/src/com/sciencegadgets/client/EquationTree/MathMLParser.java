package com.sciencegadgets.client.EquationTree;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HTML;

public abstract class MathMLParser {
	public Node elLeft;
	public Node elEquals;
	public Node elRight;

	/**
	 * Abstract class whose subclass parses a MathML HTML, there will be 3 super
	 * field than can be used:
	 * <p>
	 * elLeft, elEquals, and elRight
	 * </p>
	 * 
	 * @param mathMLequation
	 */
	public MathMLParser(HTML mathMLequation) {

		NodeList<Node> sideEqSide = mathMLequation.getElement().getFirstChild()
				.getFirstChild().getChildNodes();
		elLeft = sideEqSide.getItem(0);
		elEquals = sideEqSide.getItem(1);
		elRight = sideEqSide.getItem(2);

		onRootsFound(elLeft, elEquals, elRight);

		addChildren((Element) elLeft, true);
		addChildren((Element) elRight, false);
	}

	/**
	 * Copies the node structure from mathML to an equation tree by recursively
	 * calling itself
	 * 
	 * @param fromMLN
	 * @param isLeft
	 */
	private void addChildren(Element mathMLNode, Boolean isLeft) {
mathMLNode.setId("mathroot");
		NodeList<Node> mathMLChildren = mathMLNode.getChildNodes();

		for (int i = 0; i < mathMLChildren.getLength(); i++) {
			Node currentNode = mathMLChildren.getItem(i);
			onVisitNode(currentNode, isLeft, i);

			if (currentNode.getChildCount() > 1) {
				addChildren((Element) currentNode, isLeft);
			}
			onGoingToNextChild (currentNode);
		}
	}
	

	/**
	 * This method is called when the roots are found. These roots are the sides
	 * of the equation along with the equals node
	 * 
	 * @param nodeLeft
	 * @param nodeEquals
	 * @param nodeRight
	 */
	protected abstract void onRootsFound(Node nodeLeft, Node nodeEquals,
			Node nodeRight);

	/**
	 * This method is called every time a node is found
	 * 
	 * @param currentNode
	 * @param isLeft
	 *            - true if this node is on the left side of the equation
	 *            (arbitrary boolean)
	 */
	protected abstract void onVisitNode(Node currentNode, Boolean isLeft, int indexOfSiblings);
	protected abstract void onGoingToNextChild (Node currentNode);
}
