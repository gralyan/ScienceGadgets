package com.sciencegadgets.client.algebra;

import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.TypeArgument;
import com.sciencegadgets.shared.UnitUtil;
import com.sciencegadgets.shared.TypeML.Operator;

public class EquationHTML extends HTML {

	private static final String FENCED = "fenced";

	Element mlTree;
	public boolean autoFillParent = false;
	private boolean hasSmallUnits = true;
	public boolean pilot = false;

	public EquationHTML(Element mlTree) {
		this(mlTree, true);
	}

	public EquationHTML(Element mlTree, boolean hasSmallUnits) {
		this.mlTree = mlTree;
		this.setStyleName("Equation");
		this.hasSmallUnits = hasSmallUnits;

		NodeList<Node> children = mlTree.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			makeHTMLNode((Element) children.getItem(i), this.getElement());
		}

	}

	private EquationHTML(String html) {
		super(html);
		this.setStyleName("Equation");
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
	private void makeHTMLNode(Element mlNode, Element displayParentEl) {
		Element mlParent = mlNode.getParentElement();

		String id = mlNode.getAttribute("id");
		TypeML type = TypeML.getType(mlNode.getTagName());
		TypeML parentType = TypeML.getType(mlParent.getTagName());

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
				nodeHTML.addClassName(TypeML.Fraction
						.asChild(TypeArgument.NUMERATOR));
			} else if (isDenominator) {
				nodeHTML.addClassName(TypeML.Fraction
						.asChild(TypeArgument.DENOMINATOR));
			}
			break;
		case Exponential:
			boolean isBase = mlNode.equals(mlParent.getFirstChildElement());
			boolean isExponential = mlNode.equals(mlParent
					.getFirstChildElement().getNextSiblingElement());
			if (isBase) {
				nodeHTML.addClassName(TypeML.Exponential
						.asChild(TypeArgument.BASE));
			} else if (isExponential) {
				nodeHTML.addClassName(TypeML.Exponential
						.asChild(TypeArgument.EXPONENT));
			}
			break;
		case Term:
		case Equation:
		case Sum:
			nodeHTML.addClassName(parentType.asChild());
			break;
		}

		// (fence) sums in terms
		if ((TypeML.Sum.equals(type) && TypeML.Term.equals(parentType))) {
			// fenced.add(nodeHTML);
			nodeHTML.addClassName(FENCED);
		}
		// (fence) terms, sums, exponentials, fractions or numbers with units
		// in bases or exponents
		if ((TypeML.Exponential.equals(parentType)//
				&& !TypeML.Variable.equals(type))//
				&& !(TypeML.Number.equals(type) && "".equals(mlNode
						.getAttribute(MathAttribute.Unit.getName())))) {
			// fenced.add(nodeHTML);
			nodeHTML.addClassName(FENCED);
		}

		// Addition to tree
		displayParentEl.appendChild(nodeHTML);

		for (int i = 0; i < mlNode.getChildCount(); i++) {
			Node child = mlNode.getChild(i);

			// Recursive creation
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				makeHTMLNode((Element) child, nodeHTML);
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
						nodeHTML.addClassName(FENCED);
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
				nodeHTML.setInnerText(text);
				if (unit != null) {
					nodeHTML.appendChild(unit);
				}
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
		double fontPercent = smallerRatio * 95;// *95 for looser fit, *100 for
												// percent
		this.getElement().getStyle().setFontSize((fontPercent), Unit.PCT);
	}

	/**
	 * Matches the heights of all the children of an {@link TypeML.Equation},
	 * {@link TypeML.Term} or {@link TypeML.Sum} by:<br/>
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
			if (base.getClassName().contains(TypeML.Fraction.toString())) {
				Element exp = ((Element) curEl.getChild(1));
				int lift = (base.getOffsetHeight() / 2) - exp.getOffsetHeight();
				exp.getStyle().setBottom(lift / pxPerEm, Unit.EM);
			}

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
//				s.setPaddingTop(childTopPad , Unit.PX);
//				s.setPaddingBottom(childBottomPad , Unit.PX);
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
