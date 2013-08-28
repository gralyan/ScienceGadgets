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

import org.apache.commons.lang.math.Fraction;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebra.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.algebra.edit.RandomSpecification;

public class MathMLBindingTree {

	// private MathMLBindingTree tree = this;
	private MathMLBindingNode root;
	private LinkedList<Wrapper> wrappers = new LinkedList<Wrapper>();
	private HashMap<String, MathMLBindingNode> idMap = new HashMap<String, MathMLBindingNode>();
	private HashMap<String, Element> idMLMap = new HashMap<String, Element>();
	private Element mathML;
	private Element eqHTML;
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
	public MathMLBindingTree(Element mathML, boolean inEditMode)
			throws TopNodesNotFoundException {
		if (!inEditMode) {
			mathML = EquationRandomizer.randomizeNumbers(mathML);
		}

		this.mathML = mathML;
		this.inEditMode = inEditMode;

		bindMLtoNodes(mathML);

		eqHTML = EquationHTML.makeEquationHTML(this);

	}

	public Element getMathMLClone() {
		return (Element) mathML.cloneNode(true);
	}

	public Element getEqHTMLClone() {
		return (Element) eqHTML.cloneNode(true);
	}

	public MathMLBindingNode getRoot() {
		return root;
	}

	public MathMLBindingNode getLeftSide() {
		checkSideForm();
		return root.getChildAt(0);
	}

	public MathMLBindingNode getRightSide() {
		checkSideForm();
		return root.getChildAt(2);
	}

	public MathMLBindingNode getEquals() {
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
	
	public void reloadEqHTML(){
		eqHTML = EquationHTML.makeEquationHTML(this);
	}

	public LinkedList<Wrapper> getWrappers() {
		return wrappers;
	}

	public MathMLBindingNode getNodeById(String id)
			throws NoSuchElementException {
		MathMLBindingNode node = idMap.get(id);
		if (node == null) {
			JSNICalls.consoleError("Can't get node by id: " + id + "\n"
					+ getMathMLClone().getString());
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

	private String createId() {
		return "ML" + idCounter++;// Random.nextInt(2147483647);
	}

	public void validateTree() {
		for (MathMLBindingNode node : idMap.values()) {
			node.validate();
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Node Class
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public class MathMLBindingNode {
		private Element mlNode;
		private Wrapper wrapper;
		private Element nodeHTML;

		/**
		 * Wrap existing MathML node
		 */
		public MathMLBindingNode(Element mlNode) {

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
		public MathMLBindingNode(Type type, String symbol) {

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
		public MathMLBindingNode encase(Type type) {

			MathMLBindingNode encasing = new MathMLBindingNode(type, "");

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
		public void add(int index, MathMLBindingNode node)
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
			String id = ((Element) node).getAttribute("id");
			return getNodeById(id);
		}

		public MathMLBindingNode getFirstChild() {
			return getChildAt(0);
		}

		public int getChildCount() {
			return mlNode.getChildCount();
		}

		public MathMLBindingNode getNextSibling()
				throws IndexOutOfBoundsException {
			return getSibling(1);
		}

		public MathMLBindingNode getPrevSibling()
				throws IndexOutOfBoundsException {
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
		private MathMLBindingNode getSibling(int indexesAway)
				throws IndexOutOfBoundsException {
			MathMLBindingNode parent = this.getParent();
			int siblingIndex = getIndex() + indexesAway;

			try {
				MathMLBindingNode sibling = parent.getChildAt(siblingIndex);
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
			LinkedList<MathMLBindingNode> children = getChildren();

			for (MathMLBindingNode child : children) {
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

		public MathMLBindingNode getParent() {
			if ("math".equalsIgnoreCase(getTag())) {
				throw new NoSuchElementException(
						"Can't get the parent of a math tag because it's the root:\n"
								+ toString());
			}
			Element parentElement = getMLNode().getParentElement();
			String parentId = parentElement.getAttribute("id");
			MathMLBindingNode parentNode = getNodeById(parentId);
			return parentNode;
		}

		/**
		 * validates the proper number of children, numbers can be parsed, no
		 * sums within sums or terms within terms, and adds parentheses where
		 * needed
		 */
		public void validate() {

			int childCount = getChildCount();

			boolean isBadNumber = false, isWrongChildren = false, isSumception = false, isTermception = false;

			switch (getType()) {
			case Number:
				// Confirm that the symbol is a number or random number spec
				if (!inEditMode) {
					try {
						Double.parseDouble(getSymbol());
					} catch (NumberFormatException e) {
						isBadNumber = true;
					}
				}
				// no break
			case Variable:// Confirm that there are no children
			case Operation:
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
				Element elementNode = getMLNode();

				elementNode.setAttribute("separators", "");
				String open = "",
				close = "";

				switch (getParent().getType()) {
				case Sum:
					isSumception = true;
					break;
				case Term:
				case Exponential:
					open = "(";
					close = ")";
					break;
				}
				elementNode.setAttribute("open", open);
				elementNode.setAttribute("close", close);
				if (childCount < 3) {
					isWrongChildren = true;
				}
				break;

			case Term:// Confirm that there are < 3 children
				if (Type.Term.equals(getParent().getType())) {
					isTermception = true;
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

		public MathMLBindingTree getTree() {
			return MathMLBindingTree.this;
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

		public Operator getOperation() {
			if ("mo".equalsIgnoreCase(getTag())) {
				String symbol = getSymbol();

				for (Operator op : Operator.values()) {
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
			} else if ("math".equals(tag)) {
				type = Type.Equation;
			}

			if (type == null) {
				throw new NoSuchElementException(
						"There is no type for the tag: " + tag);
			}
			return type;
		}

		public Element getNodeHTML() {
			return nodeHTML;
		}

		public void setNodeHTML(Element nodeHTML) {
			this.nodeHTML = nodeHTML;
		}
	}

	public static enum Type {
		Term("mrow", true), Sum("mfenced", true), Exponential("msup", true), Fraction(
				"mfrac", true), Variable("mi", false), Number("mn", false), Operation(
				"mo", false), Equation("math", true);

		private String tag;
		private boolean hasChildren;
		public final String IN_PREFIX = "in-";

		Type(String tag, boolean hasChildren) {
			this.tag = tag;
			this.hasChildren = hasChildren;
		}
		
		public String asChild(){
			return (IN_PREFIX+toString().toLowerCase());
		}

		public String getTag() {
			return tag;
		}

		public boolean hasChildren() {
			return hasChildren;
		}

	}

	public static enum Operator {
		DOT("\u00B7", "&middot;"), SPACE("\u00A0", "&nbsp;"), CROSS("\u00D7",
				"&times;"), PLUS("+", "+"), MINUS("-", "-");

		private String sign;
		private String html;

		Operator(String sign, String html) {
			this.sign = sign;
			this.html = html;
		}

		public String getSign() {
			return sign;
		}

		public String getHTML() {
			return html;
		}

		public static Operator getMultiply() {
			return DOT;
		}
	}

	private void bindMLtoNodes(Node mathMLequation)
			throws TopNodesNotFoundException {

		// Find the top tree nodes: [left side] <mo>=<mo> [right side]
		Element rootNode = (Element) mathMLequation;
		root = new MathMLBindingNode(rootNode);

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
		idMap.put(id, new MathMLBindingNode(mathMLNode));

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