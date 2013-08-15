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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Random;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebra.edit.RandomSpecification;

public class MathMLBindingTree {

//	private MathMLBindingTree tree = this;
	private MathMLBindingNode root;
	private MathMLBindingNode leftSide;
	private MathMLBindingNode equals;
	private MathMLBindingNode rightSide;
	private LinkedList<Wrapper> wrappers = new LinkedList<Wrapper>();
	private HashMap<String, MathMLBindingNode> idMap = new HashMap<String, MathMLBindingNode>();
	private HashMap<String, Element> idMLMap = new HashMap<String, Element>();
	private Element mathML;
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

	public LinkedList<Wrapper> getWrappers() {
		return wrappers;
	}

	public MathMLBindingNode getNodeById(String id)
			throws NoSuchElementException {
		MathMLBindingNode node = idMap.get(id);
		if (node == null) {
			JSNICalls.consoleError("Can't get node by id: " + id);
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
	
	private String createId(){
		return "ML"+ idCounter++;//Random.nextInt(2147483647);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Node Class
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public class MathMLBindingNode {
		private Element mlNode;
		private Wrapper wrapper;
		private Element SVGElement;
		private boolean isLeftSide = false;
		private boolean isRightSide = false;

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
		 * <p>getMlNode()</p>
		 * 
		 * @param tag - MathML tag
		 * @param symbol - inner text
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
		 * @param tag - the tag of the new node
		 * @return - encasing node
		 */
		public MathMLBindingNode encase(Type type) {

			MathMLBindingNode encasing = new MathMLBindingNode(type, "");

			// Move around nodes
			this.getParent().add(this.getIndex(), encasing);
			this.remove();
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

			Type newNodeType = node.getType();
			LinkedList<MathMLBindingNode> children = node.getChildren();
			int childCount = children.size();
			Element elementNode = node.getMLNode();

			IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
					"Wrong number of children, type: " + newNodeType
							+ " can't have (" + childCount + ") children");

			switch (newNodeType) {
			case Number:
				// Confirm that the symbol is a number or random number spec
				// then fall into Variable
				if (!node.getSymbol().equals(
						RandomSpecification.RANDOM_SYMBOL)) {
					try {
						Double.parseDouble(node.getSymbol());
					} catch (NumberFormatException e) {
//						throw new NumberFormatException("The number node "
//								+ node.toString() + " must have a number");
						JSNICalls.consoleWarn("The number node "+ node.toString() + " must have a number");
					}
				}
			case Variable:// Confirm that there are no children
			case Operation:
				if (childCount != 0){
					JSNICalls.consoleError("Wrong number of children, type: " + newNodeType
						+ " can't have (" + childCount + ") children");
					throw illegalArgumentException;
				}
				break;

			case Exponential:// Confirm that there are 2 children
			case Fraction:
				if (childCount != 2){
					JSNICalls.consoleError("Wrong number of children, type: " + newNodeType
							+ " can't have (" + childCount + ") children");
					throw illegalArgumentException;
					}
				break;

			case Term:
				elementNode.setAttribute("open", "");
				elementNode.setAttribute("close", "");
			case Sum:// Confirm that there are multiple children
				if (childCount < 2){
					JSNICalls.consoleError("Wrong number of children, type: " + newNodeType
							+ " can't have (" + childCount + ") children");
					throw illegalArgumentException;
				}
				elementNode.setAttribute("separators", "");
				if (!Type.Term.equals(getType())
						&& !Type.Exponential.equals(getType())) {
					elementNode.setAttribute("open", "");
					elementNode.setAttribute("close", "");
				}
				break;
			}

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
			if("math".equalsIgnoreCase(getTag())){
				throw new NoSuchElementException("Can't get the parent of a math tag because it's the root:\n"+toString());
			}
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

		
		public Wrapper wrap(Wrapper wrap) {
			wrapper = wrap;
			wrappers.add(wrapper);
			return wrapper;
		}

		public Wrapper getWrapper() {
			return wrapper;
		}
		
		public void setSVG(Element element){
			SVGElement = element;
		}
		
		public Element getSVG(){
			return SVGElement;
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
		
		public boolean isLeftSide(){
			return isLeftSide;
		}
		public boolean isRightSide(){
			return isRightSide;
		}

		public Operator getOperation() {
			if ("mo".equalsIgnoreCase(getTag())) {
				String symbol = getSymbol();

				for (Operator op : Operator.values()) {
					if (op.getSign().equalsIgnoreCase(symbol)) {
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
			}else if ("math".equals(tag)) {
				type = Type.Equation;
			}

			if (type == null) {
				throw new NoSuchElementException(
						"There is no type for the tag: " + tag);
			}
			return type;
		}
	}

	public static enum Type {
		Term("mrow", true), Sum("mfenced", true), Exponential("msup", true), Fraction(
				"mfrac", true), Variable("mi", false), Number("mn", false), Operation(
				"mo", false), Equation("math", true);

		private String tag;
		private boolean hasChildren;

		Type(String tag, boolean hasChildren) {
			this.tag = tag;
			this.hasChildren = hasChildren;
		}

		public String getTag() {
			return tag;
		}

		public boolean hasChildren() {
			return hasChildren;

		}

	}

	public static enum Operator {
		DOT("&middot;"), SPACE("&nbsp;"), CROSS("&times;"), PLUS("+"), MINUS(
				"-");

		private String sign;

		Operator(String sign) {
			this.sign = sign;
		}

		public String getSign() {
			return sign;
		}

		public static Operator getMultiply() {
			return DOT;
		}
	}

	private void bindMLtoNodes(Node mathMLequation)
			throws TopNodesNotFoundException {

		// Find the top tree nodes: [left side] <mo>=<mo> [right side]
		Element rootNode = (Element) mathMLequation;
		NodeList<Node> sideEqSide = rootNode.getChildNodes();
		
		root = new MathMLBindingNode(rootNode);
		
		addRecursively(rootNode);
		
		leftSide = getNodeById(((Element)sideEqSide.getItem(0)).getAttribute("id"));
		equals = getNodeById(((Element)sideEqSide.getItem(1)).getAttribute("id"));
		rightSide = getNodeById(((Element)sideEqSide.getItem(2)).getAttribute("id"));
		
		leftSide.isLeftSide = true;
		rightSide.isRightSide = true;


		// // Prints both maps for debugging
//		System.out.println("idMLMap");
//		for (String key : idMLMap.keySet())
//			System.out.println(key + "\t" + idMLMap.get(key).getString());
//
//		System.out.println("idMap");
//		for (String key : idMap.keySet())
//			System.out.println(key + "\t" + idMap.get(key).toString());
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