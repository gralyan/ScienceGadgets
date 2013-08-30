package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.DOM;
import com.sciencegadgets.client.algebra.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.algebra.MathMLBindingTree.Type;

public class EquationHTML {
	
	MathMLBindingTree mathTree;

	public static Element makeEquationHTML(MathMLBindingTree mathTree) {
		
		Element eqHTML = DOM.createDiv();
		eqHTML.setClassName("Equation");
		eqHTML.setAttribute("id", "Root");

		for(MathMLBindingNode topNode : mathTree.getRoot().getChildren()){
			makeHTMLNode(topNode, eqHTML);
		}
		return eqHTML;
	}

	/**
	 * Recursive creation of the display tree. Makes a display node equivalent
	 * of
	 * 
	 * @param mlNode
	 * <br/>
	 *            and adds it to<br/>
	 * @param displayParentEl
	 */
	private static void makeHTMLNode(MathMLBindingNode node, Element displayParentEl) {
		Element mlNode = node.getMLNode();
		
		String id = mlNode.getAttribute("id");
		Type type = node.getType();
		Type parentType = node.getParentType();

		// make new display node with appropriate properties
		Element nodeHTML = DOM.createDiv();
		node.setNodeHTML(nodeHTML);
		nodeHTML.setId(id);
		nodeHTML.addClassName(type.toString());

		switch (parentType) {
		case Fraction:
			if (node.getIndex() == 0) {
				nodeHTML.addClassName(Type.Fraction.asChild()+"-numerator");
			} else if (node.getIndex() == 1) {
				nodeHTML.addClassName(Type.Fraction.asChild()+"-denominator");
			}
			break;
		case Exponential:
			if (node.getIndex() == 0) {
				nodeHTML.addClassName(Type.Exponential.asChild()+"-base");
			} else if (node.getIndex() == 1) {
				nodeHTML.addClassName(Type.Exponential.asChild()+"-exponent");
			}
			break;
		case Equation:
		case Sum:
		case Term:
			nodeHTML.addClassName(parentType.asChild());
		}

		displayParentEl.appendChild(nodeHTML);

		for (int i = 0; i < mlNode.getChildCount(); i++) {
			Node child = mlNode.getChild(i);
			
			//Recursive creation
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				makeHTMLNode(node.getChildAt(i), nodeHTML);
			//Inner text operation adjustment
			} else if (child.getNodeType() == Node.TEXT_NODE) {
				String text = mlNode.getInnerText();
				if (text.startsWith("&")) { // must insert as js code
					text = node.getOperation().getSign();
				}
				nodeHTML.setInnerText(text);
			}
		}
	}
}
