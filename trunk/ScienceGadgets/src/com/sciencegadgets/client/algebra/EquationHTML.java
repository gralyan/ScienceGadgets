package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.UnitUtil;
import com.sciencegadgets.shared.TypeML.Operator;

public class EquationHTML extends HTML {

	private static final String FENCED = "fenced";

	Element mlTree;
	public boolean autoFillParent = false;
	private boolean hasSmallUnits = true;
	public boolean pilot = false;
	private Element left = null;
	private Element right = null;

	public EquationHTML(Element mlTree) {
		this(mlTree, true);
	}

	public EquationHTML(Element mlTree, boolean hasSmallUnits) {
		this.mlTree = mlTree;
		this.setStyleName("Equation");
		this.hasSmallUnits = hasSmallUnits;

		NodeList<Node> children = mlTree.getChildNodes();
		// for (int i = 0; i < children.getLength(); i++) {
		// }
		left = makeHTMLNode((Element) children.getItem(0), this.getElement());
		makeHTMLNode((Element) children.getItem(1), this.getElement());
		right = makeHTMLNode((Element) children.getItem(2), this.getElement());

	}

	private EquationHTML(String html) {
		super(html);
		this.setStyleName("Equation");
	}

	public Element getLeft() {
		return left;
	}

	public Element getRight() {
		return right;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		if (pilot) {
			matchHeightsAndAlign(this.getElement());
		}
		if (autoFillParent) {
			resizeEquation();
		}
	}

	public EquationHTML clone() {
		return new EquationHTML(this.getHTML());
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
	private Element makeHTMLNode(Element mlNode, Element displayParentEl) {
		Element mlParent = mlNode.getParentElement();

		String id = mlNode.getAttribute("id");
		TypeML type = TypeML.getType(mlNode.getTagName());
		TypeML parentType = TypeML.getType(mlParent.getTagName());
		boolean isSecondChild = false;

		// make new display node with appropriate properties
		Element container = DOM.createDiv();
		Element nodeHtml = container;
		container.setId(id);

		String functionName = null;

		// Add class names based on parents
		switch (parentType) {
		case Fraction:
		case Exponential:
			boolean isFirstChild = mlNode.equals(mlParent
					.getFirstChildElement());
			isSecondChild = mlNode.equals(mlParent.getFirstChildElement()
					.getNextSiblingElement());
			if (isFirstChild) {
				container.addClassName(parentType.asChild(true));
			} else if (isSecondChild) {
				container.addClassName(parentType.asChild(false));
			} else {
				JSNICalls.error("Wrong children for a " + parentType + " "
						+ mlNode.getParentElement().getString());
			}
			break;
		case Log:
		case Trig:
		case Equation:
		case Term:
		case Sum:
			container.addClassName(parentType.asChild());
			break;
		}

		// Add parentheses (fence) to certain elements
		switch (parentType) {
		case Term:// Sums in Terms
			if (TypeML.Sum.equals(type)) {
				nodeHtml = fence(nodeHtml, container);
			}
			break;
		case Exponential:// All but Variables and unitless Numbers
			if (!TypeML.Variable.equals(type)//
					&& !(TypeML.Number.equals(type) && "".equals(mlNode
							.getAttribute(MathAttribute.Unit.getName())))) {
				nodeHtml = fence(nodeHtml, container);
			}
			break;

		}

		switch (type) {
		case Log:
			functionName = "log";
			Element base = DOM.createDiv();
			base.addClassName(TypeML.Log.asLogBase());
			base.setInnerText(mlNode.getAttribute(MathAttribute.LogBase
					.getName()));
			nodeHtml.insertFirst(base);

			// fall through
		case Trig:
			Element funcName = DOM.createDiv();
			if (functionName == null) {
				functionName = mlNode.getAttribute(MathAttribute.Function
						.getName());
			}
			funcName.setInnerText(functionName);
			funcName.addClassName("functionName");
			nodeHtml.insertFirst(funcName);
			// fall through
			break;
		case Fraction:
			container.getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		}

		// Addition to tree
		displayParentEl.appendChild(container);

		for (int i = 0; i < mlNode.getChildCount(); i++) {
			Node child = mlNode.getChild(i);

			// Recursive creation
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				makeHTMLNode((Element) child, nodeHtml);
				// Inner text operation adjustment
			} else if (child.getNodeType() == Node.TEXT_NODE) {
				String text = mlNode.getInnerText();
				Element unit = null;
				switch (type) {
				case Number:
					String unitName = mlNode.getAttribute(MathAttribute.Unit
							.getName());
					if (!"".equals(unitName)) {
						unit = UnitUtil.element_From_attribute(unitName, id,
								hasSmallUnits);
					}
					// falls through
				case Variable:
					if (text.startsWith(Operator.MINUS.getSign())) {
						// All negative numbers in parentheses
						// text = "(" + text + ")";
						// nodeHTML.addClassName(FENCED);
						nodeHtml = fence(nodeHtml, container);
					}
					break;
				case Operation:
					if (text.startsWith("&")) { // must insert as js code
						for (TypeML.Operator op : TypeML.Operator.values()) {
							if (op.getHTML().equals(text)) {
								text = op.getSign();
							}
						}
					}
					break;
				}
				nodeHtml.setInnerText(text);
				if (unit != null) {
					nodeHtml.appendChild(unit);
				}
			}
		}
		nodeHtml.addClassName(type.toString());
		return nodeHtml;
	}

	private Element fence(Element nodeHtml, Element container) {
		String containerClass = container.getClassName();
		if (!(containerClass.contains(TypeML.Trig.asChild()) || containerClass
				.contains(TypeML.Log.asChild()))) {

			nodeHtml = container.appendChild(DOM.createDiv());
			nodeHtml.addClassName(FENCED);
		}
		return nodeHtml;
	}

	/**
	 * Resizes the equation to fill the panel
	 */
	private void resizeEquation() {
		double widthRatio = (double) this.getParent().getOffsetWidth()
				/ this.getOffsetWidth();
		double heightRatio = (double) this.getParent().getOffsetHeight()
				/ this.getOffsetHeight();

		double smallerRatio = (widthRatio > heightRatio) ? heightRatio
				: widthRatio;
		// *95 for looser fit, *100 for percent
		double fontPercent = smallerRatio * 95;
		
		this.getElement().getStyle().setFontSize((fontPercent), Unit.PCT);
	}

	/**
	 * Matches the heights of all the children of an {@link TypeML#Equation},
	 * {@link TypeML#Term} or {@link TypeML#Sum} by:<br/>
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
		// and partially Exponentials
		String curClass = curEl.getClassName();
		if (!(curClass.contains(TypeML.Equation.toString())
				|| curClass.contains(TypeML.Term.toString())
				|| curClass.contains(TypeML.Sum.toString()) || curClass
					.contains(TypeML.Exponential.toString()))) {
			return;
		}

		LinkedList<Element> childrenInline = new LinkedList<Element>();
		LinkedList<Element> fractionsInline = new LinkedList<Element>();

		addChildrenIfInline(curEl, childrenInline);

		double pxPerEm = getPxPerEm(curEl);

		// Fractions must be centered, find the tallest numerator or denominator
		// to match using padding to allow centering
		int tallestFracChild = 0;
		for (Element child : childrenInline) {
			// Find the tallest denominator to match centers
			if (child.getClassName().contains(TypeML.Fraction.toString())) {
				fractionsInline.add(child);
				for (int i = 0; i < 2; i++) {
					int fracChildHeight = ((Element) child.getChild(i))
							.getClientHeight();
					if (fracChildHeight > tallestFracChild) {
						tallestFracChild = fracChildHeight;
					}
				}
			}
		}
		// Match fraction horizontal lines inline
		for (Element fractionChild : fractionsInline) {

			int numHeight = ((Element) fractionChild.getChild(0))
					.getClientHeight();
			int denHeight = ((Element) fractionChild.getChild(1))
					.getClientHeight();

			Style s = fractionChild.getStyle();
			s.setPaddingTop((tallestFracChild - numHeight) / pxPerEm, Unit.EM);
			s.setPaddingBottom((tallestFracChild - denHeight) / pxPerEm,
					Unit.EM);
		}

		// Find highest top and lowest bottom to match heights
		int lowestBottom = 0;
		int highestTop = 999999999;
		for (Element child : childrenInline) {
			int childTop = child.getAbsoluteTop();
			if (childTop < highestTop) {
				highestTop = childTop;
			}
			int childBottom = child.getAbsoluteBottom();
			if (childBottom > lowestBottom) {
				lowestBottom = childBottom;
			}
		}

		// Lift exponents of fraction bases to top
		if (curClass.contains(TypeML.Exponential.toString())) {
			Element base = ((Element) curEl.getChild(0));
			Element exp = ((Element) curEl.getChild(1));
			int lift = (exp.getOffsetTop() - base.getOffsetTop());
			exp.getStyle().setBottom(lift / pxPerEm, Unit.EM);

			// Align inline siblings flush using padding at highest and lowest
		} else {
			for (Element child : childrenInline) {
				Style s = child.getStyle();

				// Fractions with some padding don't need to be aligned
				if (fractionsInline.contains(child)) {
					if (!"0em".equals(s.getPaddingTop())
							|| !"0em".equals(s.getPaddingBottom())) {
						continue;
					}
				}

				int childTopPad = child.getAbsoluteTop() - highestTop;
				int childBottomPad = lowestBottom - child.getAbsoluteBottom();

				s.setPaddingTop(childTopPad / pxPerEm, Unit.EM);
				s.setPaddingBottom(childBottomPad / pxPerEm, Unit.EM);
			}
		}

	}

	private double getPxPerEm(Element element) {
		Element dummy = DOM.createDiv();
		element.appendChild(dummy);
		dummy.getStyle().setHeight(1000, Unit.EM);
		double pxHeight = dummy.getOffsetHeight();
		double pxPerEm = pxHeight / ((double) 1000);
		dummy.removeFromParent();
		return pxPerEm;
	}

	private void addChildrenIfInline(Element curEl,
			LinkedList<Element> childrenInline) {
		if (curEl.getClassName().contains(TypeML.Exponential.toString())) {
			// Only the base of an exponent is considered inline
			childrenInline.add((Element) curEl.getChild(0));

		} else {
			NodeList<Node> children = curEl.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				childrenInline.add((Element) children.getItem(i));
			}
		}
	}

	// private void analizeFractionRelationships(Element fraction, Element
	// curEl,
	// LinkedList<Element> fracContainers,
	// LinkedList<Element> fracContainerSibs) {
	//
	// Element fracContain = fraction;
	// while (!curEl.equals(fracContain)) {
	// fracContainers.add(fracContain);
	// NodeList<Node> fracContSibs = fracContain.getParentElement()
	// .getChildNodes();
	// for (int i = 0; i < fracContSibs.getLength(); i++) {
	// Element fracContSib = (Element) fracContSibs.getItem(i);
	// if (!fracContain.equals(fracContSib)) {
	// fracContainerSibs.add(fracContSib);
	// }
	// }
	// fracContain = fracContain.getParentElement();
	// }
	// }
	//
	// private void findTerminalChildren(Element el, LinkedList<Element>
	// terminals) {
	// NodeList<Node> children = el.getChildNodes();
	// for (int i = 0; i < children.getLength(); i++) {
	// Node child = children.getItem(i);
	// if (Node.ELEMENT_NODE == child.getNodeType()) {
	// if (!((Element) child).getClassName().contains(
	// UnitUtil.UNIT_CLASSNAME)) {
	// findTerminalChildren((Element) child, terminals);
	// }
	// } else {
	// terminals.add(el);
	// }
	// }
	// }

}
