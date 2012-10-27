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

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Random;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;

public class MathMLBindingTree {

	private MathMLBindingTree tree = this;
	private MathMLBindingNode root;
	private MathMLBindingNode leftSide;
	private MathMLBindingNode equals;
	private MathMLBindingNode rightSide;
	private LinkedList<MLElementWrapper> wrappers = new LinkedList<MLElementWrapper>();
	private HashMap<String, MathMLBindingNode> idMap = new HashMap<String, MathMLBindingNode>();
	private HashMap<String, Node> idMLMap = new HashMap<String, Node>();
	private Node mathML;

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
	public MathMLBindingTree(Node mathML) throws TopNodesNotFoundException {
		this.mathML = mathML;

		bindMLtoNodes(mathML);

		this.wrapTree();
	}

	public Element getMathML() {
		Element clone = (Element) mathML.cloneNode(true);
		return clone;
	}

	public MathMLBindingNode getRoot() {
		return root;
	}

	public MathMLBindingNode getLeftSide() {
		return leftSide;
	}

	public MathMLBindingNode getRightSide() {
		return rightSide;
	}

	public void setLeftSide(MathMLBindingNode jNode) {
		leftSide = jNode;
	}

	public void setRightSide(MathMLBindingNode jNode) {
		rightSide = jNode;
	}

	public MathMLBindingNode getEquals() {
		return equals;
	}

	public LinkedList<MLElementWrapper> getWrappers() {
		return wrappers;
	}

	public LinkedList<MLElementWrapper> wrapTree() {

		Collection<MathMLBindingNode> children = idMap.values();
		MLElementWrapper wrap;
		wrappers.clear();

		for (MathMLBindingNode child : children) {

			wrap = new MLElementWrapper(child, true, true);
			child.setWrapper(wrap);
			wrappers.add(wrap);
		}
		return wrappers;
	}

	public MathMLBindingNode getNodeById(String id)
			throws NoSuchElementException {
		MathMLBindingNode node = idMap.get(id);
		if (node == null) {
			throw new NoSuchElementException("Can't get node by id: " + id);
		}
		return node;
	}

	public MathMLBindingNode NEW_NODE(Element mlNode) {
		return new MathMLBindingNode(mlNode);
	}

	public MathMLBindingNode NEW_NODE(Type type, String symbol) {
		return new MathMLBindingNode(type, symbol);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Node Class
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public class MathMLBindingNode {
		private Element mlNode;
		private MLElementWrapper wrapper;

		/**
		 * Wrap existing MathML node
		 */
		public MathMLBindingNode(Element mlNode) {

			if ("".equals(mlNode.getAttribute("id"))) {
				mlNode.setAttribute("id", Math.random() + "");
			}

			this.mlNode = mlNode;
		}

		/**
		 * Creates a new MathML DOM node which should be added into the MathML
		 * </br>get the DOM node with:
		 * <p>
		 * getMlNode()
		 * </p>
		 * ;
		 * 
		 * @param tag
		 *            - MathML tag
		 * @param symbol
		 *            - inner text
		 */
		public MathMLBindingNode(Type type, String symbol) {

			String tag = type.getTag();

			com.google.gwt.user.client.Element newNode = DOM.createElement(tag);
			newNode.setAttribute("id", Math.random() + "");

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
		public MathMLBindingNode encase(Type type) {

			MathMLBindingNode encasing = new MathMLBindingNode(type, "");

			// Move around nodes
			this.getParent().add(this.getIndex(), encasing);
			this.remove();
			encasing.add(this);

			return encasing;
		}

		/**
		 * Adds a child at the specified index.</br> Use index -1 to add end to
		 * the end of the child list</br></br> Children of this node must
		 * reflect it's type</br> <em>Requirements:</em></br> <b>Variable and
		 * Number</b> must have <b>exactly 0</b> children</br> <b>Term and
		 * Sum</b> must have <b>at least 2</b> children</br> <b>Fraction and
		 * Exponential</b> must have <b>exactly 2</b> children</br></br> The
		 * order should be:</br> <b>Term and Sum</b> - same as the order
		 * seen</br> <b>Fraction</b> - numerator then denominator</br>
		 * <b>Exponent</b> - base then exponent
		 * 
		 * @param index
		 *            - the placement of siblings
		 * @param newNode
		 *            - the node to be added
		 * @param children
		 *            - children of this added node
		 */
		public void add(int index, MathMLBindingNode newNode)
				throws IllegalArgumentException {

			Type newNodeType = newNode.getType();
			LinkedList<MathMLBindingNode> children = newNode.getChildren();
			int childCount = children.size();
			Element node = newNode.getMLNode();

			IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
					"Wrong number of children, type: " + newNodeType
							+ " can't have (" + childCount + ") children");

			switch (newNodeType) {
			case Number:// Confirm that the symbol is a number, then fall into
						// Variable
				try {
					Double.parseDouble(newNode.getSymbol());
				} catch (NumberFormatException e) {
					throw new NumberFormatException("The number node "
							+ newNode.toString() + " must have a number");
				}
			case Variable:// Confirm that there are no children
			case Operation:
				if (childCount != 0)
					throw illegalArgumentException;
				break;

			case Exponential:// Confirm that there are 2 children
			case Fraction:
				if (childCount != 2)
					throw illegalArgumentException;
				break;

			case Term:
				if (childCount < 2)
					throw illegalArgumentException;
				node.setAttribute("open", "");
				node.setAttribute("close", "");
			case Sum:// Confirm that there are multiple children
				node.setAttribute("separators", "");
				if (!Type.Term.equals(getType())) {
					node.setAttribute("open", "");
					node.setAttribute("close", "");
				} 
				break;
			}

			if (index < 0) {
				mlNode.appendChild(node);
			} else {
				Node referenceChild = mlNode.getChild(index);
				mlNode.insertBefore(node, referenceChild);
			}

			String id = newNode.getId();
			System.out.println("id"+id+"-");
			if (id == "") {
				id = Math.random() + "";
			}

			idMap.put(id, newNode);
			idMLMap.put(id, node);
		}

		/**
		 * Creates a node to add as a child at the specified index. Use index -1
		 * to append to the end of the child list
		 * 
		 * @return - The newly create child
		 * @throws Exception
		 */
		public MathMLBindingNode add(int index, Type type, String symbol)
				throws NoSuchElementException {
			MathMLBindingNode newNode = new MathMLBindingNode(type, symbol);
			this.add(index, newNode);
			return newNode;
		}

		public void add(MathMLBindingNode newNode) {
			add(-1, newNode);
		}

		public MathMLBindingNode add(Type type, String symbol)
				throws NoSuchElementException {
			return add(-1, type, symbol);
		}

		public LinkedList<MathMLBindingNode> getChildren() {
			NodeList<Node> childrenNodesList = getMLNode().getChildNodes();
			LinkedList<MathMLBindingNode> childrenNodes = new LinkedList<MathMLBindingNode>();

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

		public MathMLBindingNode getChildAt(int index) {
			Node node = getMLNode().getChildNodes().getItem(index);
			String id = ((Element) node).getId();
			return getNodeById(id);
		}

		public MathMLBindingNode getFirstChild() {
			return getChildAt(0);
		}

		public int getChildCount() {
			return mlNode.getChildCount();
		}

		public MathMLBindingNode getNextSibling() throws IndexOutOfBoundsException{
			return getSibling(1);
		}

		public MathMLBindingNode getPrevSibling() throws IndexOutOfBoundsException{
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
		private MathMLBindingNode getSibling(int indexesAway) throws IndexOutOfBoundsException {
			MathMLBindingNode parent = this.getParent();
			int siblingIndex = getIndex() + indexesAway;

			try {
				MathMLBindingNode sibling = parent.getChildAt(siblingIndex);
				return sibling;
			} catch (IndexOutOfBoundsException e) {
				throw new IndexOutOfBoundsException(
						"there is no child at index " + siblingIndex + ", "
								+ indexesAway + "indexes away from sibling: \n"
								+ this.toString() + "\n" + this);
			}
		}

		public void remove() {

			removeChildren(this);

			String id = getId();
			idMap.remove(id);
			idMLMap.remove(id);
			mlNode.removeFromParent();
		}

		private void removeChildren(MathMLBindingNode parent) {
			LinkedList<MathMLBindingNode> children = parent.getChildren();

			for (MathMLBindingNode child : children) {
				String id = child.getId();
				idMap.remove(id);
				idMLMap.remove(id);
			}
		}

		public int getIndex() {
			return this.getParent().getChildren().indexOf(this);
		}

		public MathMLBindingNode getParent() {
			Element parentElement = getMLNode().getParentElement();
			String parentId = parentElement.getAttribute("id");
			MathMLBindingNode parentNode = getNodeById(parentId);
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

		public void setWrapper(MLElementWrapper wrap) {
			wrapper = wrap;
		}

		public MLElementWrapper getWrapper() {
			return wrapper;
		}

		/**
		 * @return Tag of MathML DOM node in <b>Lower Case</b>
		 */
		public String getTag() {
			return mlNode.getTagName().toLowerCase();
		}

		public MathMLBindingTree getTree() {
			return tree;
		}

		public String getId() {
			return getMLNode().getAttribute("id");
		}

		// private Boolean isFunction() {
		// String nodeString = this.toString();
		// if ("cos".equals(nodeString) || "sin".equals(nodeString)
		// || "tan".equalsIgnoreCase(nodeString)
		// || "sec".equalsIgnoreCase(nodeString)
		// || "csc".equalsIgnoreCase(nodeString)
		// || "cot".equalsIgnoreCase(nodeString)
		// || "sinh".equalsIgnoreCase(nodeString)
		// || "cosh".equalsIgnoreCase(nodeString)
		// || "tanh".equalsIgnoreCase(nodeString)
		// || "log".equalsIgnoreCase(nodeString)
		// || "ln".equalsIgnoreCase(nodeString)) {
		// return true;
		// } else {
		// return false;
		// }
		// }

		public Type getType() throws NoSuchElementException {
			String tag = getTag();
			Type type = null;

			if ("mfenced".equals(tag)) {
				type = Type.Sum;
			} else if ("mrow".equals(tag)) {
				type = Type.Term;
			} else if ("mi".equals(tag)) {
				type = Type.Variable;
			} else if ("mn".equals(tag)) {
				type = Type.Number;
			} else if ("msup".equals(tag)) {
				type = Type.Exponential;
			} else if ("mfrac".equals(tag)) {
				type = Type.Fraction;
			} else if ("mo".equals(tag)) {
				type = Type.Operation;
			}

			if (type == null) {
				throw new NoSuchElementException(
						"There is no type for the tag: " + tag);
			}
			return type;
		}
	}

	public static enum Type {
		Term("mrow"), Sum("mfenced"), Exponential("msup"), Fraction("mfrac"), Variable(
				"mi"), Number("mn"), Operation("mo");

		private String tag;

		Type(String tag) {
			this.tag = tag;
		}

		public String getTag() {
			return tag;
		}
	}

	public static enum Operators {
		DOT("&middot"), SPACE("&nbsp;"), CROSS("&times;"), PLUS("+"), MINUS("-");

		private String sign;

		Operators(String sign) {
			this.sign = sign;
		}

		public String getSign() {
			return sign;
		}

		public static Operators getMultiply() {
			return CROSS;
		}
	}

	private void bindMLtoNodes(Node mathMLequation)
			throws TopNodesNotFoundException {

		// Find the top tree nodes: left side <mo>=<mo> right side
		Element rootNode = (Element) mathMLequation;
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

		this.root = new MathMLBindingNode(rootNode);
		this.leftSide = new MathMLBindingNode((Element) sideEqSide.getItem(0));
		this.equals = new MathMLBindingNode((Element) sideEqSide.getItem(1));
		this.rightSide = new MathMLBindingNode((Element) sideEqSide.getItem(2));

		addChildren(rootNode);
	}

	private void addChildren(Element mathMLNode) {
		NodeList<Node> mathMLChildren = (mathMLNode).getChildNodes();

		//TODO
		//MathML validation, nodes must have a certain number of children
//		String pTag = mathMLNode.getTagName();
//		if("mi".equals(pTag) || "mn".equals(pTag) || "mo".equals(pTag))
		
//		throw new InvalidParameterException(
//				"Exponent (msup tag) and fraction (mfrac) MathML nodes must have exactly 2 children, the following has: "
//						+ parent.getChildCount()
//						+ ":\n"
//						+ parent.toString());
			
		//Recursive binding of elements to this class
		for (int i = 0; i < mathMLChildren.getLength(); i++) {
			Element currentNode = (Element) mathMLChildren.getItem(i);

			// Nodes with no children are either inner text or formatting only
			if (currentNode.getChildCount() > 0) {

				String id = Random.nextInt() + "";

				currentNode.setAttribute("id", id);
				// String id = currentNode.getAttribute("id");

				idMLMap.put(id, currentNode);
				idMap.put(id, new MathMLBindingNode(currentNode));

				addChildren((Element) currentNode);
			}
		}
	}
}