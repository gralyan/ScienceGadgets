package com.sciencegadgets.client;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.JohnTree.MLtoJohnTree;
import com.sciencegadgets.client.AlgebraManipulation.MathMLParser;

public class JohnTree {

	private JohnNode root;
	private JohnNode leftSide;
	private JohnNode rightSide;

	public JohnTree(HTML mathML) {
		JohnTree johnT = new MLtoJohnTree(mathML).johnTree;
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

	public void draw(Canvas canvas) {
		Context2d context = canvas.getContext2d();
		
		int height = 600;
		int width = 600;

		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
		context.setFont("bold 40px sans-serif");

		int center = width / 2;
		int rowHeight = 30;
		int childWidth = 60;
		
		context.fillText("=", center, rowHeight);
		drawChildren(context, this.root, childWidth, 2*rowHeight);
		
		// (getChild()
		// context.arc(x, y, radius, startAngle, endAngle, anticlockwise)
		//context.fillText("=", center, 30);
		//context.fillText("a", center - 30, 60);
		//context.fillText("-", center + 30, 60);
		//context.fillText("x", center + 15, 90);
		//context.fillText("b", center + 45, 90);
	}
	private void drawChildren(Context2d context, JohnNode node,int childWidth, int rowHeight){
		System.out.println("aaaa");
		List<JohnNode> kids = node.getChildren();
		System.out.println("bb");
		int leftMost = -(kids.size()-childWidth)/2;
		for(JohnNode child : kids){
			context.fillText(child.toString(), leftMost, rowHeight);
			if(child.getChildren().size() != 0){
			drawChildren(context, child,(leftMost + (childWidth/2)), rowHeight+30);
		}}
		
	}

	// private DrawNode(){

	// }

	private class JohnNode {
		// encapsulated dom node
		private Node node;
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
		 * Determines the type of node 
		 * eg. Operand, Equals...
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
			//children.add(jNode);
			//jNode.indexInSiblings = children.indexOf(jNode);
			//jNode.parent = this;
		}
		private void add(JohnNode jNode) {
			children.add(jNode);
			jNode.indexInSiblings = children.indexOf(jNode);
			jNode.parent = this;
		}
		public List<JohnNode> getChildren() {
			return children;
		}
		public JohnNode getFirstChild(){
			return children.get(0);
		}
		public JohnNode getChildAt(int index){
			return children.get(index);
		}
		public JohnNode getNextSibling(){
			int nextIndex = this.indexInSiblings + 1;
			return this.getParent().getChildAt(nextIndex);
		}
		public JohnNode getParent(){
			return parent;
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
		private JohnTree johnTree;
		private JohnNode prevLeftNode;
		private JohnNode prevRightNode;
		private JohnNode curNode;

		public MLtoJohnTree(HTML mathMLequation) {
			super(mathMLequation);
		}

		@Override
		protected void onRootsFound(Node nodeLeft, Node nodeEquals,
				Node nodeRight) {
			JohnNode nLeft = new JohnNode(nodeLeft);
			JohnNode nEq = new JohnNode(nodeEquals);
			JohnNode nRight = new JohnNode(nodeRight);
			

			johnTree = new JohnTree(nLeft, nEq, nRight);
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
		public JohnTree getJohnTree(){
			return johnTree;
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

