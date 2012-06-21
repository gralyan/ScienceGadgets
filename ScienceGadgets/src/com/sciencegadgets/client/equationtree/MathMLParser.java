/*   Copyright 2012 John Gralyan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sciencegadgets.client.equationtree;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.TopNodesNotFoundException;

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
	 * @throws TopNodesNotFoundException
	 */
	public MathMLParser(HTML mathMLequation) throws TopNodesNotFoundException {

		// Find the top tree nodes
		Element rootNode = mathMLequation.getElement();
		String middleString = "";

		while (!"=".equals(middleString)) {
			switch (rootNode.getChildCount()) {
			case 0: // prevent infinite loop
				throw new TopNodesNotFoundException(
						"The MathML is invalid, It must contain the following pattern for the top layer of the equation:"
								+ "\n<mrow>[left side of eqation]</mrow>"
								+ "\n\t<mo>=<mo>"
								+ "\n<mrow>[right side of eqation]</mrow>\n");
			case 1:
				rootNode = rootNode.getFirstChildElement();
				break;
			default:
				middleString = ((Element) rootNode.getChild(1)).getInnerText();
				if (!"=".equals(middleString)) {
					rootNode = rootNode.getFirstChildElement();
				}
			}
		}

		NodeList<Node> sideEqSide = rootNode.getChildNodes();

		elLeft = sideEqSide.getItem(0);
		elEquals = sideEqSide.getItem(1);
		elRight = sideEqSide.getItem(2);

		onRootsFound(elLeft, elEquals, elRight);

		addChildren(elLeft, true);
		addChildren(elRight, false);
	}

	/**
	 * Copies the node structure from mathML to an equation tree by recursively
	 * calling itself
	 * 
	 * @param fromMLN
	 * @param isLeft
	 */
	private void addChildren(Node mathMLNode, Boolean isLeft) {
		// mathMLNode.setId("mathroot");
		NodeList<Node> mathMLChildren = ((Element) mathMLNode).getChildNodes();

		for (int i = 0; i < mathMLChildren.getLength(); i++) {
			Node currentNode = mathMLChildren.getItem(i);

			// Nodes with no children are either inner text or formatting only
			if (currentNode.getChildCount() > 0) {

				// Subclasses do whatever they want with the node
				onVisitNode(currentNode, isLeft, i);

				// Recursive call
				addChildren((Element) currentNode, isLeft);

				// Returning from the stack
				onGoingToNextChild(currentNode);
			}
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
	protected abstract void onVisitNode(Node currentNode, Boolean isLeft,
			int indexOfSiblings);

	protected abstract void onGoingToNextChild(Node currentNode);
}
