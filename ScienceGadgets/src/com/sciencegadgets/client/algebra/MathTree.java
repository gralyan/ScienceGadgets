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
package com.sciencegadgets.client.algebra;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.DOM;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebra.Type.Operator;

public class MathTree {

	private MathNode root;
	private LinkedList<Wrapper> wrappers = new LinkedList<Wrapper>();
	private HashMap<String, MathNode> idMap = new HashMap<String, MathNode>();
	private HashMap<String, Element> idMLMap = new HashMap<String, Element>();
	private HashMap<String, Element> idHTMLMap = new HashMap<String, Element>();
	private Element mathML;
	private EquationHTML eqHTML;
	private boolean inEditMode;
	private int idCounter = 0;

	/**
	 * A tree representation of an equation.
	 * 
	 * @param mathML
	 *            - The equation written in MathML XML
	 * @param isParsedForMath
	 *            - If true, the tree is an abstract syntax tree that can be
	 *            manipulated as math. If false it is a tree of MathML as taken
	 *            from XML
	 * @throws TopNodesNotFoundException
	 */
	public MathTree(Element mathML, boolean inEditMode)
			throws TopNodesNotFoundException {
		if (!inEditMode) {
			mathML = EquationRandomizer.randomizeNumbers(mathML);
		}

		this.mathML = mathML;
		this.inEditMode = inEditMode;

		bindMLtoNodes(mathML);

		reloadEqHTML();
	}

	public Element getMathMLClone() {
		return (Element) mathML.cloneNode(true);
	}

	public EquationHTML getEqHTMLClone() {
		// return (Element) eqHTML.cloneNode(true);
		return new EquationHTML(mathML);
	}

	public MathNode getRoot() {
		return root;
	}

	public MathNode getLeftSide() {
		checkSideForm();
		return root.getChildAt(0);
	}

	public MathNode getRightSide() {
		checkSideForm();
		return root.getChildAt(2);
	}

	public MathNode getEquals() {
		checkSideForm();
		return root.getChildAt(1);
	}

	private void checkSideForm() {
		if (root.getChildCount() != 3) {
			JSNICalls
					.consoleError("root has too many children, not side=side: "
							+ getMathMLClone().getString());
		}
		if (!"=".equals(root.getChildAt(1).getSymbol())) {
			JSNICalls
					.consoleError("<mo>=</mo> isn't the root's second child, not side=side "
							+ getMathMLClone().getString());
		}
	}

	public void reloadEqHTML() {
		eqHTML = new EquationHTML(mathML);

		idHTMLMap.clear();

		NodeList<Element> allElements = eqHTML.getElement()
				.getElementsByTagName("*");
		for (int i = 0; i < allElements.getLength(); i++) {
			Element el = (Element) allElements.getItem(i).cloneNode(true);
			idHTMLMap.put(el.getAttribute("id"), el);
			el.removeAttribute("class");
			el.removeAttribute("id");
			el.getStyle().setDisplay(Display.INLINE_BLOCK);
		}
	}

	public LinkedList<Wrapper> getWrappers() {
		return wrappers;
	}

	public MathNode getNodeById(String id) throws NoSuchElementException {
		MathNode node = idMap.get(id);
		if (node == null) {
			JSNICalls.consoleError("Can't get node by id: " + id + "\n"
					+ getMathMLClone().getString());
			throw new NoSuchElementException("Can't get node by id: " + id);
		}
		return node;
	}

	public MathNode NEW_NODE(Element mlNode) {
		return new MathNode(mlNode);
	}

	public MathNode NEW_NODE(Type type, String symbol) {
		return new MathNode(type, symbol);
	}

	private String createId() {
		return "ML" + idCounter++;// Random.nextInt(2147483647);
	}

	public void validateTree() {

		for (MathNode node : idMap.values()) {
			node.validate();
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Node Class
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public class MathNode {
		private Element mlNode;
		private Wrapper wrapper;

		/**
		 * Wrap existing MathML node
		 */
		public MathNode(Element mlNode) {

			if ("".equals(mlNode.getAttribute("id"))) {
				mlNode.setAttribute("id", createId());
			}

			this.mlNode = mlNode;
		}

		/**
		 * Creates a new MathML DOM node which should be added into the MathML
		 * </br>get the DOM node with:
		 * <p>
		 * getMlNode()
		 * </p>
		 * 
		 * @param tag
		 *            - MathML tag
		 * @param symbol
		 *            - inner text
		 */
		public MathNode(Type type, String symbol) {

			String tag = type.getTag();

			com.google.gwt.user.client.Element newNode = DOM.createElement(tag);
			newNode.setAttribute("id", createId());

			if (!"".equals(symbol)) {
				newNode.setInnerText(symbol);
			}

			this.mlNode = newNode;
		}

		/**
		 * Adds a node between this node and its parent, encasing this branch of
		 * the tree in a new node. <br/>
		 * 
		 * @param tag
		 *            - the tag of the new node
		 * @return - encasing node
		 */
		public MathNode encase(Type type) {

			MathNode encasing = new MathNode(type, "");

			// Move around nodes
			this.getParent().add(this.getIndex(), encasing);
			encasing.add(-1, this);

			return encasing;
		}

		/**
		 * Adds a child at the specified index.</br> Use index -1 to add end to
		 * the end of the child list</br>If the node is already in the tree, it
		 * is just repositioned. There is no need to remove it first</br></br>
		 * Children of this node must reflect it's type</br>
		 * <em>Requirements:</em></br> <b>Variable and Number</b> must have
		 * <b>exactly 0</b> children</br> <b>Term and Sum</b> must have <b>at
		 * least 2</b> children</br> <b>Fraction and Exponential</b> must have
		 * <b>exactly 2</b> children</br></br> The order should be:</br> <b>Term
		 * and Sum</b> - same as the order seen</br> <b>Fraction</b> - numerator
		 * then denominator</br> <b>Exponent</b> - base then exponent
		 * 
		 * @param index
		 *            - the placement of siblings
		 * @param node
		 *            - the node to be added
		 * @param children
		 *            - children of this added node
		 */
		public void add(int index, MathNode node)
				throws IllegalArgumentException {

			Element elementNode = node.getMLNode();

			// Add node to DOM tree
			if (index < 0 || index >= mlNode.getChildCount()) {
				mlNode.appendChild(elementNode);
			} else {
				Node referenceChild = mlNode.getChild(index);
				mlNode.insertBefore(elementNode, referenceChild);
			}

			String id = node.getId();
			if (id == "") {
				id = createId();
			}

			// add node to binding map
			if (!idMap.containsKey(id)) {
				idMap.put(id, node);
				idMLMap.put(id, elementNode);
			}
		}

		/**
		 * Creates a node to add as a child at the specified index. Use index -1
		 * to append to the end of the child list
		 * 
		 * @return - The newly create child
		 * @throws Exception
		 */
		public MathNode add(int index, Type type, String symbol)
				throws NoSuchElementException {
			MathNode newNode = new MathNode(type, symbol);
			this.add(index, newNode);
			return newNode;
		}

		public void add(MathNode newNode) {
			add(-1, newNode);
		}

		public MathNode add(Type type, String symbol)
				throws NoSuchElementException {
			return add(-1, type, symbol);
		}

		public LinkedList<MathNode> getChildren() {
			NodeList<Node> childrenNodesList = getMLNode().getChildNodes();
			LinkedList<MathNode> childrenNodes = new LinkedList<MathNode>();

			for (int i = 0; i < childrenNodesList.getLength(); i++) {
				Node curNode = childrenNodesList.getItem(i);

				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = ((Element) curNode);
					String childId = childElement.getAttribute("id");
					childrenNodes.add(getNodeById(childId));
				}
			}
			return childrenNodes;
		}

		public MathNode getChildAt(int index) {
			Node node = getMLNode().getChildNodes().getItem(index);
			String id = ((Element) node).getAttribute("id");
			return getNodeById(id);
		}

		public MathNode getFirstChild() {
			return getChildAt(0);
		}

		public int getChildCount() {
			return mlNode.getChildCount();
		}

		public MathNode getNextSibling() throws IndexOutOfBoundsException {
			return getSibling(1);
		}

		public MathNode getPrevSibling() throws IndexOutOfBoundsException {
			return getSibling(-1);
		}

		/**
		 * This method gets the sibling at a position relative the this node
		 * 
		 * @param indexesAway
		 *            - the number of indexes away from this sibling positive
		 *            for siblings to the right, negative for siblings to the
		 *            left
		 *            <p>
		 *            ex:</br>-1 for previous </br>1 for next
		 *            </p>
		 * @return
		 */
		private MathNode getSibling(int indexesAway)
				throws IndexOutOfBoundsException {
			MathNode parent = this.getParent();
			int siblingIndex = getIndex() + indexesAway;

			try {
				MathNode sibling = parent.getChildAt(siblingIndex);
				return sibling;
			} catch (JavaScriptException e) {
				throw new IndexOutOfBoundsException(
						"there is no child at index " + siblingIndex + ", "
								+ indexesAway + "indexes away from sibling: \n"
								+ this.toString() + "\n" + this);
			}
		}

		public void remove() {
			removeChildren();

			String id = getId();
			idMap.remove(id);
			idMLMap.remove(id);
			mlNode.removeFromParent();
		}

		private void removeChildren() {
			LinkedList<MathNode> children = getChildren();

			for (MathNode child : children) {
				JSNICalls.consoleLog("Removing Nested child: "
						+ child.toString());
				String id = child.getId();
				idMap.remove(id);
				idMLMap.remove(id);
				child.removeChildren();
			}
		}

		public int getIndex() {
			return this.getParent().getChildren().indexOf(this);
		}

		public MathNode getParent() {
			if ("math".equalsIgnoreCase(getTag())) {
				throw new NoSuchElementException(
						"Can't get the parent of a math tag because it's the root:\n"
								+ toString());
			}
			Element parentElement = getMLNode().getParentElement();
			String parentId = parentElement.getAttribute("id");
			MathNode parentNode = getNodeById(parentId);
			return parentNode;
		}

		public Element getMLNode() {
			return mlNode;
		}

		public String toString() {
			return mlNode.getString();
		}

		public void setSymbol(String symbol) {
			mlNode.setInnerText(symbol);
		}

		public String getSymbol() {
			return mlNode.getInnerText();
		}

		public Wrapper wrap(Wrapper wrap) {
			wrapper = wrap;
			wrappers.add(wrapper);
			return wrapper;
		}

		public Wrapper getWrapper() {
			return wrapper;
		}

		/**
		 * @return Tag of MathML DOM node in <b>Lower Case</b>
		 */
		public String getTag() {
			return mlNode.getTagName().toLowerCase();
		}

		public MathTree getTree() {
			return MathTree.this;
		}

		public String getId() {
			return getMLNode().getAttribute("id");
		}

		public boolean isLeftSide() {
			if (this.equals(root.getChildAt(0)))
				return true;
			else
				return false;
		}

		public boolean isRightSide() {
			if (this.equals(root.getChildAt(2)))
				return true;
			else
				return false;
		}

		public Type.Operator getOperation() {
			if ("mo".equalsIgnoreCase(getTag())) {
				String symbol = getSymbol();

				for (Type.Operator op : Type.Operator.values()) {
					if (op.getSign().equalsIgnoreCase(symbol)
							|| op.getHTML().equalsIgnoreCase(symbol)) {
						return op;
					}
				}
			}
			// throw new
			// InvalidParameterException("Can't getOperation for this node: "+toString());
			return null;
		}

		public Type getType() {
			return Type.getType(getTag());
		}

		public Type getParentType() {
			String parentTag = getMLNode().getParentElement().getTagName();
			return Type.getType(parentTag);
		}

		public Element getHTMLElement() {
			Element el = idHTMLMap.get(getId());
			if (el == null) {
				JSNICalls.consoleWarn("No HTML for node: " + toString());
			}
			return (Element) el.cloneNode(true);
		}

		public String getHTMLString() {
			Element el = idHTMLMap.get(getId());
			if (el == null) {
				JSNICalls.consoleWarn("No HTML for node: " + toString());
			}
			return el.getString();
		}
		/**
		 * Validates the proper number of children, numbers can be parsed, no
		 * sums within sums or terms within terms, and collects nodes to
		 * decorate with {@link #decorateWithAesthetics()}
		 */
		private void validate() {

			int childCount = getChildCount();

			boolean isBadNumber = false, isWrongChildren = false, isSumception = false, isTermception = false;

			switch (getType()) {
			case Number:
				// Confirm that the symbol is a number in solve mode
				if (!inEditMode) {
					try {
						Double.parseDouble(getSymbol());
					} catch (NumberFormatException e) {
						isBadNumber = true;
					}
				}
				// no break
			case Variable:
				// no break
			case Operation:
				// Confirm that there are no children
				if (childCount != 1) {
					isWrongChildren = true;
				}
				break;
			case Exponential:// Confirm that there are 2 children
			case Fraction:
				if (childCount != 2) {
					isWrongChildren = true;
				}
				break;
			case Sum:
			case Term:// Confirm that there are < 3 children
				if (getType().equals(getParent().getType())) {
					if (Type.Term.equals(getType())) {
						isTermception = true;
					} else if (Type.Sum.equals(getType())) {
						isSumception = true;
					}
				}
				if (childCount < 3) {
					isWrongChildren = true;
				}
				break;
			case Equation:
				checkSideForm();
			}

			if (isBadNumber) {
				JSNICalls.consoleWarn("The number node " + toString()
						+ " must have a number");
			}
			if (isWrongChildren) {
				String errorMerrage = "Wrong number of children, type: "
						+ getType() + " can't have (" + childCount
						+ ") children: " + toString();
				JSNICalls.consoleError(errorMerrage);
				throw new IllegalArgumentException(errorMerrage);
			}
			if (isSumception) {
				JSNICalls.consoleError("There shouldn't be a sum in a sum: "
						+ getParent().toString());
			}
			if (isTermception) {
				JSNICalls.consoleError("There shouldn't be a term in a term"
						+ getParent().toString());
			}
			if (idMap.size() != idMLMap.size()) {
				JSNICalls
						.consoleError("The binding maps must have the same size: idMap.size()="
								+ idMap.size()
								+ " idMLMap.size()="
								+ idMLMap.size());
			}
		}


	}

	private void bindMLtoNodes(Node mathMLequation)
			throws TopNodesNotFoundException {

		// Find the top tree nodes: [left side] <mo>=<mo> [right side]
		Element rootNode = (Element) mathMLequation;
		root = new MathNode(rootNode);

		addRecursively(rootNode);

		validateTree();

		// // Prints both maps for debugging
		// System.out.println("idMLMap");
		// for (String key : idMLMap.keySet())
		// System.out.println(key + "\t" + idMLMap.get(key).getString());
		//
		// System.out.println("idMap");
		// for (String key : idMap.keySet())
		// System.out.println(key + "\t" + idMap.get(key).toString());
	}

	private void addRecursively(Element mathMLNode) {

		String id = createId();

		mathMLNode.setAttribute("id", id);

		idMLMap.put(id, mathMLNode);
		idMap.put(id, new MathNode(mathMLNode));

		NodeList<Node> mathMLChildren = (mathMLNode).getChildNodes();
		for (int i = 0; i < mathMLChildren.getLength(); i++) {
			Element currentNode = (Element) mathMLChildren.getItem(i);

			// Nodes with no children are either inner text or formatting only
			if (currentNode.getChildCount() > 0) {
				addRecursively((Element) currentNode);
			}
		}
	}

}