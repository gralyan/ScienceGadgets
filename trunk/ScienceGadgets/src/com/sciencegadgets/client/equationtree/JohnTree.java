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
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public class JohnTree {

	private JohnTree tree = this;
	private JohnNode root;
	private JohnNode leftSide;
	private JohnNode equals;
	private JohnNode rightSide;
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
	public JohnTree(HTML mathML, Boolean isParsedForMath)
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

	public JohnNode getRoot() {
		return root;
	}

	public JohnNode getLeftSide() {
		return leftSide;
	}

	public JohnNode getRightSide() {
		return rightSide;
	}

	public void setLeftSide(JohnNode jNode) {
		leftSide = jNode;
	}

	public void setRightSide(JohnNode jNode) {
		rightSide = jNode;
	}

	public JohnNode getEquals() {
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

	private void wrapChildren(JohnNode jNode) {
		MLElementWrapper wrap;
		List<JohnNode> children = jNode.getChildren();

		for (JohnNode child : children) {
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

	public class JohnNode {
		private Node domNode;
		private Type type;
		private String symbol;
		private String tag;
		private MLElementWrapper wrapper;
		private JohnNode parent;
		private List<JohnNode> children = new LinkedList<JohnNode>();
		private Boolean isHidden = false;

		/**
		 * Construct node for existing domNode and appropriate info
		 * 
		 * @param domNode
		 *            - existing MathMl node
		 * @param tag
		 * @param type
		 * @param symbol
		 */
		private JohnNode(Node domNode, String tag, Type type, String symbol) {
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
		private JohnNode(Node node) {
			domNode = node;
			tag = node.getNodeName();
			type = null;
			symbol = ((Element) node).getInnerText();
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
		private JohnNode(String tag, Type type, String symbol) {
			com.google.gwt.user.client.Element newDomNode = DOM
					.createElement(tag);

			this.domNode = newDomNode;
			this.tag = tag;
			this.type = type;
			this.symbol = symbol;
			
			Log.severe("tag: "+tag);
			
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
		public JohnNode encase(String tag, Type type) {
			JohnNode encasing = new JohnNode(tag, type, this.toString());
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
		public JohnNode add(int index, String tag, Type type, String symbol) {
			JohnNode child = new JohnNode(tag, type, symbol);
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
		 * @param johnNode
		 */
		public void add(int index, JohnNode johnNode) {
			if (index < 0) {
				children.add(johnNode);
			} else {
				children.add(index, johnNode);
			}
			johnNode.parent = this;
			
			johnNode.isHidden = johnNode.checkIsHidden();
		}
		
		public void add(JohnNode johnNode) {
			add(-1, johnNode);
		}
		public JohnNode add(String tag, Type type, String symbol) {
			return add(-1, tag, type, symbol);
		}

		public List<JohnNode> getChildren() {
			return children;
		}

		public JohnNode getFirstChild() {
			return children.get(0);
		}

		public JohnNode getChildAt(int index) {
			return children.get(index);
		}

		public int getChildCount() {
			return children.size();
		}

		public JohnNode getNextSibling() {
			return getSibling(1);
		}

		public JohnNode getPrevSibling() {
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
		private JohnNode getSibling(int indexesAway) {
			int siblingIndex = this.getParent().getChildren().indexOf(this)
					+ indexesAway;
			try {
				JohnNode sibling = this.getParent().getChildAt(siblingIndex);
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
			List<JohnNode> sibs = this.parent.getChildren();
			sibs.remove(sibs.indexOf(this));
		}

		public int getIndex() {
			return this.parent.getChildren().indexOf(this);
		}

		public JohnNode getParent() {
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

		public JohnTree getTree() {
			return tree;
		}


		private Boolean checkIsHidden() {

			if ("(".equals(this.toString()) || ")".equals(this.toString())) {
				return true;
			} else if ("mo".equalsIgnoreCase(this.getTag())) {
				return true;
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
					|| "tan".equalsIgnoreCase(nodeString) || "sec".equalsIgnoreCase(nodeString)
					|| "csc".equalsIgnoreCase(nodeString) || "cot".equalsIgnoreCase(nodeString)
					|| "sinh".equalsIgnoreCase(nodeString) || "cosh".equalsIgnoreCase(nodeString)
					|| "tanh".equalsIgnoreCase(nodeString) || "log".equalsIgnoreCase(nodeString)
					|| "ln".equalsIgnoreCase(nodeString)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static enum Type {
		Term, Series, Function, Exponent, Fraction, Variable, Number;
	}

	class MLtoMLTree extends MathMLParser {
		HashMap<Node, JohnNode> nodeMap;
		private JohnNode prevLeftNode;
		private JohnNode prevRightNode;
		private JohnNode prevSibling;
		private JohnNode curNode;
		JohnNode nLeft;
		JohnNode nEq;
		JohnNode nRight;

		public MLtoMLTree(HTML mathMLequation) throws TopNodesNotFoundException {
			super(mathMLequation);
		}

		public void change(JohnTree jTree) {
			jTree.root = new JohnNode(null, null, null, null);
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

			nodeMap = new HashMap<Node, JohnNode>();
			nEq = new JohnNode(nodeEquals);
			nLeft = new JohnNode(nodeLeft);
			nRight = new JohnNode(nodeRight);

			prevLeftNode = nLeft;
			prevRightNode = nRight;
		}

		@Override
		protected void onVisitNode(Node currentNode, Boolean isLeft,
				int indexOfChildren) {

			curNode = new JohnNode(currentNode);

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

		private LinkedList<JohnNode> nestedMrows = new LinkedList<JohnNode>();
		private LinkedList<JohnNode> negatives = new LinkedList<JohnNode>();

		public void change(JohnTree jTree) {
			parseTree(jTree.getRoot());
			rearrangeNestedMrows();
			rearrangeNegatives();
		}

		private void parseTree(JohnNode jNode) {
			List<JohnNode> kids = jNode.getChildren();
			if (kids == null) {
				return;
			}
			for (JohnNode kid : kids) {
				if ("mrow".equalsIgnoreCase(kid.getTag())) {
					assignComplexChildMrow(kid);
				} else {
					assignSimpleTypes(kid);
				}
				findNestedMrows(jNode, kid);
				//TODO
				kid.isHidden = kid.checkIsHidden();
//				kid.isHidden = checkIsHidden(kid);

				if (kid.getChildCount() > 0) {
					parseTree(kid);
				}
			}
		}

		/**
		 * Assigns the MathML tags to JohnNode {@link Type}. This method is
		 * designed to take care of the simple cases, the more complex cases are
		 * assigned in assignComplexChildMrow
		 * 
		 * @param kid
		 */
		private void assignSimpleTypes(JohnNode kid) {
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
		private void assignComplexChildMrow(JohnNode kid) {

			if (kid.getChildCount() == 0) {
				Log.severe("mrow without children: " + kid);
				kid.remove();
			} else {

				// Default to term until + or - found in children
				kid.type = Type.Term;

				for (JohnNode baby : kid.getChildren()) {

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
						kid.children = new LinkedList<JohnNode>();
						//TODO
					} else if (baby.isFunction()) {
//					} else if (isFunction(baby.toString())) {
//						kid.type = Type.Function;
					}
				}
			}
		}

		/**
		 * Propagate a negation down the first of every child
		 * 
		 * @param node
		 */

		private void negatePropagate(JohnNode node) {
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
		private void findNestedMrows(JohnNode parent, JohnNode kid) {

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
			for (JohnNode kid : nestedMrows) {
				List<JohnNode> nests = kid.getChildren();
				for (JohnNode nest : nests) {
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
			for (JohnNode neg : negatives) {

				JohnNode negOne = new JohnNode(neg.getDomNode(), "mn",
						Type.Number, "-1");

				if (neg.getParent().type == Type.Series) {
					JohnNode negArg = neg.getNextSibling();

					JohnNode encasingTerm = new JohnNode(negArg.getDomNode(),
							"mrow", Type.Term, "-" + negArg.toString());

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

		MathTreeToML(JohnTree sourceTree, Boolean changeDomNodes) {
			this.changeDomNodes = changeDomNodes;
			Element firstNode = mlHTML.getElement().getFirstChildElement();
			addChild(sourceTree.getRoot(), firstNode);
		}

		MathTreeToML(JohnNode jNode) {
			Element firstNode = mlHTML.getElement().getFirstChildElement();
			addChild(jNode, firstNode);
		}

		private void addChild(JohnNode from, Node to) {
			List<JohnNode> children = from.getChildren();

			for (JohnNode child : children) {

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