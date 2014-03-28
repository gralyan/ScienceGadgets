package com.sciencegadgets.client.algebra;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitHTML;

public class EquationHTML extends HTML {

	private static final String FENCED = CSS.FENCED;

	EquationTree mTree;
	public boolean autoFillParent = false;
	private boolean hasSmallUnits = true;
	private boolean hasSubscripts = true;
	public boolean pilot = false;
	private Element left = null;
	private Element right = null;
	HashMap<Element, EquationNode> displayMap = new HashMap<Element, EquationNode>();

	public EquationHTML(EquationTree mTree) {
		this(mTree, true, true);
	}

	public EquationHTML(EquationTree mTree, boolean hasSmallUnits, boolean hasSubscripts) {
		this.mTree = mTree;
		this.setStyleName(CSS.EQUATION);
		this.hasSmallUnits = hasSmallUnits;
		this.hasSubscripts = hasSubscripts;

		left = makeHTMLNode(mTree.getLeftSide(), this.getElement());
		makeHTMLNode(mTree.getEquals(), this.getElement());
		right = makeHTMLNode(mTree.getRightSide(), this.getElement());

	}

	private EquationHTML(String html) {
		super(html);
		this.setStyleName(CSS.EQUATION);
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
	private Element makeHTMLNode(EquationNode mNode, Element displayParentEl) {
		EquationNode mParent = mNode.getParent();

		String id = mNode.getId();
		TypeEquationXML type = mNode.getType();
		TypeEquationXML parentType = mParent.getType();
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
			boolean isFirstChild = mNode.equals(mParent.getFirstChild());
			isSecondChild = mNode.equals(mParent.getChildAt(1));
			if (isFirstChild) {
				container.addClassName(parentType.asChild(true));
			} else if (isSecondChild) {
				container.addClassName(parentType.asChild(false));
			} else {
				JSNICalls.error("Wrong children for a " + parentType + " "
						+ mNode.getParent());
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
			if (TypeEquationXML.Sum.equals(type)) {
				nodeHtml = fence(nodeHtml, container);
			}
			break;
		case Exponential:// All but Variables and unitless Numbers
			if (!TypeEquationXML.Variable.equals(type)//
					&& !(TypeEquationXML.Number.equals(type) && "".equals(mNode
							.getAttribute(MathAttribute.Unit)))) {
				nodeHtml = fence(nodeHtml, container);
			}
			break;

		}

		switch (type) {
		case Log:
			functionName = "log";
			Element base = DOM.createDiv();
			base.addClassName(TypeEquationXML.Log.asLogBase());
			base.setInnerText(mNode.getAttribute(MathAttribute.LogBase));
			nodeHtml.insertFirst(base);

			// fall through
		case Trig:
			if (functionName == null) {
				functionName = mNode.getAttribute(MathAttribute.Function);
			}
			Element funcNameDisplay = DOM.createDiv();
			funcNameDisplay.setInnerText(functionName);
			funcNameDisplay.addClassName(CSS.FUNCTION_NAME);
			nodeHtml.insertFirst(funcNameDisplay);
			break;
		case Fraction:
			// if (!(TypeML.Exponential.equals(parentType) && mNode.getIndex()
			// == 1)) {
			// container.getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
			// }
		}

		// Addition to tree
		displayParentEl.appendChild(container);

		Element unit = null;
		switch (type) {
		case Sum:
		case Term:
		case Fraction:
		case Exponential:
		case Log:
		case Trig:
			// Recursive creation
			for (EquationNode child : mNode.getChildren()) {
				makeHTMLNode(child, nodeHtml);
			}
			break;
		case Number:
			UnitAttribute unitName = mNode.getUnitAttribute();
			if (!"".equals(unitName)) {
				unit = UnitHTML.create(unitName, id,
						hasSmallUnits);
			}
			// falls through
		case Variable:
			String text = mNode.getXMLNode().getInnerText();
			if (hasSubscripts && text.length() > 1) {
				if (text.startsWith(Operator.MINUS.getSign())) {
					nodeHtml = fence(nodeHtml, container);
				}
				try {
					new BigDecimal(text);
					nodeHtml.setInnerText(text);
				} catch (NumberFormatException e) {
					// non-numbers, characters after the first are subscripts
					//note - constants are number nodes with character text
					nodeHtml.setInnerText(text.substring(0, 1));
					Element subscript = DOM.createDiv();
					subscript.addClassName(CSS.SUBSCRIPT);
					subscript.setInnerText(text.substring(1));
					nodeHtml.appendChild(subscript);
				}
			} else {
				nodeHtml.setInnerText(text);
			}
			break;
		case Operation:
			String txt = mNode.getSymbol();
			if (txt.startsWith("&")) { // must insert as js code
				for (TypeEquationXML.Operator op : TypeEquationXML.Operator.values()) {
					if (op.getHTML().equals(txt)) {
						txt = op.getSign();
					}
				}
			}
			nodeHtml.setInnerText(txt);
			break;
		}

		if (unit != null) {
			nodeHtml.appendChild(unit);
		}

		container.addClassName(type.toString());
		displayMap.put(nodeHtml, mNode);
		return nodeHtml;
	}

	private Element fence(Element nodeHtml, Element container) {
		String containerClass = container.getClassName();
		if (!(containerClass.contains(TypeEquationXML.Trig.asChild()) || containerClass
				.contains(TypeEquationXML.Log.asChild()))) {

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
		// *90 for looser fit, *100 for percent
		double fontPercent = smallerRatio * 90;

		this.getElement().getStyle().setFontSize((fontPercent), Unit.PCT);
	}

	/**
	 * Matches the heights of all the children of an {@link TypeEquationXML#Equation},
	 * {@link TypeEquationXML#Term} or {@link TypeEquationXML#Sum} by:<br/>
	 * 1.Lifting centers to the tallest denominator using padding-bottom<br/>
	 * 2.Matching tops to tallest height with padding-top<br/>
	 * <b>Note:</b> All children of these nodes are initially aligned at their
	 * baseline
	 */
	private void matchHeightsAndAlign(Element curEl) {

		EquationNode curNode = displayMap.get(curEl);

		TypeEquationXML curType = null;
		if (curNode != null) {
			curType = curNode.getType();
		}
		if (curEl.getChildCount() > 0) {
			for (int i = 0; i < curEl.getChildCount(); i++) {
				if (Node.ELEMENT_NODE == curEl.getChild(i).getNodeType()) {
					matchHeightsAndAlign((Element) curEl.getChild(i));
				}
			}
		}

		if (!(TypeEquationXML.Equation.equals(curType) || TypeEquationXML.Term.equals(curType)
				|| TypeEquationXML.Sum.equals(curType) || TypeEquationXML.Exponential
					.equals(curType))) {
			return;
		}

		LinkedList<Element> childrenHorizontal = new LinkedList<Element>();
		LinkedList<Element> fractionChildrenHorizontal = new LinkedList<Element>();

		addChildrenIfInline(curEl, childrenHorizontal, curType);

		double pxPerEm = getPxPerEm(curEl);

		// Fractions must be centered, find the tallest numerator or denominator
		// to match using padding to allow centering
		int tallestFracChild = 0;
		for (Element child : childrenHorizontal) {
			// Find the tallest denominator to match centers
			if (child.getClassName().contains(TypeEquationXML.Fraction.toString())) {
				fractionChildrenHorizontal.add(child);
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
		for (Element fractionChild : fractionChildrenHorizontal) {

			int numHeight = ((Element) fractionChild.getChild(0))
					.getClientHeight();
			int denHeight = ((Element) fractionChild.getChild(1))
					.getClientHeight();

			Style s = fractionChild.getStyle();
			s.setPaddingTop((tallestFracChild - numHeight) / pxPerEm, Unit.EM);
			s.setPaddingBottom((tallestFracChild - denHeight) / pxPerEm,
					Unit.EM);
			// s.setBottom((tallestFracChild - denHeight) / pxPerEm,
			// Unit.EM);
		}

		// Find highest top and lowest bottom to match heights
		// int lowestBottom = 0;
		// int highestTop = 999999999;
		// for (Element child : childrenHorizontal) {
		// int childTop = child.getAbsoluteTop();
		// if (childTop < highestTop) {
		// highestTop = childTop;
		// }
		// int childBottom = child.getAbsoluteBottom();
		// if (childBottom > lowestBottom) {
		// lowestBottom = childBottom;
		// }
		// }
		//
		// highestTop = curEl.getAbsoluteTop();
		// lowestBottom = curEl.getAbsoluteBottom();

		// Lift exponents of fraction bases to top
		if (TypeEquationXML.Exponential.equals(curType)) {
			Element base = ((Element) curEl.getChild(0));
			Element exp = ((Element) curEl.getChild(1));
			int lift = (exp.getOffsetTop() - base.getOffsetTop());
			exp.getStyle().setBottom(lift / pxPerEm, Unit.EM);

			// Align inline siblings flush using padding at highest and lowest
		} else {
			// for (Element child : childrenHorizontal) {
			// Style s = child.getStyle();
			//
			// // Fractions with some padding don't need to be aligned
			// if (fractionChildrenHorizontal.contains(child)) {
			// if (!"0em".equals(s.getPaddingTop())
			// || !"0em".equals(s.getPaddingBottom())) {
			// continue;
			// }
			// }else
			// if(child.getClassName().contains(TypeML.Operation.toString())) {
			// continue;
			// }
			//
			// int childTopPad = child.getAbsoluteTop() - highestTop;
			// int childBottomPad = lowestBottom - child.getAbsoluteBottom();
			//
			// s.setPaddingTop(childTopPad / pxPerEm, Unit.EM);
			// s.setPaddingBottom(childBottomPad / pxPerEm, Unit.EM);
			// }
		}

	}

	public static double getPxPerEm(Element element) {
		Element dummy = DOM.createDiv();
		element.appendChild(dummy);
		dummy.getStyle().setHeight(1000, Unit.EM);
		double pxHeight = dummy.getOffsetHeight();
		double pxPerEm = pxHeight / ((double) 1000);
		dummy.removeFromParent();
		return pxPerEm;
	}

	private void addChildrenIfInline(Element curEl,
			LinkedList<Element> childrenInline, TypeEquationXML curType) {
		switch (curType) {
		case Exponential:
			// Only the base of an exponent is considered inline
			childrenInline.add((Element) curEl.getChild(0));
			break;
		case Fraction:
			break;
		case Equation:
		case Sum:
		case Term:
		case Trig:
		case Log:
			NodeList<Node> children = curEl.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				childrenInline.add((Element) children.getItem(i));
			}
			break;
		}
	}

}
