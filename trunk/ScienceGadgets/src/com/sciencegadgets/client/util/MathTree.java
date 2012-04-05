package com.sciencegadgets.client.util;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HTML;

public class MathTree {
	
	MathNode root;
	
	public MathTree(HTML mathML){
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
			System.out.println("creating " + this.toString());
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
		
		private String getNodeText() {
			return ((Element)node).getInnerText();
		}

		private Type determineType(){
			String s = ((Element)node).getTagName();
			if(s.equals("mrow"))
				return Type.mrow;
			else if(s.equals("mo"))
				return Type.mo;
			else if(s.equals("mi"))
				return Type.mi;
				
			return Type.notype; //error
		}
		
		public String toString(){
			return "\'" + text + "\'" + " " + type.toString();
		}
	}
	
	private static enum Type{
		mrow,   //term
		mi,		//variable
		mo,		//operator
		msub,	//variable with subscript
		mn,		//subscript
		notype, //error
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