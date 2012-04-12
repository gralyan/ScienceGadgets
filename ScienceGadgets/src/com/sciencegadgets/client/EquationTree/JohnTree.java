package com.sciencegadgets.client.EquationTree;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.ScienceGadgets;
import com.sciencegadgets.client.AlgebraManipulation.MLElementWrapper;
import com.sciencegadgets.client.EquationTree.JohnTree.JohnNode;

public class JohnTree {

	private JohnNode root;
	private JohnNode leftSide;
	private JohnNode rightSide;
	private LinkedList<MLElementWrapper> wrappers;

	/**
	 * A tree representation of an equation.
	 * 
	 * @param mathML
	 *            - The equation written in MathML XML
	 * @param isParsedForMath
	 *            - If true, the tree is an abstract syntax tree that can be
	 *            manipulated as math. If false it is a tree of MathML as taken
	 *            from XML
	 */
	public JohnTree(HTML mathML, Boolean isParsedForMath) {
		new MLtoJohnTree(mathML).change(this);

		if (isParsedForMath) {
			new MLTreeToMathTree().change(this);
		}
	}

	public JohnTree(JohnNode leftSide, JohnNode equalsRoot, JohnNode rightSide) {
		root = equalsRoot;
		this.leftSide = leftSide;
		this.rightSide = rightSide;
		root.add(this.leftSide);
		root.add(this.rightSide);
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

	public LinkedList<MLElementWrapper> getWrappers() {
		return wrappers;
	}

	protected class JohnNode {
		// encapsulated dom node
		private Node domNode;
		private Type type;
		private String symbol;
		private String tag;
		private MLElementWrapper wrapper;

		private JohnNode parent;
		// private int indexInSiblings;
		private List<JohnNode> children = new LinkedList<JohnNode>();

		public JohnNode(Node node, MLElementWrapper wrap) {
			wrapper = wrap;
			domNode = node;
			tag = node.getNodeName();
			type = null;
			symbol = ((Element) node).getInnerText();
		}

		/**
		 * Adds a {@link Node} to the {@link JohnTree} by creating a new
		 * {@link JohnNode}
		 * 
		 */
		public void add(Node node, MLElementWrapper wrap) {
			add(new JohnNode(node, wrap));
		}

		private void add(JohnNode johnNode) {
			children.add(johnNode);
			johnNode.parent = this;
		}

		private void add(int index, JohnNode johnNode) {
			children.add(index, johnNode);
			johnNode.parent = this;
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
			int nextIndex = this.getParent().getChildren().indexOf(this) + 1;
			return this.getParent().getChildAt(nextIndex);
		}

		public void remove() {
			List<JohnNode> sibs = this.parent.getChildren();
			sibs.remove(sibs.indexOf(this));
		}

		public int getIndex() {
			return this.parent.getChildren().indexOf(this);
		}

		public JohnNode getParent() {
			return parent;
		}

		public Node getDomNode() {
			return domNode;
		}

		public String toString() {
			return symbol;
		}

		public MLElementWrapper getWrapper() {
			return wrapper;
		}

		public HTML toMathML() {
			HTML mathML;
			if (type == null) {
				mathML = new HTML(tag + " " + "$" + symbol + "$");
			} else {
				mathML = new HTML(type + " " + "$" + symbol + "$");
			}
			ScienceGadgets.parseJQMath(mathML.getElement());
			return mathML;
		}

		public Type getType() {
			return type;
		}

		public String getTag() {
			return tag;
		}
	}

	private static enum Type {
		T, S, E, Fn, V, N, Fr;
	}

	class MLtoJohnTree extends MathMLParser {
		HashMap<Node, JohnNode> nodeMap;
		private JohnNode prevLeftNode;
		private JohnNode prevRightNode;
		private JohnNode prevSibling;
		private JohnNode curNode;
		JohnNode nLeft;
		JohnNode nEq;
		JohnNode nRight;
		private LinkedList<MLElementWrapper> wrappers;
		private MLElementWrapper wrap;

		public MLtoJohnTree(HTML mathMLequation) {
			super(mathMLequation);
		}

		public void change(JohnTree jTree) {
			jTree.root = nEq;
			jTree.leftSide = nLeft;
			jTree.rightSide = nRight;
			jTree.root.add(jTree.leftSide);
			jTree.root.add(jTree.rightSide);
			jTree.wrappers = wrappers;

		}

		@Override
		protected void onRootsFound(Node nodeLeft, Node nodeEquals,
				Node nodeRight) {

			nodeMap = new HashMap<Node, JohnNode>();
			nEq = new JohnNode(nodeEquals, null);

			wrappers = new LinkedList<MLElementWrapper>();

			wrap = MLElementWrapper.wrapperFactory((Element) nodeLeft);
			wrappers.add(wrap);
			nLeft = new JohnNode(nodeLeft, wrap);

			wrap = MLElementWrapper.wrapperFactory((Element) nodeRight);
			wrappers.add(wrap);
			nRight = new JohnNode(nodeRight, wrap);

			prevLeftNode = nLeft;
			prevRightNode = nRight;
		}

		@Override
		protected void onVisitNode(Node currentNode, Boolean isLeft,
				int indexOfChildren) {
			wrap = MLElementWrapper.wrapperFactory((Element) currentNode);
			wrappers.add(wrap);
			curNode = new JohnNode(currentNode, wrap);

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

		JohnNode mathRoot;
		private LinkedList<JohnNode> nestedMrows = new LinkedList<JohnNode>();
		private LinkedList<JohnNode> removeList = new LinkedList<JohnNode>();

		public void change(JohnTree jTree) {
			mathRoot = jTree.getRoot();
			commenseRevolution(mathRoot);
		}

		private void commenseRevolution(JohnNode jNode) {
			assignTypes(jNode);
			deleteRemoveListItems();
			convertChildrensMrow(jNode);
			deleteRemoveListItems();
			findNestedMrows(jNode);
			rearrangeNestedMrows();
		}

		private void assignTypes(JohnNode jNode) {
			List<JohnNode> kids = jNode.getChildren();
			if (kids == null) {
				return;
			}
			for (JohnNode kid : kids) {
				if ("mi".equalsIgnoreCase(kid.getTag())) {
					kid.type = Type.V;
				} else if ("mn".equalsIgnoreCase(kid.getTag())) {
					kid.type = Type.N;
				} else if ("msub".equalsIgnoreCase(kid.getTag())) {
					kid.type = Type.V;
				} else if ("msup".equalsIgnoreCase(kid.getTag())
						|| "msubsup".equalsIgnoreCase(kid.getTag())) {
					kid.type = Type.E;
				} else if ("mfrac".equalsIgnoreCase(kid.getTag())) {
					kid.type = Type.Fr;
				} else if ("mspace".equalsIgnoreCase(kid.getTag())) {
					//kid.remove();
					removeList.add(kid);
				}
				if (kid.getChildCount() > 0) {
					assignTypes(kid);
				}
			}
		}

		/**
		 * Converts all mrow tags to either {@link Type.Term} or
		 * {@link Type.Series}
		 * 
		 * @param jNode
		 * @return
		 */
		private void convertChildrensMrow(JohnNode jNode) {
			List<JohnNode> kids = jNode.getChildren();
			if (kids == null) {
				return;
			}
			kids: for (JohnNode kid : kids) {
				if ("mrow".equalsIgnoreCase(kid.getTag())) {
					kid.type = Type.T;

					for (JohnNode baby : kid.getChildren()) {

						// For Series
						if ("mo".equalsIgnoreCase(baby.getTag())) {

							// Negate the next node because we'll delete all mo
							if ("−".equals(baby.toString())) {
								negatePropagate(baby.getNextSibling());
							}
							if ("−".equals(baby.toString())
									|| "+".equals(baby.toString())) {
								// A "-" at the beginning doesn't make it a
								// series
								if (baby.getIndex() > 0) {
									kid.type = Type.S;
								}

								removeList.add(baby);// Save <mo> for deletion
							}
							
							// For Δ: Δa should be treated as one variable
						} else if ("Δ".equals(baby.toString())) {

							kid.type = Type.V;
							kid.children = new LinkedList<JohnNode>();
							continue kids;
						} else if ("cos".equals(baby.toString())
								|| "sin".equals(baby.toString())) {
							kid.type = Type.Fn;
							//TODO not sure why removing the trig function also removes the argument???
							removeList.add(baby);
							continue kids;
						}
					}
				}
				if (kid.getChildCount() > 0) {
					convertChildrensMrow(kid);
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

		private void deleteRemoveListItems() {
			for (JohnNode r : removeList) {
				r.remove();
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
		private void findNestedMrows(JohnNode parent) {
			if (parent.getChildCount() == 0) {
				return;
			}

			List<JohnNode> kids = parent.getChildren();
			for (JohnNode kid : kids) {
				// If it's a term within a term or a series within a series
				if (((Type.S).equals(kid.getType()) && (Type.S).equals(parent
						.getType()))
						|| ((Type.T).equals(kid.getType()) && (Type.T)
								.equals(parent.getType()))) {
					nestedMrows.add(kid);
				}
				findNestedMrows(kid);
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
			for (JohnNode nest : nestedMrows) {
				List<JohnNode> kids = nest.getChildren();
				for (JohnNode kid : kids) {
					// Add to the beginning in order to preserve order, nests
					// come in pairs
					switch (nest.getIndex() % 2) {
					case 0:
						nest.getParent().add(0, kid);
						break;
					case 1:
						nest.getParent().add(1, kid);
						break;
					}
				}
				nest.remove();
			}
		}
	}
}