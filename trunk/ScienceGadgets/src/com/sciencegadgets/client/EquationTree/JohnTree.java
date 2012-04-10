package com.sciencegadgets.client.EquationTree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math.ConvergenceException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.ScienceGadgets;
import com.sciencegadgets.client.AlgebraManipulation.MLElementWrapper;
import com.sciencegadgets.client.EquationTree.JohnTree.JohnNode;
import com.sciencegadgets.client.EquationTree.JohnTree.MLTreeToMathTree;

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
		MLtoJohnTree ml = new MLtoJohnTree(mathML);
		wrappers = ml.wrappers;
		root = ml.nEq;
		leftSide = ml.nLeft;
		rightSide = ml.nRight;
		root.add(leftSide);
		root.add(rightSide);

		if (isParsedForMath) {
			MLTreeToMathTree mathTree = new MLTreeToMathTree(this);
		}
	}

	public JohnTree(JohnNode leftSide, JohnNode equalsRoot, JohnNode rightSide) {
		root = equalsRoot;
		this.leftSide = leftSide;
		this.rightSide = rightSide;
		root.add(this.leftSide);
		root.add(this.rightSide);
	}

	/*
	 * public JohnTree(Node leftSide, Node equalsRoot, Node rightSide) { root =
	 * new JohnNode(equalsRoot); this.leftSide = new JohnNode(leftSide);
	 * this.rightSide = new JohnNode(rightSide); root.add(this.leftSide);
	 * root.add(this.rightSide); }
	 */
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

	/*
	 * public void draw(AbsolutePanel panel) { // Widgets displaying nodes will
	 * be added to the panel, drawings like // connecting likes are added to the
	 * canvas. Both are same size, canvas // in panel canvas = new
	 * DrawingArea(panel.getOffsetWidth(), panel.getOffsetHeight());
	 * panel.add(canvas); this.panel = panel;
	 * 
	 * int center = canvas.getWidth() / 2; rowHeight = canvas.getHeight() / 10;
	 * childWidth = center / 5;
	 * 
	 * panel.add(this.root.toMathML(), center, 0); drawChildren(this.root,
	 * center, (byte) 1);
	 * 
	 * }
	 * 
	 * private void drawChildren(JohnNode pNode, int parentX, byte layer) {
	 * 
	 * List<JohnNode> kids = pNode.getChildren();
	 * 
	 * // The distance from the center of the parent node to the farthest edge
	 * // of the child int offset = pNode.getChildCount() * childWidth / 2; int
	 * layerHeight = 2 * rowHeight * layer;
	 * 
	 * for (JohnNode child : kids) { HTML childHTML = child.toMathML();
	 * panel.add(childHTML, (parentX - offset), layerHeight);
	 * 
	 * int childLeft = childHTML.getAbsoluteLeft()-panel.getAbsoluteLeft(); int
	 * childTop = childHTML.getAbsoluteTop() - panel.getAbsoluteTop();
	 * 
	 * int pad = 5; int lineX = childLeft + childHTML.getOffsetWidth()/2 +
	 * pad;// (childWidth / 2); int lineY = childTop;
	 * 
	 * 
	 * Rectangle box = new Rectangle(childLeft - pad, childTop,
	 * childHTML.getOffsetWidth() + 2 * pad, childHTML.getOffsetHeight() * 4 /
	 * 3); Line line = new Line(parentX, layerHeight-rowHeight, lineX, lineY);
	 * canvas.add(line); canvas.add(box); if (child.getChildCount() != 0) {
	 * drawChildren(child, lineX, (byte)(layer+1)); } offset -= (childWidth); }
	 * 
	 * }
	 */
	// private DrawNode(){

	// }

	protected class JohnNode {
		// encapsulated dom node
		private Node domNode;
		@SuppressWarnings("unused")
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
			// children.add(jNode);
			// jNode.indexInSiblings = children.indexOf(jNode);
			// jNode.parent = this;
		}

		private void add(JohnNode johnNode) {
			children.add(johnNode);
			// johnNode.indexInSiblings = children.indexOf(johnNode);
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
			int nextIndex = this.getParent().getChildren().indexOf(this) + 1;// this.indexInSiblings
																				// +
																				// 1;
			return this.getParent().getChildAt(nextIndex);
		}

		public void remove() {
			// int index = this.indexInSiblings;
			List<JohnNode> sibs = this.parent.getChildren();
			sibs.remove(this.parent.getChildren().lastIndexOf(this));
			/*
			 * for (JohnNode child : sibs) { if (child.indexInSiblings > index)
			 * { child.indexInSiblings -= 1; } }
			 */}

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
				mathML = new HTML(type + tag + " " + "$" + symbol + "$");
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
		Term, Series, Equals, Function, Variable, Number;
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

		JohnNode mathRight;
		JohnNode mathLeft;

		MLTreeToMathTree(JohnTree jTree) {
			mathLeft = jTree.getLeftSide();
			mathRight = jTree.getRightSide();
			commenseRevolution(mathLeft);
			commenseRevolution(mathRight);
		}

		private void commenseRevolution(JohnNode jNode) {
			if (jNode.getTag().equalsIgnoreCase("mrow")) {
				convertRow(jNode);
				rearrangeRows(jNode);
			}
		}

		private void convertRow(JohnNode jNode) {
			List<JohnNode> kids = jNode.getChildren();
			jNode.type = Type.Term;

			for (JohnNode child : kids) {
				if (child.getTag().equalsIgnoreCase("mo")) {
					jNode.type = Type.Series;
					child.remove();
				}
				if (child.getTag().equalsIgnoreCase("mrow")) {
					convertRow(child);
				}
			}

		}

		private void rearrangeRows(JohnNode jNode) {
			List<JohnNode> kids = jNode.getChildren();

			for (JohnNode kid : kids) {
				if ((Type.Series).equals(jNode.parent.getType())) {
					List<JohnNode> orphans = kid.getChildren();
					
					for (JohnNode orphan : orphans) {
						Window.alert(jNode.toString());
						orphan.parent = jNode.parent;
						jNode.parent.children.add(orphan);
						jNode.remove();

					}
				}
				if(kid.getChildCount() >0){
					rearrangeRows(kid);
				}
			}
		}
	}
}