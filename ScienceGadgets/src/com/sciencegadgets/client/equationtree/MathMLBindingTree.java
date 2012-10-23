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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Collection;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

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

	public MathMLBindingNode getNodeById(String id) {
		id = id.replaceAll("[A-Za-z]", "");
		MathMLBindingNode node = idMap.get(id);
		if (node == null) {
			System.out.println("xxx CANT GET NODE BY ID: " + id + " xxx");
		}
		return node;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Node Class
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public class MathMLBindingNode {
		private Element mlNode;
//		private Type type;
//		private HTML symbol;
//		private String tag;
		private MLElementWrapper wrapper;
		private MathMLBindingNode parent;
		private Boolean isHidden = false;

		/**
		 * Wrap existing MathML node
		 */
		private MathMLBindingNode(Element mlNode) {
			this.mlNode = mlNode;
//			tag = mlNode.getNodeName();
//			type = null;

//			symbol = new HTML();
//			Node clone = ((Element) mlNode).cloneNode(true);
//			symbol.getElement().appendChild(clone);
		}

		/**
		 * Creates a new MathML DOM node which should be added into the MathML
		 * in the same position this node is added to the tree. It can be found
		 * with <b>getDomNode()</b>;
		 * 
		 * @param tag
		 *            - MathML tag
		 * @param type
		 *            - type of node
		 * @param symbol
		 *            - inner text
		 */
		private MathMLBindingNode(String tag) {

			this.mlNode = DOM.createElement(tag);
//			this.tag = tag;
//			this.type = type;
//			this.symbol = symbol;

		}

		/**
		 * Adds a node between this node and its parent, encasing this branch of
		 * the tree in a new node. <br/>
		 * The text will be it's children's, to set a new text use:
		 * <p>
		 * <b>[encasing node].setString(String)</b>
		 * </p>
		 * 
		 * @param tag
		 *            - the tag of the new node
		 * @param type
		 *            - the type of the new node
		 * @return - encasing node
		 */
		// TODO look at this again
		public MathMLBindingNode encase(String tag) {
			System.out.println("parent: "+mlNode.getParentElement().getString());

			//Make symbol (HTML node) for encasing
//			Node elClone = ((Element) this.getMLNode()).cloneNode(true);
//			HTML smbl = new HTML();
//			smbl.getElement().appendChild(elClone);
			MathMLBindingNode encasing = new MathMLBindingNode(tag);

			//Move around nodes
			this.getParent().add(this.getIndex(), encasing);
			this.remove();
			encasing.add(this);
			System.out.println("parent2: "+mlNode.getParentElement().getString());

			return encasing;
		}

		/**
		 * Creates a node to add as a child at the specified index. Use index -1
		 * to append to the end of the child list
		 * 
		 * @return - The newly create child
		 */
		public MathMLBindingNode add(int index, String tag,
				String symbol) {

			int randId = Random.nextInt();

			Element elmnt = DOM.createElement(tag);
			elmnt.setInnerText(symbol);
			elmnt.setAttribute("id", randId + "");
			MathMLBindingNode newNode = new MathMLBindingNode(elmnt);

			if (index < 0) {
				this.add(-1, newNode);
			} else {
				this.add(index, newNode);
			}
			return newNode;
		}

		/**
		 * Adds a child at the specified index. Use index -1 to addend to the
		 * end of the child list
		 * 
		 * @param index
		 * @param mathMLBindingNode
		 */
		public void add(int index, MathMLBindingNode newNode) {
//			Element parent = mlNode.getParentElement();
			String id = newNode.getId();

			if (index < 0) {
				mlNode.appendChild(newNode.mlNode);
			} else {
				Node childBefore = mlNode.getChild(index);
				mlNode.insertAfter(childBefore, newNode.mlNode);
			}
			idMap.put(id, newNode);
			idMLMap.put(id, newNode.mlNode);

			// mathMLBindingNode.parent = this;
			// newNode.isHidden = newNode.checkIsHidden();
		}

		public void add(MathMLBindingNode newNode) {
			add(-1, newNode);
		}

		public MathMLBindingNode add(String tag, String symbol) {
			return add(-1, tag, symbol);
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
			// return getChildren().get(index);
		}

		public MathMLBindingNode getFirstChild() {
			return getChildAt(0);
		}

		public int getChildCount() {
			return mlNode.getChildCount();
		}

		public MathMLBindingNode getNextSibling() {
			return getSibling(1);
		}

		public MathMLBindingNode getPrevSibling() {
			return getSibling(-1);
		}

		/**
		 * This method gets the sibling at a position relative the this node
		 * 
		 * @param indexesAway
		 *            - the number of indexes away from this sibling positive
		 *            for siblings to the right, negative for siblings to the
		 *            left
		 * @return
		 */
		private MathMLBindingNode getSibling(int indexesAway) {
			MathMLBindingNode parent = this.getParent();
			// int thisIndex = parent.getChildren().indexOf(this);
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
			String id = getId();
			idMap.remove(id);
			idMLMap.remove(id);
			mlNode.removeFromParent();
			// Log.severe("REMOVING: " + this.toString());
			// List<MathMLBindingNode> sibs = this.getParent().getChildren();
			// sibs.remove(sibs.indexOf(this));
		}

		public int getIndex() {
			return this.getParent().getChildren().indexOf(this);
		}

		public MathMLBindingNode getParent() {
			// if (parent == null) {
			// throw new NullPointerException("There is no parent for:\n"
			// + this.toString() + "\n" + this);
			// }

			Element parentElement = getMLNode().getParentElement();
			String parentId = parentElement.getAttribute("id");
			MathMLBindingNode parentNode = getNodeById(parentId);
			return parentNode;
			// return parent;
		}

		public Element getMLNode() {
			return mlNode;
		}

		public void setMLNode(Element mlNode) {
			this.mlNode = mlNode;
		}

		public String toString() {
			return mlNode.getString();
		}
		
		public void setString(String content){
			mlNode.setInnerText(content);
		}

		public Boolean isHidden() {
			return isHidden;
		}

		public void setWrapper(MLElementWrapper wrap) {
			wrapper = wrap;
		}

		public MLElementWrapper getWrapper() {
			return wrapper;
		}

		public String getTag() {
			return mlNode.getTagName();
		}

		public MathMLBindingTree getTree() {
			return tree;
		}

		public String getId() {
			return getMLNode().getAttribute("id");
		}
		
		private Boolean checkIsHidden() {

			// if ("(".equals(this.toString()) || ")".equals(this.toString())) {
			// return true;
			// // } else if ("mo".equalsIgnoreCase(this.getTag())) {
			// // return true;
			// } else if (this.isFunction()
			// // Don't show function name as child, parent will be function
			// ) {
			// return true;
			// } else if ("msub".equalsIgnoreCase(this.getParent().getTag())
			// // Don't show subscripts because it's really one variable
			// || ("msubsup".equalsIgnoreCase(this.getParent().getTag()) && this
			// .getIndex() == 1)) {
			// return true;
			// }
			return false;

		}

		private Boolean isFunction() {
			String nodeString = this.toString();
			if ("cos".equals(nodeString) || "sin".equals(nodeString)
					|| "tan".equalsIgnoreCase(nodeString)
					|| "sec".equalsIgnoreCase(nodeString)
					|| "csc".equalsIgnoreCase(nodeString)
					|| "cot".equalsIgnoreCase(nodeString)
					|| "sinh".equalsIgnoreCase(nodeString)
					|| "cosh".equalsIgnoreCase(nodeString)
					|| "tanh".equalsIgnoreCase(nodeString)
					|| "log".equalsIgnoreCase(nodeString)
					|| "ln".equalsIgnoreCase(nodeString)) {
				return true;
			} else {
				return false;
			}
		}
		
		public Type getType() {
			String tag = getTag();
			Type type = null;

			if("mfenced".equalsIgnoreCase(tag)){
				type = Type.Series;
			}else{
				type = Type.valueOf(tag);
			}
			
			return type;
		}
	}
	
	public static enum Type {
		Term, Series, Function, msup, mfrac, mi, mn;
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

	// class MLtoMLTree extends MathMLParser {
	// HashMap<Node, MathMLBindingNode> nodeMap;
	// private MathMLBindingNode prevLeftNode;
	// private MathMLBindingNode prevRightNode;
	// private MathMLBindingNode prevSibling;
	// private MathMLBindingNode curNode;
	// MathMLBindingTree jTree;
	// MathMLBindingNode nLeft;
	// MathMLBindingNode nEq;
	// MathMLBindingNode nRight;
	// MathMLBindingNode nRoot;
	//
	// public MLtoMLTree(HTML mathMLequation) throws TopNodesNotFoundException {
	// super(mathMLequation);
	// }
	//
	// public void change(MathMLBindingTree johnTree) {
	// jTree = johnTree;
	// // jTree.root = new MathMLBindingNode(null, null, null, null);
	// jTree.leftSide = nLeft;
	// jTree.equals = nEq;
	// jTree.rightSide = nRight;
	//
	// jTree.root = nRoot;
	// jTree.root.add(jTree.leftSide);
	// jTree.root.add(jTree.equals);
	// jTree.root.add(jTree.rightSide);
	// }
	//
	// @Override
	// protected void onRootsFound(Node nodeRoot, Node nodeLeft, Node
	// nodeEquals,
	// Node nodeRight) {
	//
	// nodeMap = new HashMap<Node, MathMLBindingNode>();
	// nRoot = new MathMLBindingNode(nodeRoot);
	// nEq = new MathMLBindingNode(nodeEquals);
	// nLeft = new MathMLBindingNode(nodeLeft);
	// nRight = new MathMLBindingNode(nodeRight);
	//
	// prevLeftNode = nLeft;
	// prevRightNode = nRight;
	// }
	//
	// @Override
	// protected void onVisitNode(Node currentNode, Boolean isLeft,
	// int indexOfChildren) {
	//
	// curNode = new MathMLBindingNode(currentNode);
	//
	// // Must be separated to allow the TreeCanvas to allocate space
	// // proportional to member count ratio of sides of the equation
	// if (isLeft) {
	// if (indexOfChildren == 0) {
	// prevLeftNode.add(curNode);
	// } else {
	// prevSibling.getParent().add(curNode);
	// }
	// prevLeftNode = curNode;
	//
	// } else {
	//
	// if (indexOfChildren == 0) {
	// prevRightNode.add(curNode);
	// } else {
	// prevSibling.getParent().add(curNode);
	// }
	// prevRightNode = curNode;
	// }
	// nodeMap.put(currentNode, curNode);
	//
	// }
	//
	// @Override
	// protected void onGoingToNextChild(Node currentNode) {
	// prevSibling = nodeMap.get(currentNode);
	//
	// }
	// }

	// class MLTreeToMathTree {
	//
	// private LinkedList<MathMLBindingNode> nestedMrows = new
	// LinkedList<MathMLBindingNode>();
	// private LinkedList<MathMLBindingNode> negatives = new
	// LinkedList<MathMLBindingNode>();
	//
	// public void change(MathMLBindingTree jTree) {
	// parseTree(jTree.getRoot());
	// rearrangeNestedMrows();
	// rearrangeNegatives();
	// }
	//
	// private void parseTree(MathMLBindingNode jNode) {
	// List<MathMLBindingNode> kids = jNode.getChildren();
	// if (kids == null) {
	// return;
	// }
	// for (MathMLBindingNode kid : kids) {
	// if ("mrow".equalsIgnoreCase(kid.getTag())) {
	// assignComplexChildMrow(kid);
	// } else {
	// assignSimpleTypes(kid);
	// }
	// findNestedMrows(jNode, kid);
	//
	// kid.isHidden = kid.checkIsHidden();
	//
	// if (kid.getChildCount() > 0) {
	// parseTree(kid);
	// }
	// }
	// }
	//
	// /**
	// * Assigns the MathML tags to MathMLBindingNode {@link Type}. This
	// * method is designed to take care of the simple cases, the more complex
	// * cases are assigned in assignComplexChildMrow
	// *
	// * @param kid
	// */
	// private void assignSimpleTypes(MathMLBindingNode kid) {
	// if ("mi".equalsIgnoreCase(kid.getTag())) {
	// kid.type = Type.Variable;
	// } else if ("mn".equalsIgnoreCase(kid.getTag())) {
	// kid.type = Type.Number;
	// } else if ("msub".equalsIgnoreCase(kid.getTag())) {
	// kid.type = Type.Variable;
	// } else if ("msup".equalsIgnoreCase(kid.getTag())
	// || "msubsup".equalsIgnoreCase(kid.getTag())
	// || "msqrt".equalsIgnoreCase(kid.getTag())) {
	// kid.type = Type.Exponent;
	// } else if ("mfrac".equalsIgnoreCase(kid.getTag())) {
	// kid.type = Type.Fraction;
	// } else if ("mo".equalsIgnoreCase(kid.getTag())) {
	// kid.type = Type.None;
	// }
	// }
	//
	// /**
	// * Converts all mrow tags to either {@link Type.Term} or
	// * {@link Type.Series}. Also takes case of special cases such as delta
	// * and functions
	// *
	// * @param kid
	// * @return
	// */
	// private void assignComplexChildMrow(MathMLBindingNode kid) {
	//
	// if (kid.getChildCount() == 0) {
	// Log.severe("mrow without children: " + kid);
	// kid.remove();
	// } else {
	//
	// // Default to term until + or - found in children
	// kid.type = Type.Term;
	//
	// for (MathMLBindingNode baby : kid.getChildren()) {
	//
	// if ("mo".equalsIgnoreCase(baby.getTag())) {
	// // Check children for +/- => series
	// if ("−".equals(baby.toString())
	// || "+".equals(baby.toString())) {
	// // A "-" at the beginning doesn't make it a
	// // series
	// if (baby.getIndex() > 0) {
	// kid.type = Type.Series;
	// }
	// // Negate the next node because we don't want minus
	// if ("−".equals(baby.toString())) {
	// // negatives.add(baby);
	//
	// // baby.setString("+");
	// negatePropagate(baby.getNextSibling());
	//
	// }
	// }
	//
	// /*
	// * Special cases
	// */
	// } else if ("Δ".equals(baby.toString())) {
	// // For Δ: Δa should be treated as one variable
	// kid.type = Type.Variable;
	// kid.children = new LinkedList<MathMLBindingNode>();
	// } else if (baby.isFunction()) {
	// kid.type = Type.Function;
	// }
	// }
	// }
	// }
	//
	// /**
	// * Propagate a negation down the first of every child
	// *
	// * @param node
	// */
	//
	// private void negatePropagate(MathMLBindingNode node) {
	// // node.symbol = "-" + node.symbol;
	// if (node.getChildCount() > 0) {
	// negatePropagate(node.getChildAt(0));
	// }
	// }
	//
	// /**
	// * Finds all instances where there is a series inside a series or a term
	// * inside a term. These will be compiled into one node to make the tree
	// * more mathematically sound by conveying the associative property. Due
	// * to {@link ConcurrentModificationException} problems, the actual
	// * re-arrangement is done in another step: {@link rearrangeNestedMrows}
	// *
	// * @param parent
	// * @param nestMrow
	// */
	// private void findNestedMrows(MathMLBindingNode parent,
	// MathMLBindingNode kid) {
	//
	// if (
	// /**/((Type.Series).equals(kid.getType())
	// /**/&& (Type.Series).equals(parent.getType()))
	// /**/||
	// /**/((Type.Term).equals(kid.getType())
	// /**/&& (Type.Term).equals(parent.getType()))
	//
	// ) {
	// nestedMrows.add(kid);
	// }
	// }
	//
	// /**
	// * Second part of {@link findNestedMrows}, compiles all nested
	// * {@link Type.Series} and {@link Type.Term} to one node
	// */
	// private void rearrangeNestedMrows() {
	// if (nestedMrows == null) {
	// return;
	// }
	// for (MathMLBindingNode kid : nestedMrows) {
	// List<MathMLBindingNode> nests = kid.getChildren();
	// for (MathMLBindingNode nest : nests) {
	// kid.getParent().add(kid.getIndex(), nest);
	// }
	// kid.remove();
	// Log.info("Removing nested mrow :" + kid);
	// }
	// }
	//
	// /**
	// * This method allows the "invisible negative one" to be displayed
	// * explicitly and maneuvered accordingly. It makes an encasing sentinel
	// * term for negative terms in a series.
	// */
	// private void rearrangeNegatives() {
	// for (MathMLBindingNode neg : negatives) {
	//
	// MathMLBindingNode negOne = new MathMLBindingNode(
	// neg.getMLNode(), "mn", Type.Number, new HTML("<mn>-1</mn>"));
	//
	// if (neg.getParent().type == Type.Series) {
	// MathMLBindingNode negArg = neg.getNextSibling();
	//
	// MathMLBindingNode encasingTerm = new MathMLBindingNode(
	// negArg.getMLNode(), "mrow", Type.Term,
	// new HTML("-"+negArg.toString()));
	//
	// neg.getParent().add(negArg.getIndex(), encasingTerm);
	//
	// encasingTerm.add(negOne);
	// negArg.remove();
	// encasingTerm.add(negArg);
	//
	// Log.info("neg: " + neg + ", tag: " + neg.getTag()
	// + ", type: " + neg.getType() + ", parent: "
	// + neg.getParent());
	// Log.info("Rearranging the negative for: " + negArg);
	// } else {
	// neg.getParent().add(neg.getIndex() + 1, negOne);
	// Log.info("Adding -1 node to the term");
	// }
	// }
	// }
	//
	// }
	//
	// /**
	// * This class allows the tree representation of the equation to be
	// converted
	// * back into MathML for display.
	// *
	// * @author John Gralyan
	// *
	// */
	// class MathTreeToML {
	// HTML mlHTML = new HTML("<math></math>");
	// Boolean changeDomNodes = false;
	//
	// MathTreeToML(MathMLBindingTree sourceTree, Boolean changeDomNodes) {
	// this.changeDomNodes = changeDomNodes;
	// Element firstNode = mlHTML.getElement().getFirstChildElement();
	// addChild(sourceTree.getRoot(), firstNode);
	// }
	//
	// MathTreeToML(MathMLBindingNode jNode) {
	// Element firstNode = mlHTML.getElement().getFirstChildElement();
	// addChild(jNode, firstNode);
	// }
	//
	// private void addChild(MathMLBindingNode from, Node to) {
	// List<MathMLBindingNode> children = from.getChildren();
	//
	// for (MathMLBindingNode child : children) {
	//
	// // if ("-1".equals(child.toString()) ) {
	// // continue;
	// // }
	//
	// Node childTo = to.appendChild(child.getMLNode().cloneNode(
	// false));
	//
	// if (changeDomNodes) {
	// child.setMLNode(childTo);
	// }
	//
	// if ("mi".equals(child.getTag()) | "mn".equals(child.getTag())
	// | "mo".equals(child.getTag())) {
	//
	// ((Element) childTo).setInnerText(child.toString());
	// }
	//
	// if (child.getChildCount() >= 0) {
	// addChild(child, childTo);
	// }
	// }
	// }
	//
	// }
}