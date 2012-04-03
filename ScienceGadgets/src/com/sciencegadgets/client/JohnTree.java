package com.sciencegadgets.client;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HTML;

public class JohnTree{

	private JohnNode root;
	private JohnNode left;
	private JohnNode right;
	
	public JohnTree(HTML mathML){
		
		//get mathML tree
		NodeList<Node> topLayer = mathML.getElement().getFirstChild()
				.getFirstChild().getChildNodes();		
		Node equal = topLayer.getItem(1);
		Node leftSide = topLayer.getItem(0);
		Node rightSide = topLayer.getItem(2);
		
		//print for debug
		//System.out.println("root " + ((Element)equal).getInnerText());
		//System.out.println("left " + ((Element)l).getInnerText());
		//System.out.println("right " + ((Element)r).getInnerText());
		
		//Cast to John Nodes
		root = new JohnNode(equal);
		
		root.add(leftSide);
		root.add(rightSide);
		
		//Draw(Context2d);
	}
	
	public void Draw(Canvas canvas){
		Context2d context = canvas.getContext2d();
		
		int height = 600;
		int width = 600;
		
		canvas.setWidth(width + "px");
		canvas.setHeight(height + "px");
		canvas.setCoordinateSpaceWidth(width);
		canvas.setCoordinateSpaceHeight(height);
		context.setFont("bold 40px sans-serif"); 
		
		int w = width/2;
		
		//(getChild()
		
		context.fillText("=", w, 30);
		//context.arc(x, y, radius, startAngle, endAngle, anticlockwise)
		context.fillText("a", w-30, 60);
		context.fillText("-", w+30, 60);
		context.fillText("x", w+15, 90);
		context.fillText("b", w+45, 90);
	}
	
	//private DrawNode(){
		
	//}
	
	private class JohnNode{
		//encapsulated dom node
		private Node node;
		private Type type;
		private String symbol;
		
		
		private List<JohnNode> children = new LinkedList<JohnNode>();
		
		public JohnNode(Node n){
			node = n;
			type = determineType(n);
			symbol = ((Element)n).getInnerText();
			
			add(n);
		}

		/**
		 * Determines the type of node <p>eg. Operand, Equals...</p><p>Returns null if none found</p>
		 * @param node
		 * @return type of node
		 */
		private Type determineType(Node node){
			String tag = node.getNodeName();
			
			if (tag.equalsIgnoreCase("mn"))
				return Type.Operand;
			else if(tag.equalsIgnoreCase("mi"))
				return Type.Operand;
			else if(tag.equalsIgnoreCase("mo")){
				if(tag.equalsIgnoreCase("="))// fixxxx
					return Type.Equals;
				else
					return Type.Operator;
			}
		//	throw new Exception("DERP");
			return null;
		}
		
		/**
		 * The tree is expanded by adding MatHML rows.
		 * This method splits the MathML row into JohnNodes which are added to the JohnTree
		 */
		public void add(Node n){
			if(n.getChildCount() >  1){
				//This is temporary, adds in flat order, not hierachy order of operations :(
				for(int i=0 ; i<n.getChildCount() ; i++){
						Node  node = n.getChild(i);
						String x = ((Element)node).getInnerText();
						System.out.println("CHILD   NODE: " + x);
						this.add(new JohnNode(node));
				}
			}
		}
		
		private void add(JohnNode jn){
			children.add(jn);
			System.out.println("Adding " + jn.toString() + " as  " + type.toString());
		}
		
		public void  addChildren(){
			NodeList<Node> c = node.getChildNodes();
			int s = c.getLength();
			Node n;
			
			for(int i = 0; i < s; i++){
				n = c.getItem(i);
				children.add(new JohnNode(n));
			}
		}
		
		public List<JohnNode>  getChildren(){
			return children;
		}
		
		public String toString(){
			return symbol;
		}
	}
	
	private static enum Type{
		Term,
		Equals,
		Operator,
		Operand;
	}
}









/*
String row = ((Element)n).getInnerText();
int size = row.length();

for(int i = 0; i < size; i++){
	String c = "" + row.charAt(i);
	HTML  h = new HTML(c);
	Node x = (Node)h.getElement();
	Fragments.add(new JohnNode(x));
}


for(JohnNode y : Fragments)
	this.add(y);*/










