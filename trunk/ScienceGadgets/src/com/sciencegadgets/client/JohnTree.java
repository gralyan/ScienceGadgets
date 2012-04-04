package com.sciencegadgets.client;

import java.util.LinkedList;
import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.AlgebraManipulation.MathMLParser;

public class JohnTree {

	private JohnNode root;
	private JohnNode leftSide;
	private JohnNode rightSide;
	private int childWidth;
	private int rowHeight;
	private AbsolutePanel panel;
	private DrawingArea canvas;

	public JohnTree(HTML mathML) {
		MLtoJohnTree ml = new MLtoJohnTree(mathML);
		root = ml.nEq;
		leftSide = ml.nLeft;
		rightSide = ml.nRight;
		root.add(leftSide);
		root.add(rightSide);
	}

	public JohnTree(JohnNode leftSide, JohnNode equalsRoot, JohnNode rightSide) {
		root = equalsRoot;
		this.leftSide = leftSide;
		this.rightSide = rightSide;
		root.add(this.leftSide);
		root.add(this.rightSide);
	}

	public JohnTree(Node leftSide, Node equalsRoot, Node rightSide) {
		root = new JohnNode(equalsRoot);
		this.leftSide = new JohnNode(leftSide);
		this.rightSide = new JohnNode(rightSide);
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
	/*
	public void draw(AbsolutePanel panel) {
		// Widgets displaying nodes will be added to the panel, drawings like
		// connecting likes are added to the canvas. Both are same size, canvas
		// in panel
		canvas = new DrawingArea(panel.getOffsetWidth(),
				panel.getOffsetHeight());
		panel.add(canvas);
		this.panel = panel;

		int center = canvas.getWidth() / 2;
		rowHeight = canvas.getHeight() / 10;
		childWidth = center / 5;

		panel.add(this.root.toMathML(), center, 0);
		drawChildren(this.root, center, (byte) 1);

	}

	private void drawChildren(JohnNode pNode, int parentX, byte layer) {

		List<JohnNode> kids = pNode.getChildren();

		// The distance from the center of the parent node to the farthest edge
		// of the child
		int offset = pNode.getChildCount() * childWidth / 2;
		int layerHeight = 2 * rowHeight * layer;

		for (JohnNode child : kids) {
			HTML childHTML = child.toMathML();
			panel.add(childHTML, (parentX - offset), layerHeight);

			int childLeft = childHTML.getAbsoluteLeft()-panel.getAbsoluteLeft();
			int childTop = childHTML.getAbsoluteTop() - panel.getAbsoluteTop();
			
			int pad = 5;
			int lineX = childLeft + childHTML.getOffsetWidth()/2 + pad;// (childWidth / 2);
			int lineY = childTop;


			Rectangle box = new Rectangle(childLeft - pad,
					childTop,
					childHTML.getOffsetWidth() + 2 * pad,
					childHTML.getOffsetHeight() * 4 / 3);
			Line line = new Line(parentX, layerHeight-rowHeight, lineX, lineY);
			canvas.add(line);
			canvas.add(box);
			if (child.getChildCount() != 0) {
				drawChildren(child, lineX, (byte)(layer+1));
			}
			offset -= (childWidth);
		}

	}
*/
	// private DrawNode(){

	// }

	protected class JohnNode {
		// encapsulated dom node
		private Node domNode;
		@SuppressWarnings("unused")
		private Type type;
		private String symbol;

		private JohnNode parent;
		private int indexInSiblings;
		private List<JohnNode> children = new LinkedList<JohnNode>();

		public JohnNode(Node node) {
			domNode = node;
			type = determineType(node);
			symbol = ((Element) node).getInnerText();
		}

		/**
		 * Determines the type of node eg. Operand, Equals...
		 * <p>
		 * Returns null if none found
		 * </p>
		 * 
		 * @param node
		 * @return type of node from the emum Type
		 */
		private Type determineType(Node node) {
			String tag = node.getNodeName();

			if (tag.equalsIgnoreCase("mn"))
				return Type.Operand;
			else if (tag.equalsIgnoreCase("mi"))
				return Type.Operand;
			else if (tag.equalsIgnoreCase("mo")) {
				if (tag.equalsIgnoreCase("="))// fixxxx
					return Type.Equals;
				else
					return Type.Operator;
			}
			// throw new Exception("DERP");
			return null;
		}

		/**
		 * Adds a {@link Node} to the {@link JohnTree} by creating a new
		 * {@link JohnNode}
		 * 
		 */
		public void add(Node node) {
			add(new JohnNode(node));
			// children.add(jNode);
			// jNode.indexInSiblings = children.indexOf(jNode);
			// jNode.parent = this;
		}

		private void add(JohnNode johnNode) {
			children.add(johnNode);
			johnNode.indexInSiblings = children.indexOf(johnNode);
			johnNode.parent = this;
		}

		public List<JohnNode> getChildren() {
			return children;
		}

		@SuppressWarnings("unused")
		public JohnNode getFirstChild() {
			return children.get(0);
		}

		public JohnNode getChildAt(int index) {
			return children.get(index);
		}

		public int getChildCount() {
			return children.size();
		}

		@SuppressWarnings("unused")
		public JohnNode getNextSibling() {
			int nextIndex = this.indexInSiblings + 1;
			return this.getParent().getChildAt(nextIndex);
		}

		public JohnNode getParent() {
			return parent;
		}
		@SuppressWarnings("unused")
		public Node getDomNode(){
			return domNode;
		}

		public String toString() {
			return symbol;
		}

		public HTML toMathML() {
			HTML mathML = new HTML("$" + symbol + "$");
			ScienceGadgets.parseJQMath(mathML.getElement());
			return mathML;
		}
	}

	private static enum Type {
		Term, Equals, Operator, Operand;
	}

	class MLtoJohnTree extends MathMLParser {
		// private JohnTree johnTree;
		private JohnNode prevLeftNode;
		private JohnNode prevRightNode;
		private JohnNode curNode;
		JohnNode nLeft;
		JohnNode nEq;
		JohnNode nRight;

		public MLtoJohnTree(HTML mathMLequation) {
			super(mathMLequation);
		}
		@Override
		protected void onRootsFound(Node nodeLeft, Node nodeEquals,
				Node nodeRight) {
			nLeft = new JohnNode(nodeLeft);
			nEq = new JohnNode(nodeEquals);
			nRight = new JohnNode(nodeRight);

			prevLeftNode = nLeft;
			prevRightNode = nRight;
		}

		// TODO may possibly add children linearly???
		@Override
		protected void onVisitNode(Node currentNode, Boolean isLeft) {
			if (isLeft) {
				curNode = new JohnNode(currentNode);
				prevLeftNode.add(curNode);
				prevLeftNode = curNode;
			} else {
				curNode = new JohnNode(currentNode);
				prevRightNode.add(currentNode);
				prevLeftNode = curNode;
			}

		}

	}

}

/*
 * String row = ((Element)n).getInnerText(); int size = row.length();
 * 
 * for(int i = 0; i < size; i++){ String c = "" + row.charAt(i); HTML h = new
 * HTML(c); Node x = (Node)h.getElement(); Fragments.add(new JohnNode(x)); }
 * 
 * 
 * for(JohnNode y : Fragments) this.add(y);
 */

