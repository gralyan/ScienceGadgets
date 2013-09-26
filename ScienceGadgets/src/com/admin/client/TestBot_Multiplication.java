package com.admin.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.transformations.MultiplyTransformations;

public class TestBot_Multiplication extends MultiplyTransformations {
	 public static void deployTestBot() throws TopNodesNotFoundException {
	 String sum =
	 "<mfenced separators=\"\" open=\"\" close=\"\"><mi>s</mi><mo>+</mo><mi>u</mi><mo>+</mo><mrow><mi>m</mi><mo>\u00B7</mo><mi>m</mi></mrow></mfenced>";
	 String term =
	 "<mrow><mi>t</mi><mo>\u00B7</mo><msup><mi>e</mi><mi>r</mi></msup><mo>\u00A0</mo><mi>m</mi></mrow>";
	 String exp =
	 "<msup><mrow><mi>e</mi><mo>\u00B7</mo><mi>x</mi></mrow><mi>p</mi></msup>";
	 String frac =
	 "<mfrac><mrow><mi>n</mi><mo>\u00B7</mo><mi>u</mi><mo>\u00B7</mo><mi>m</mi></mrow><mrow><mi>d</mi><mo>\u00B7</mo><mi>e</mi><mo>\u00B7</mo><mi>n</mi></mrow></mfrac>";
	 String var = "<mi>v</mi>";
	 String num = "<mn>2</mn>";
	
	 String expA =
	 "<msup><mi>a</mi><mfenced><mi>s</mi><mo>+</mo><mi>u</mi><mo>+</mo><mrow><mi>m</mi><mo>\u00B7</mo><mi>m</mi></mrow></mfenced></msup>";
	 String expB =
	 "<msup><mi>a</mi><mfenced><mi>u</mi><mo>+</mo><mrow><mi>m</mi><mo>\u00B7</mo><mi>m</mi></mrow><mo>+</mo><mi>s</mi></mfenced></msup>";
	 String expC =
	 "<msup><mi>a</mi><mfenced><mi>m</mi><mo>+</mo><mrow><mi>u</mi><mo>\u00B7</mo><mi>m</mi></mrow><mo>+</mo><mi>s</mi></mfenced></msup>";
	
	 String[] types = { expA, expB, expC};
	 // String[] types = { sum, exp, frac, var, num };
	 for (String type : types) {
	 // for (String tipe : types) {
	
	 Element leftElement = (Element) new HTML(expA).getElement()
	 .getFirstChildElement().cloneNode(true);
	 Element multiplyElement = (Element) new HTML(
	 "<mo> \u00D7 </mo>").getElement()
	 .getFirstChildElement().cloneNode(true);
	 Element rightElement = (Element) new HTML(type).getElement()
	 .getFirstChildElement().cloneNode(true);
	
	 HTML disp = new HTML(
	 "<math><mpadded class=\"parentDummy\"></mpadded><mo>=</mo><mi>inEquation</mi></math>");
	 Element mathElement = disp.getElement().getFirstChildElement();
	 Element parentElement = new HTML(
	 "<mrow><mi>L</mi><mo>( </mo><mo> )</mo><mi>R</mi></mrow>")
	 .getElement().getFirstChildElement();
	 Element parentdummy = mathElement.getElementsByTagName(
	 "mpadded").getItem(0);
	 Element grandParentElement = parentdummy.getParentElement();
	 grandParentElement.insertBefore(parentElement, parentdummy);
	 parentdummy.removeFromParent();
	
	 parentElement.insertAfter(leftElement, parentElement
	 .getElementsByTagName("mo").getItem(0));
	 parentElement.insertAfter(multiplyElement, leftElement);
	 parentElement.insertAfter(rightElement, multiplyElement);
	
	 MathTree mathTree = new MathTree(mathElement, false);
	
	 MathNode leftNode = mathTree.idMap.get(leftElement
	 .getAttribute("id"));
	 MathNode rightNode = mathTree.idMap.get(rightElement
	 .getAttribute("id"));
	 operation = mathTree.idMap.get(multiplyElement
	 .getAttribute("id"));
	 parent = operation.getParent();
	 grandParent = parent.getParent();
	
	 HTML dispBefore = new HTML(disp.getElement()
	 .getFirstChildElement().getString());
	
	 // System.out.println("grandParent\t: " + grandParent.toString());
	 // System.out.println("parent\t: " + parent.toString());
	 // System.out.println("leftNode\t: " + leftNode.toString());
	 // System.out.println("multiply\t: " + multiply.toString());
	 // System.out.println("rightNode\t: " + rightNode.toString());
	
	 RootPanel.get().add(new HTML("before"));
	 RootPanel.get().add(dispBefore);
	 // System.out.println("before");
	 // System.out.println(dispBefore.getElement()
	 // .getFirstChildElement().getString());
	 assign(leftNode, operation, rightNode);
	 RootPanel.get().add(new HTML("after"));
	 RootPanel.get().add(
	 new HTML(disp.getElement().getFirstChildElement()
	 .getString()));
	 RootPanel.get().add(new HTML("&nbsp;"));
	 // System.out.println("after");
	 // System.out.println(disp.getElement().getFirstChildElement()
	 // .getString());
	
	 // }
	 }
	
	 }
}
