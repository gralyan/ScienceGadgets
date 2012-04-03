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
		Node l = topLayer.getItem(0);
		Node r = topLayer.getItem(2);
		
		//print for debug
		System.out.println("root " + ((Element)equal).getInnerText());
		System.out.println("left " + ((Element)l).getInnerText());
		System.out.println("right " + ((Element)r).getInnerText());
		
		
		//Cast to John Nodes
		root = new JohnNode(equal);
		
		root.add(l);
		root.add(r);
		
		/*
		JohnNode left = new JohnNode(l);
		JohnNode right = new JohnNode(r);
		*/
		
		/*
		left.getChildren();
		right.getChildren();
		
		
		//finish the tree
		root.add(left);
		root.add(right);
		*/
		
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
		
		context.fillText("=", w, 30);
		//context.arc(x, y, radius, startAngle, endAngle, anticlockwise)
		context.fillText("a", w-30, 60);
		context.fillText("-", w+30, 60);
		context.fillText("x", w+15, 90);
		context.fillText("b", w+45, 90);
	}
	
	private class JohnNode{
		Type type;
		String symbol;
		
		//encapsulated dom node
		Node node;
		
		private List<JohnNode> children = new LinkedList<JohnNode>();
		
		
		//Nodes may be rows!
		public JohnNode(Node n){
			
			
			
			node = n;
			determineType(n);
			symbol = ((Element)n).getInnerText();
		}
		
		public void Fragment(Node n){
			List<JohnNode>Fragments = new LinkedList<JohnNode>();
						
			String row = ((Element)n).getInnerText();
			int size = row.length();
			
			for(int i = 0; i < size; i++){
				String c = "" + row.charAt(i);
				HTML  h = new HTML(c);
				Node x = (Node)h.getElement();
				Fragments.add(new JohnNode(x));
			}
			
			//This is temporary, adds in order, not by order of operations :(
			for(JohnNode y : Fragments)
				this.add(y);
		}
		
		private void determineType(Node n){
			String tag = n.getNodeName();
			
			if (tag.equalsIgnoreCase("mn"))
				type = Type.Operand;
			else if(tag.equalsIgnoreCase("mo")){
				if(tag.equalsIgnoreCase("="))// fixxxx
					type = Type.Equals;
				else
					type = Type.Operator;
			}			
		}
		
		/*
		 * The tree is expanded by adding MatHML rows.
		 * This method splits the MathML row into JohnNodes which are added to the JohnTree
		 */
		public void add(Node n){
			Fragment(n);
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