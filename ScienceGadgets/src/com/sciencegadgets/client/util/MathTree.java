package com.sciencegadgets.client.util;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HTML;

public class MathTree {
	
	MathNode root;
//	MathTree that; //global reference to tree object
	
	public MathTree(HTML mathML){
	//	that = this;
		
		Element first = mathML.getElement();
		String f = first.getTagName();
		System.out.println(f); //div
		
		Element second = (Element) first.getFirstChild();
		String s = second.getTagName();
		System.out.println(s); //math
		
		Element third = (Element) second.getFirstChild();
		String t = third.getTagName();
		System.out.println(t); //mrow
		
		//sanity check
//		if(!t.equals("mrow"))
//			throw new Exception("Not a valid MathML Equation!");
		
		root = new MathNode(third);
	}
	
	public Canvas getTreeDrawing(){		
		return (new MathCanvas()).getCanvas();
	}
	
	private class MathCanvas {
		Canvas canvas;
		Context2d context;
		
		//int row = 0; 		//current row to draw on
		int rowCount = 0; 	//number of items on the row
		
		int rowHeight = 100;
		int nodeWidth = 100;
		
		private MathCanvas(){
			canvas = Canvas.createIfSupported();
			Draw();
		}

		public Canvas getCanvas() {
			return canvas;
		}
	
		private void Draw(){
			
			int height = 1000;
			int width = 1000;
			
			context = canvas.getContext2d();
			context.setFont("50pt serif");
			canvas.setCoordinateSpaceHeight(height);
			canvas.setCoordinateSpaceWidth(width);
			canvas.setHeight(height + "px");
			canvas.setWidth(width + "px");
			
	
			Draw(root);
		}
		
		private void Draw(MathNode node){
			
			int r = 0;
			int w = 0;
			
			context.fillText(node.toString(),10,10);
			
			r++;
			
			for(MathNode n : node.getChildren()){
				Draw(n,r,w);
				w += nodeWidth;
			}
		}
		
		private void Draw(MathNode node,int r, int w){
			
			context.fillText(node.toString(),r*rowHeight, w);
			r++;
			
			w=0;
			for(MathNode n : node.getChildren()){
				Draw(n,r,w);
				w += 50;
			}
		}
		
	}//end class MathCanvas
	
	static int nodeCount = 0;
		
	private class MathNode{
		
		List<MathNode> children;
		
		//encapsulated dom node 
		Node node;
		
		String text;
		Type type;
		
		
		public MathNode(Node n){
			children = new LinkedList<MathNode>();
			node = n;
			type = determineType();
			text = getNodeText();	
			
			nodeCount++;
			System.out.println("creating " + this.toString() + " " + nodeCount);
			children = addChildren();
			
		}
		
		private List<MathNode> addChildren() {
			NodeList<Node> nodes = node.getChildNodes();
			List<MathNode> childs = new LinkedList<MathNode>(); 
			Node n;
			
			for(int i = 0; i < nodes.getLength(); i++){
				n = nodes.getItem(i);
				childs.add(new MathNode(n));
			}
				
			return childs;
		}
		
		public List<MathNode> getChildren(){
			return children;
		}
		
		private String getNodeText() {
			return ((Element)node).getInnerText();
		}

		private Type determineType(){
			String s = ((Element)node).getTagName();
			
			if(s == null) return Type.notype; //error
			
			if(s.equalsIgnoreCase("mrow"))
				return Type.mrow;
			else if(s.equalsIgnoreCase("mo"))
				return Type.mo;
			else if(s.equalsIgnoreCase("mi"))
				return Type.mi;
			else
				return Type.leaf;
		}
		
		public String toString(){
			//return "\'" + text + "\'" + " " + type.toString();
			return text;
		}
	}
	
	private static enum Type{
		mrow,   //term
		mi,		//variable
		mo,		//operator
		msub,	//variable with subscript
		mn,		//subscript
		notype, //error
		leaf,
	}
}
//
//switch(type){
//case mrow:
//	text = "";
//	makeBranch(this);
//break;
//case mo:
//case mi:
//	text = getNodeText();
//break;
//}
//
//private MathNode makeBranch (MathNode parent){
//
//NodeList<Node> children = parent.getChildren();
//
//Node leftNode = children.getItem(0);
//Node rightNode = children.getItem(2);
//Node middleNode = children.getItem(1); 
//
//MathNode middle = new MathNode(middleNode);
//middle.addLeftChild(new MathNode(leftNode));
//middle.addRightChild(new MathNode(rightNode));
//
//return middle;
//}
//
//private void getTagName(HTML)
//
//Node leftNode = children.getItem(0);
//Node rightNode = children.getItem(2);
//Node rootNode = children.getItem(1); 
//
//root = new MathNode(rootNode);
//System.out.println("adding root");
//root.addLeftChild(new MathNode(leftNode));
//System.out.println("adding left");
//root.addRightChild(new MathNode(rightNode));
//System.out.println("adding right");