package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.TypeArgument;
import com.sciencegadgets.shared.UnitUtil;
import com.sciencegadgets.shared.TypeML.Operator;

public class EquationHTML extends HTML {

	Element mlTree;
	private double fontPercent = 0;
	public boolean autoFillParent = false;
	private LinkedList<Element> fenced = new LinkedList<Element>();
	public final String Aesthetic = "Aesthetic";
	private boolean hasSmallUnits = true;

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
		addParenthesis();
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		// revealUnits();
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
	private void makeHTMLNode(Element mlNode, Element displayParentEl) {
		Element mlParent = mlNode.getParentElement();

		String id = mlNode.getAttribute("id");
		TypeML type = TypeML.getType(mlNode.getTagName());
		TypeML parentType = TypeML.getType(mlParent.getTagName());

		// make new display node with appropriate properties
		Element nodeHTML = DOM.createDiv();
		nodeHTML.setId(id);
		nodeHTML.addClassName(type.toString());

		boolean isRadical = false;

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
				if ("mfrac".equalsIgnoreCase(mlNode.getTagName())
						&& "1".equals(mlNode.getFirstChildElement()
								.getInnerText())) {
					Element radical = DOM.createDiv();
					radical.addClassName("radical");
					radical.addClassName(Aesthetic);
					displayParentEl.insertFirst(radical);
					displayParentEl.insertFirst(nodeHTML);
				}
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
			fenced.add(nodeHTML);
		}
		// (fence) terms, sums, exponentials, fractions or numbers with units
		// in bases or exponents
		if ((TypeML.Exponential.equals(parentType) && //
				!TypeML.Variable.equals(type))//
				&& !(TypeML.Number.equals(type) && //
				"".equals(mlNode.getAttribute(MathAttribute.Unit.getName())))) {
			fenced.add(nodeHTML);
		}

		// Addition to tree
		if (!isRadical) {
			displayParentEl.appendChild(nodeHTML);
		}

		for (int i = 0; i < mlNode.getChildCount(); i++) {
			Node child = mlNode.getChild(i);

			// Recursive creation
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (!isRadical) {
					makeHTMLNode((Element) child, nodeHTML);
				} else {
					makeHTMLNode((Element) child.getChild(1), nodeHTML);
				}
				// Inner text operation adjustment
			} else if (child.getNodeType() == Node.TEXT_NODE) {
				String text = mlNode.getInnerText();
				Element unit = null;
				switch (type) {
				case Number:
					String unitName = mlNode.getAttribute(MathAttribute.Unit
							.getName());
					if (!"".equals(unitName)) {
						unit = UnitUtil.element_From_attribute(unitName, id, hasSmallUnits);
					}
				case Variable:
					if (text.startsWith(Operator.MINUS.getSign())) {
						// All negative numbers in parentheses
						text = "(" + text + ")";
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

	/** Surround marked sums in parentheses */
	private void addParenthesis() {

		for (Element toFence : fenced) {

			Element parOpen = DOM.createDiv();
			parOpen.addClassName(Aesthetic);
			parOpen.addClassName(TypeML.Sum.asChild());
			Element parClose = (Element) parOpen.cloneNode(true);
			parOpen.setInnerText("(");
			parClose.setInnerText(")");

			String fenceClass = toFence.getClassName();
			if (fenceClass != null) {
				if (fenceClass.contains(TypeML.Fraction.name())) {
					Element toFenceParent = toFence.getParentElement();
					toFenceParent.insertBefore(parOpen, toFence);
					toFenceParent.insertAfter(parClose, toFence);
				} else {
					toFence.insertFirst(parOpen);
					toFence.appendChild(parClose);
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

		fontPercent = smallerRatio * 95;// *.95 for looser fit, *100 for percent
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
		String curClass = curEl.getClassName();
		if (curClass.contains(TypeML.Equation.toString())
				|| curClass.contains(TypeML.Term.toString())
				|| curClass.contains(TypeML.Sum.toString())) {
			// Child of another Equation, Term or Sum is done with parent
			if (!curClass.contains(TypeML.Equation.toString())) {
				String parentClass = curEl.getParentElement().getClassName();
				if (parentClass.contains(TypeML.Equation.toString())
						|| parentClass.contains(TypeML.Term.toString())
						|| parentClass.contains(TypeML.Sum.toString())) {
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

			int tallestNumerator = 0;
			int tallestDenominator = 0;
			int liftCenter = 999999999;// shortest child
			for (Element child : childrenInline) {
				// Find the tallest denominator to match centers
				if (child.getClassName().contains(TypeML.Fraction.toString())) {
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

					if (child.getClassName().contains(
							TypeML.Fraction.toString())) {
						lift -= ((Element) child.getChild(1)).getOffsetHeight();
						if (lift != tallestDenominator) {// if changed
							// Lift frac to match horizontal lines
							child.getStyle().setBottom(lift, Unit.PX);
						}
					} else if (fracContainerSibs.contains(child)) {

						if (child.getClassName().contains(Aesthetic)) {
							// Don't raise or pad, stretch aesthetics
							int childHeight = child.getOffsetHeight();
							double ratio = child.getParentElement()
									.getOffsetHeight() / childHeight;

							//Stretch Aesthetics
							String[] transformCSStypes = { "transform",
									"WebkitTransform", "MozTransform",
									"MsTransform", "OTransform" };
							for (String t : transformCSStypes) {
								child.getStyle().setProperty(t,
										"scaleY(" + ratio + ")");
							}

							// Lift aesthetics
							if (!fracContainers.contains(child)) {
								lift -= (liftCenter / 2);
								child.getStyle().setBottom(lift, Unit.PX);
							}

						} else if (!fracContainers.contains(child)) {

							child.getStyle().clearHeight();

							lift -= (liftCenter / 2);
							// Lift Text to center of tallest denominator
							// Lift only parents, propagates down
							child.getStyle().setBottom(lift, Unit.PX);

							// Pad only terminal child nodes, propagates up
							LinkedList<Element> terminals = new LinkedList<Element>();
							findTerminalChildren(child, terminals);
							for (Element terminal : terminals) {
								// Match bottoms of inline terminals
								terminal.getStyle().setPaddingBottom(lift,
										Unit.PX);
							}
						}
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
				if (!child.getClassName().contains(Aesthetic)) {
					int childTopPad = child.getAbsoluteTop() - highestTop;
					child.getStyle().setPaddingTop(childTopPad, Unit.PX);
				}
			}
		}
	}

	// private double toEm(int px) {
	// System.out.println("px: \t" + px);
	// System.out.println("fontSize/100: \t" + fontPercent / 100);
	// System.out.println("em: " + px / 16 * fontPercent / 100);
	// System.out.println(" ");
	//
	// if (fontPercent != 0) {
	//
	// }
	// return px / 16 * fontPercent / 100;
	// }

	private void addChildrenIfInline(Element curEl,
			LinkedList<Element> childrenInline,
			LinkedList<Element> parentsInline) {
		
		NodeList<Node> children = curEl.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Element child = (Element) children.getItem(i);

			String childClass = child.getClassName();
			if (childClass.contains(TypeML.Term.toString())
					|| childClass.contains(TypeML.Sum.toString())|| childClass.contains(TypeML.Exponential.toString())) {
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

	private void findTerminalChildren(Element el, LinkedList<Element> terminals) {
		NodeList<Node> children = el.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.getItem(i);
			if (Node.ELEMENT_NODE == child.getNodeType()) {
				if (!((Element) child).getClassName().contains(
						UnitUtil.UNIT_CLASSNAME)) {
					findTerminalChildren((Element) child, terminals);
				}
			} else {
				terminals.add(el);
			}
		}
	}

}
