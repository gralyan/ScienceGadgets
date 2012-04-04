package com.sciencegadgets.client;

import java.awt.Panel;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Rectangle;
import org.vaadin.gwtgraphics.client.shape.Text;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.JohnTree.MLtoJohnTree;
import com.sciencegadgets.client.AlgebraManipulation.MathMLParser;

public class JohnTree {

	private JohnNode root;
	private JohnNode leftSide;
	private JohnNode rightSide;

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

	public void draw(/* Canvas canvas */DrawingArea canvas) {
		// Context2d context = canvas.getContext2d();

		// int height = 600;
		// int width = 600;

		// canvas.setWidth(width + "px");
		// canvas.setHeight(height + "px");
		// canvas.setCoordinateSpaceWidth(width);
		// canvas.setCoordinateSpaceHeight(height);
		// context.setFont("bold 15px sans-serif");
		// int center = width / 2;

		// context.fillText("=", center, rowHeight);
		// drawChildren(context, this.root, center, childWidth, 2 * rowHeight);

		// (getChild()
		// context.arc(x, y, radius, startAngle, endAngle, anticlockwise)
		// context.fillText("=", center, 30);
		// context.fillText("a", center - 30, 60);
		// context.fillText("-", center + 30, 60);
		// context.fillText("x", center + 15, 90);
		// context.fillText("b", center + 45, 90);

		int center = canvas.getWidth() / 2;
		int rowHeight = 30;
		int childWidth = center / 5;
		canvas.add(new Text(center, 0, root.toString()));
		drawChildren(canvas, this.root, center, childWidth, rowHeight, (byte)1);
		
	}

	private void drawChildren(/* Context2d context */DrawingArea canvas,
			JohnNode pNode,int parentX, int childWidth, int rowHeight, byte layer) {

		List<JohnNode> kids = pNode.getChildren();

		int offset = pNode.getChildCount() * childWidth / 2;
		int layerHeight = 2*rowHeight*layer;

		for (JohnNode child : kids) {
			// context.fillText(child.toString(), (center + offset), rowHeight);
			Text text = new Text((parentX-offset),layerHeight, child.toString());
			text.setFontSize(12);
			//text.setWidth(childWidth+"px");
			//text.setHeight(rowHeight+"px");
			
			int x = text.getAbsoluteLeft()+(childWidth/2);
			int y = text.getAbsoluteTop()+rowHeight;
			
			Rectangle box = new Rectangle(text.getX(), text.getY(), text.getTextWidth(), text.getTextHeight());
			Line line = new Line( parentX, layerHeight,x,y);
			canvas.add(line);
			canvas.add(box);
			canvas.add(text);
			if (child.getChildCount() != 0) {
				drawChildren(canvas, child,x, childWidth,
						rowHeight, ++layer);
			}
			offset -= (childWidth);
		}

	}

	// private DrawNode(){

	// }

	private class JohnNode {
		// encapsulated dom node
		@SuppressWarnings("unused")
		private Node node;
		@SuppressWarnings("unused")
		private Type type;
		private String symbol;

		private JohnNode parent;
		private int indexInSiblings;
		private List<JohnNode> children = new LinkedList<JohnNode>();

		public JohnNode(Node n) {
			node = n;
			type = determineType(n);
			symbol = ((Element) n).getInnerText();
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

		private void add(JohnNode jNode) {
			children.add(jNode);
			jNode.indexInSiblings = children.indexOf(jNode);
			jNode.parent = this;
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

		public String toString() {
			return symbol;
		}

		@SuppressWarnings("unused")
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
			// nLeft = new JohnNode(super.elLeft);
			// nEq = new JohnNode(super.elEquals);
			// nRight = new JohnNode(super.elRight);
			// prevLeftNode = nLeft;
			// prevRightNode = nRight;
		}

		@Override
		protected void onRootsFound(Node nodeLeft, Node nodeEquals,
				Node nodeRight) {
			nLeft = new JohnNode(nodeLeft);
			nEq = new JohnNode(nodeEquals);
			nRight = new JohnNode(nodeRight);

			// johnTree = new JohnTree(nLeft, nEq, nRight);
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

