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

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Log;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.equationbrowser.EquationBrowserEntry;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class MathMLBindingTree {

	private MathMLBindingTree tree = this;
	private MathMLBindingNode root;
	private MathMLBindingNode leftSide;
	private MathMLBindingNode equals;
	private MathMLBindingNode rightSide;
	private LinkedList<MLElementWrapper> wrappers = new LinkedList<MLElementWrapper>();
	private HTML mathML;

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
	public MathMLBindingTree(HTML mathML, Boolean isParsedForMath)
			throws TopNodesNotFoundException {
		this.mathML = mathML;

		new MLtoMLTree(mathML).change(this);

		if (isParsedForMath) {
			new MLTreeToMathTree().change(this);
		}
		this.wrapTree();
	}

	public HTML toMathML() {
		MathTreeToML mathTreeToML = new MathTreeToML(this, true);
		mathML = mathTreeToML.mlHTML;
		return mathML;
	}

	public HTML getMathML() {
		return mathML;
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
		wrappers.clear();
		wrapChildren(root);
		return wrappers;
	}

	private void wrapChildren(MathMLBindingNode jNode) {
		MLElementWrapper wrap;
		List<MathMLBindingNode> children = jNode.getChildren();

		for (MathMLBindingNode child : children) {
			if (!child.isHidden()) {
				wrap = new MLElementWrapper(child, true, true);
				child.setWrapper(wrap);
				wrappers.add(wrap);
			}
			if (child.getChildCount() > 0) {
				wrapChildren(child);
			}

		}
	}

	public MathMLBindingNode getNodeById(String id) {
		id = id.replaceAll("[A-Za-z]", "");
		System.out.println(id);
		return checkIdOfChild(id, root);
	}

	private MathMLBindingNode checkIdOfChild(String id, MathMLBindingNode parent) {
		if (id.equals(parent.getId())) {
			return parent;
		}

		List<MathMLBindingNode> children = parent.getChildren();
		MathMLBindingNode possibleFind = null;
		
		for (MathMLBindingNode child : children) {
			possibleFind = checkIdOfChild(id, child);
			if(possibleFind != null){
				return possibleFind;
			}
		}
		return null;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Node Class
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public class MathMLBindingNode {
		private Node domNode;
		private Type type;
		private String symbol;
		private String tag;
		private MLElementWrapper wrapper;
		private MathMLBindingNode parent;
		private List<MathMLBindingNode> children = new LinkedList<MathMLBindingNode>();
		private Boolean isHidden = false;
		private String id = "";

		/**
		 * Construct node for existing domNode and appropriate info
		 * 
		 * @param domNode
		 *            - existing MathMl node
		 * @param tag
		 * @param type
		 * @param symbol
		 */
		private MathMLBindingNode(Node domNode, String tag, Type type,
				String symbol) {
			this.domNode = domNode;
			this.tag = tag;
			this.type = type;
			this.symbol = symbol;
		}

		/**
		 * Wrap existing MathML node
		 * 
		 * @param node
		 */
		private MathMLBindingNode(Node node) {
			domNode = node;
			tag = node.getNodeName();
			type = null;
			symbol = ((Element) node).getInnerText();
			id = ((Element) node).getAttribute("id");
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
		private MathMLBindingNode(String tag, Type type, String symbol) {
			com.google.gwt.user.client.Element newDomNode = DOM
					.createElement(tag);

			this.domNode = newDomNode;
			this.tag = tag;
			this.type = type;
			this.symbol = symbol;

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
		public MathMLBindingNode encase(String tag, Type type) {
			MathMLBindingNode encasing = new MathMLBindingNode(tag, type,
					this.toString());
			this.getParent().add(this.getIndex(), encasing);
			this.remove();
			encasing.add(this);

			this.getDomNode().getParentElement()
					.insertAfter(encasing.getDomNode(), this.getDomNode());
			encasing.getDomNode().appendChild(this.getDomNode());

			return encasing;
		}

		/**
		 * Creates a node to add as a child at the specified index. Use index -1
		 * to append to the end of the child list
		 * 
		 * @param index
		 * @param tag
		 * @param type
		 * @param symbol
		 * @return - The newly create child
		 */
		public MathMLBindingNode add(int index, String tag, Type type,
				String symbol) {
			MathMLBindingNode child = new MathMLBindingNode(tag, type, symbol);
			if (index < 0) {
				this.add(-1, child);
			} else {
				this.add(index, child);
			}
			return child;
		}

		/**
		 * Adds a child at the specified index. Use index -1 to addend to the
		 * end of the child list
		 * 
		 * @param index
		 * @param mathMLBindingNode
		 */
		public void add(int index, MathMLBindingNode mathMLBindingNode) {
			if (index < 0) {
				children.add(mathMLBindingNode);
			} else {
				children.add(index, mathMLBindingNode);
			}
			mathMLBindingNode.parent = this;

			mathMLBindingNode.isHidden = mathMLBindingNode.checkIsHidden();
		}

		public void add(MathMLBindingNode mathMLBindingNode) {
			add(-1, mathMLBindingNode);
		}

		public MathMLBindingNode add(String tag, Type type, String symbol) {
			return add(-1, tag, type, symbol);
		}

		public List<MathMLBindingNode> getChildren() {
			return children;
		}

		public MathMLBindingNode getFirstChild() {
			return children.get(0);
		}

		public MathMLBindingNode getChildAt(int index) {
			return children.get(index);
		}

		public int getChildCount() {
			return children.size();
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
			int siblingIndex = this.getParent().getChildren().indexOf(this)
					+ indexesAway;
			try {
				MathMLBindingNode sibling = this.getParent().getChildAt(
						siblingIndex);
				return sibling;
			} catch (IndexOutOfBoundsException e) {
				throw new IndexOutOfBoundsException(
						"there is no child at index " + siblingIndex + ", "
								+ indexesAway + "indexes away from sibling: \n"
								+ this.toString() + "\n" + this);
			}

		}

		public void remove() {
			// Log.severe("REMOVING: " + this.toString());
			List<MathMLBindingNode> sibs = this.parent.getChildren();
			sibs.remove(sibs.indexOf(this));
		}

		public int getIndex() {
			return this.parent.getChildren().indexOf(this);
		}

		public MathMLBindingNode getParent() {
			if (parent == null) {
				throw new NullPointerException("There is no parent for:\n"
						+ this.toString() + "\n" + this);
			}
			return parent;
		}

		public Node getDomNode() {
			return domNode;
		}

		public void setDomNode(Node node) {
			domNode = node;
		}

		public String toString() {
			return symbol;
		}

		public void setString(String string) {
			this.symbol = string;
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

		public HTML toMathML() {
			HTML mathML;
			if (type == null) {
				mathML = new HTML(/* tag + " " + */"$" + symbol + "$");
			} else {
				mathML = new HTML(
				/* type.toString().substring(0, 2) + " " + */"$" + symbol + "$");
			}
			EquationBrowserEntry.parseJQMath(mathML.getElement());
			return mathML;
		}

		public Type getType() {
			return type;
		}

		public String getTag() {
			return tag;
		}

		public MathMLBindingTree getTree() {
			return tree;
		}

		public String getId() {
			return id;
		}

		private Boolean checkIsHidden() {

			if ("(".equals(this.toString()) || ")".equals(this.toString())) {
				return true;
				// } else if ("mo".equalsIgnoreCase(this.getTag())) {
				// return true;
			} else if (this.isFunction()
			// Don't show function name as child, parent will be function
			) {
				return true;
			} else if ("msub".equalsIgnoreCase(this.getParent().getTag())
			// Don't show subscripts because it's really one variable
					|| ("msubsup".equalsIgnoreCase(this.getParent().getTag()) && this
							.getIndex() == 1)) {
				return true;
			}
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
	}

	public static enum Type {
		Term, Series, Function, Exponent, Fraction, Variable, Number, None;
	}

	class MLtoMLTree extends MathMLParser {
		HashMap<Node, MathMLBindingNode> nodeMap;
		private MathMLBindingNode prevLeftNode;
		private MathMLBindingNode prevRightNode;
		private MathMLBindingNode prevSibling;
		private MathMLBindingNode curNode;
		MathMLBindingNode nLeft;
		MathMLBindingNode nEq;
		MathMLBindingNode nRight;

		public MLtoMLTree(HTML mathMLequation) throws TopNodesNotFoundException {
			super(mathMLequation);
		}

		public void change(MathMLBindingTree jTree) {
			jTree.root = new MathMLBindingNode(null, null, null, null);
			jTree.leftSide = nLeft;
			jTree.equals = nEq;
			jTree.rightSide = nRight;
			jTree.root.add(jTree.leftSide);
			jTree.root.add(jTree.equals);
			jTree.root.add(jTree.rightSide);

		}

		@Override
		protected void onRootsFound(Node nodeLeft, Node nodeEquals,
				Node nodeRight) {

			nodeMap = new HashMap<Node, MathMLBindingNode>();
			nEq = new MathMLBindingNode(nodeEquals);
			nLeft = new MathMLBindingNode(nodeLeft);
			nRight = new MathMLBindingNode(nodeRight);

			prevLeftNode = nLeft;
			prevRightNode = nRight;
		}

		@Override
		protected void onVisitNode(Node currentNode, Boolean isLeft,
				int indexOfChildren) {

			curNode = new MathMLBindingNode(currentNode);

			// Must be separated to allow the TreeCanvas to allocate space
			// proportional to member count ratio of sides of the equation
			if (isLeft) {
				if (indexOfChildren == 0) {
					prevLeftNode.add(curNode);
				} else {
					prevSibling.getParent().add(curNode);
				}
				prevLeftNode = curNode;

			} else {

				if (indexOfChildren == 0) {
					prevRightNode.add(curNode);
				} else {
					prevSibling.getParent().add(curNode);
				}
				prevRightNode = curNode;
			}
			nodeMap.put(currentNode, curNode);

		}

		@Override
		protected void onGoingToNextChild(Node currentNode) {
			prevSibling = nodeMap.get(currentNode);

		}
	}

	class MLTreeToMathTree {

		private LinkedList<MathMLBindingNode> nestedMrows = new LinkedList<MathMLBindingNode>();
		private LinkedList<MathMLBindingNode> negatives = new LinkedList<MathMLBindingNode>();

		public void change(MathMLBindingTree jTree) {
			parseTree(jTree.getRoot());
			rearrangeNestedMrows();
			rearrangeNegatives();
		}

		private void parseTree(MathMLBindingNode jNode) {
			List<MathMLBindingNode> kids = jNode.getChildren();
			if (kids == null) {
				return;
			}
			for (MathMLBindingNode kid : kids) {
				if ("mrow".equalsIgnoreCase(kid.getTag())) {
					assignComplexChildMrow(kid);
				} else {
					assignSimpleTypes(kid);
				}
				findNestedMrows(jNode, kid);

				kid.isHidden = kid.checkIsHidden();

				if (kid.getChildCount() > 0) {
					parseTree(kid);
				}
			}
		}

		/**
		 * Assigns the MathML tags to MathMLBindingNode {@link Type}. This
		 * method is designed to take care of the simple cases, the more complex
		 * cases are assigned in assignComplexChildMrow
		 * 
		 * @param kid
		 */
		private void assignSimpleTypes(MathMLBindingNode kid) {
			if ("mi".equalsIgnoreCase(kid.getTag())) {
				kid.type = Type.Variable;
			} else if ("mn".equalsIgnoreCase(kid.getTag())) {
				kid.type = Type.Number;
			} else if ("msub".equalsIgnoreCase(kid.getTag())) {
				kid.type = Type.Variable;
			} else if ("msup".equalsIgnoreCase(kid.getTag())
					|| "msubsup".equalsIgnoreCase(kid.getTag())
					|| "msqrt".equalsIgnoreCase(kid.getTag())) {
				kid.type = Type.Exponent;
			} else if ("mfrac".equalsIgnoreCase(kid.getTag())) {
				kid.type = Type.Fraction;
			} else if ("mo".equalsIgnoreCase(kid.getTag())) {
				kid.type = Type.None;
			}
		}

		/**
		 * Converts all mrow tags to either {@link Type.Term} or
		 * {@link Type.Series}. Also takes case of special cases such as delta
		 * and functions
		 * 
		 * @param kid
		 * @return
		 */
		private void assignComplexChildMrow(MathMLBindingNode kid) {

			if (kid.getChildCount() == 0) {
				Log.severe("mrow without children: " + kid);
				kid.remove();
			} else {

				// Default to term until + or - found in children
				kid.type = Type.Term;

				for (MathMLBindingNode baby : kid.getChildren()) {

					if ("mo".equalsIgnoreCase(baby.getTag())) {
						// Check children for +/- => series
						if ("−".equals(baby.toString())
								|| "+".equals(baby.toString())) {
							// A "-" at the beginning doesn't make it a
							// series
							if (baby.getIndex() > 0) {
								kid.type = Type.Series;
							}
							// Negate the next node because we don't want minus
							if ("−".equals(baby.toString())) {
								// negatives.add(baby);

								baby.setString("+");
								negatePropagate(baby.getNextSibling());

							}
						}

						/*
						 * Special cases
						 */
					} else if ("Δ".equals(baby.toString())) {
						// For Δ: Δa should be treated as one variable
						kid.type = Type.Variable;
						kid.children = new LinkedList<MathMLBindingNode>();
					} else if (baby.isFunction()) {
						kid.type = Type.Function;
					}
				}
			}
		}

		/**
		 * Propagate a negation down the first of every child
		 * 
		 * @param node
		 */

		private void negatePropagate(MathMLBindingNode node) {
			node.symbol = "-" + node.symbol;
			if (node.getChildCount() > 0) {
				negatePropagate(node.getChildAt(0));
			}
		}

		/**
		 * Finds all instances where there is a series inside a series or a term
		 * inside a term. These will be compiled into one node to make the tree
		 * more mathematically sound by conveying the associative property. Due
		 * to {@link ConcurrentModificationException} problems, the actual
		 * re-arrangement is done in another step: {@link rearrangeNestedMrows}
		 * 
		 * @param parent
		 * @param nestMrow
		 */
		private void findNestedMrows(MathMLBindingNode parent,
				MathMLBindingNode kid) {

			if (
			/**/((Type.Series).equals(kid.getType())
			/**/&& (Type.Series).equals(parent.getType()))
			/**/||
			/**/((Type.Term).equals(kid.getType())
			/**/&& (Type.Term).equals(parent.getType()))

			) {
				nestedMrows.add(kid);
			}
		}

		/**
		 * Second part of {@link findNestedMrows}, compiles all nested
		 * {@link Type.Series} and {@link Type.Term} to one node
		 */
		private void rearrangeNestedMrows() {
			if (nestedMrows == null) {
				return;
			}
			for (MathMLBindingNode kid : nestedMrows) {
				List<MathMLBindingNode> nests = kid.getChildren();
				for (MathMLBindingNode nest : nests) {
					kid.getParent().add(kid.getIndex(), nest);
				}
				kid.remove();
				Log.info("Removing nested mrow :" + kid);
			}
		}

		/**
		 * This method allows the "invisible negative one" to be displayed
		 * explicitly and maneuvered accordingly. It makes an encasing sentinel
		 * term for negative terms in a series.
		 */
		private void rearrangeNegatives() {
			// TODO
			for (MathMLBindingNode neg : negatives) {

				MathMLBindingNode negOne = new MathMLBindingNode(
						neg.getDomNode(), "mn", Type.Number, "-1");

				if (neg.getParent().type == Type.Series) {
					MathMLBindingNode negArg = neg.getNextSibling();

					MathMLBindingNode encasingTerm = new MathMLBindingNode(
							negArg.getDomNode(), "mrow", Type.Term, "-"
									+ negArg.toString());

					neg.getParent().add(negArg.getIndex(), encasingTerm);

					encasingTerm.add(negOne);
					negArg.remove();
					encasingTerm.add(negArg);

					Log.info("neg: " + neg + ", tag: " + neg.getTag()
							+ ", type: " + neg.getType() + ", parent: "
							+ neg.getParent());
					Log.info("Rearranging the negative for: " + negArg);
				} else {
					neg.getParent().add(neg.getIndex() + 1, negOne);
					Log.info("Adding -1 node to the term");
				}
			}
		}

	}

	/**
	 * This class allows the tree representation of the equation to be converted
	 * back into MathML for display.
	 * 
	 * @author John Gralyan
	 * 
	 */
	class MathTreeToML {
		HTML mlHTML = new HTML("<math></math>");
		Boolean changeDomNodes = false;

		MathTreeToML(MathMLBindingTree sourceTree, Boolean changeDomNodes) {
			this.changeDomNodes = changeDomNodes;
			Element firstNode = mlHTML.getElement().getFirstChildElement();
			addChild(sourceTree.getRoot(), firstNode);
		}

		MathTreeToML(MathMLBindingNode jNode) {
			Element firstNode = mlHTML.getElement().getFirstChildElement();
			addChild(jNode, firstNode);
		}

		private void addChild(MathMLBindingNode from, Node to) {
			List<MathMLBindingNode> children = from.getChildren();

			for (MathMLBindingNode child : children) {

				// if ("-1".equals(child.toString()) ) {
				// continue;
				// }

				Node childTo = to.appendChild(child.getDomNode().cloneNode(
						false));

				if (changeDomNodes) {
					child.setDomNode(childTo);
				}

				if ("mi".equals(child.getTag()) | "mn".equals(child.getTag())
						| "mo".equals(child.getTag())) {

					((Element) childTo).setInnerText(child.toString());
				}

				if (child.getChildCount() >= 0) {
					addChild(child, childTo);
				}
			}
		}

	}
}