package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.algebra.MathMLBindingTree.Operator;
import com.sciencegadgets.client.algebra.MathMLBindingTree.Type;

public class EquationHTML extends HTML {

	Element mlTree;
	private double fontPercent = 0;
	public boolean autoFillParent = true;

	public EquationHTML(Element mlTree) {
		this.mlTree = mlTree;
		this.setStyleName("Equation");

		NodeList<Node> children = mlTree.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			makeHTMLNode((Element) children.getItem(i), this.getElement());
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		if (autoFillParent) {
			resizeEquation();
		}
		matchHeightsAndAlign(this.getElement());
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
	private static void makeHTMLNode(Element mlNode, Element displayParentEl) {
		Element mlParent = mlNode.getParentElement();

		String id = mlNode.getAttribute("id");
		Type type = Type.getType(mlNode.getTagName());
		Type parentType = Type.getType(mlParent.getTagName());

		// make new display node with appropriate properties
		Element nodeHTML = DOM.createDiv();
		nodeHTML.setId(id);
		nodeHTML.addClassName(type.toString());

		switch (parentType) {
		case Fraction:
			boolean isNumerator = mlNode
					.equals(mlParent.getFirstChildElement());
			boolean isDenominator = mlNode.equals(mlParent
					.getFirstChildElement().getNextSiblingElement());
			if (isNumerator) {
				nodeHTML.addClassName(Type.Fraction.asChild() + "-numerator");
			} else if (isDenominator) {
				nodeHTML.addClassName(Type.Fraction.asChild() + "-denominator");
			}
			break;
		case Exponential:
			boolean isBase = mlNode.equals(mlParent.getFirstChildElement());
			boolean isExponential = mlNode.equals(mlParent
					.getFirstChildElement().getNextSiblingElement());
			if (isBase) {
				nodeHTML.addClassName(Type.Exponential.asChild() + "-base");
			} else if (isExponential) {
				nodeHTML.addClassName(Type.Exponential.asChild() + "-exponent");
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

			// Recursive creation
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				makeHTMLNode((Element) child, nodeHTML);
				// Inner text operation adjustment
			} else if (child.getNodeType() == Node.TEXT_NODE) {
				String text = mlNode.getInnerText();
				if (text.startsWith("&")) { // must insert as js code
					for (Operator op : Operator.values()) {
						if (op.getHTML().equals(text)) {
							text = op.getSign();
						}
					}
				}
				nodeHTML.setInnerText(text);
			}
		}
	}

	/**
	 * Resizes the equation to fill the panel
	 * 
	 * @param el
	 */
	private void resizeEquation() {
		double widthRatio = (double) this.getParent().getOffsetWidth()
				/ this.getOffsetWidth();
		double heightRatio = (double) this.getParent().getOffsetHeight()
				/ this.getOffsetHeight();

		double smallerRatio = (widthRatio > heightRatio) ? heightRatio
				: widthRatio;

		fontPercent = smallerRatio * 95;// *.95 for looser fit, *100 for percent
		this.getElement().getStyle().setFontSize((fontPercent), Unit.PCT);
	}

	/**
	 * Matches the heights of all the children of an {@link Type.Equation},
	 * {@link Type.Term} or {@link Type.Sum} by:<br/>
	 * 1.Lifting centers to the tallest denominator using padding-bottom<br/>
	 * 2.Matching tops to tallest height with padding-top<br/>
	 * <b>Note:</b> All children of these nodes are initially aligned at their
	 * baseline
	 */
	private void matchHeightsAndAlign(Element curEl) {

		if (curEl.getChildCount() > 0) {
			for (int i = 0; i < curEl.getChildCount(); i++) {
				if (Node.ELEMENT_NODE == curEl.getChild(i).getNodeType()) {
					matchHeightsAndAlign((Element) curEl.getChild(i));
				}
			}
		}
		// This method is only appropriate for type Equation, Term, or Sum
		String curClass = curEl.getClassName();
		if (curClass.contains(Type.Equation.toString())
				|| curClass.contains(Type.Term.toString())
				|| curClass.contains(Type.Sum.toString())) {
			// Child of another Equation, Term or Sum is done with parent
			if (!curClass.contains(Type.Equation.toString())) {
				String parentClass = curEl.getParentElement().getClassName();
				if (parentClass.contains(Type.Equation.toString())
						|| parentClass.contains(Type.Term.toString())
						|| parentClass.contains(Type.Sum.toString())) {
					return;
				}
			}
			LinkedList<Element> childrenInline = new LinkedList<Element>();
			LinkedList<Element> parentsInline = new LinkedList<Element>();
			LinkedList<Element> fractionsInline = new LinkedList<Element>();
			LinkedList<Element> fracContainerSibs = new LinkedList<Element>();
			LinkedList<Element> fracContainers = new LinkedList<Element>();
			parentsInline.add(curEl);

			addChildrenIfInline(curEl, childrenInline, parentsInline);

			// NodeList<Node> children = curEl.getChildNodes();

			int tallestNumerator = 0;
			int tallestDenominator = 0;
			int liftCenter = 999999999;// shortest child
			for (Element child : childrenInline) {
				// Find the tallest denominator to match centers
				if (child.getClassName().contains(Type.Fraction.toString())) {
					fractionsInline.add(child);
					int numeratorHeight = ((Element) child.getChild(0))
							.getOffsetHeight();
					int denominatorHeight = ((Element) child.getChild(1))
							.getOffsetHeight();
					if (numeratorHeight > tallestNumerator) {
						tallestNumerator = numeratorHeight;
					}
					if (denominatorHeight > tallestDenominator) {
						tallestDenominator = denominatorHeight;
					}
				} else {
					// Find the shortest non-fraction to center lift
					int childHeight = child.getOffsetHeight();
					if (childHeight < liftCenter) {
						liftCenter = childHeight;
					}
				}
			}

			if (fractionsInline.size() != 0) {
				// Set parent heights to match fractions
				for (Element parent : parentsInline) {
					if (!parent.getClassName().contains("Equation"))
						parent.getStyle().setHeight(
								(tallestDenominator + tallestNumerator),
								Unit.PX);
				}
				// Find fraction containers and siblings
				for (Element fraction : fractionsInline) {
					analizeFractionRelationships(fraction, curEl,
							fracContainers, fracContainerSibs);
				}
				// Lift every child to the center of the tallest denominator
				for (Element child : childrenInline) {
					int lift = tallestDenominator;
					if (child.getClassName().contains(Type.Fraction.toString())) {
						lift -= ((Element) child.getChild(1)).getOffsetHeight();
					} else if (fracContainerSibs.contains(child)) {
						if (!fracContainers.contains(child)) {
							lift -= (liftCenter / 2);
							child.getStyle().clearHeight();
						}
					}
					if (lift != tallestDenominator) {// if changed
						// Lift Text to center of tallest denominator
						child.getStyle().setBottom(lift, Unit.PX);
						// Match bottoms of all inline siblings with padding
						child.getStyle().setPaddingBottom(lift, Unit.PX);
					}
				}
			}

			// Find highest top to match heights to
			int highestTop = 999999999;
			for (Element child : childrenInline) {
				int childTop = child.getAbsoluteTop();
				if (childTop < highestTop) {
					highestTop = childTop;
				}
			}

			// Match tops of all inline siblings with padding
			for (Element child : childrenInline) {
				int childTopPad = child.getAbsoluteTop() - highestTop;
				child.getStyle().setPaddingTop(childTopPad, Unit.PX);

			}
		}
	}

	private double toEm(int px) {
		System.out.println("px: \t" + px);
		System.out.println("fontSize/100: \t" + fontPercent / 100);
		System.out.println("em: " + px / 16 * fontPercent / 100);
		System.out.println(" ");

		if (fontPercent != 0) {

		}
		return px / 16 * fontPercent / 100;
	}

	private void addChildrenIfInline(Element curEl,
			LinkedList<Element> childrenInline,
			LinkedList<Element> parentsInline) {
		NodeList<Node> children = curEl.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Element child = (Element) children.getItem(i);

			String childClass = child.getClassName();
			if (childClass.contains(Type.Term.toString())
					|| childClass.contains(Type.Sum.toString())) {
				addChildrenIfInline(child, childrenInline, parentsInline);
				parentsInline.add(child);
				childrenInline.add(child);
			} else {
				childrenInline.add(child);
			}
		}
	}

	private void analizeFractionRelationships(Element fraction, Element curEl,
			LinkedList<Element> fracContainers,
			LinkedList<Element> fracContainerSibs) {
		Element fracContain = fraction;
		while (!curEl.equals(fracContain)) {
			fracContainers.add(fracContain);
			NodeList<Node> fracContSibs = fracContain.getParentElement()
					.getChildNodes();
			for (int i = 0; i < fracContSibs.getLength(); i++) {
				Element fracContSib = (Element) fracContSibs.getItem(i);
				if (!fracContain.equals(fracContSib)) {
					fracContainerSibs.add(fracContSib);
				}
			}
			fracContain = fracContain.getParentElement();
		}
	}
}
