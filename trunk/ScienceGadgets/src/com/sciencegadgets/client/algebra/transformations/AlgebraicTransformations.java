package com.sciencegadgets.client.algebra.transformations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.client.SelectionPanel.Cell;
import com.sciencegadgets.client.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.AlgebaWrapper;
import com.sciencegadgets.client.algebra.WrapDragController;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.transformations.InterFractionDrop.DropType;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;
import com.sciencegadgets.shared.TypeML.TrigFunctions;

/**
 * This class contains the set of static methods used to perform algebraic
 * changes
 * 
 */
public class AlgebraicTransformations {

	public static Label response = new Label();

	/**
	 * Performs the operation specified by the node
	 * 
	 * @param opNode
	 *            - operation to perform
	 */
	public static TransformationList operation(MathNode opNode) {
		MathNode left, right = null;

		left = opNode.getPrevSibling();
		right = opNode.getNextSibling();

		if (left != null && left != null) {
			switch (opNode.getOperation()) {
			case CROSS:
			case DOT:
			case SPACE:
				return new MultiplyTransformations(left, opNode, right);
			case MINUS:
				return new AdditionTransformations(left, opNode, right, false);
			case PLUS:
				return new AdditionTransformations(left, opNode, right, true);
			}
		}
		return null;
	}

	/**
	 * Propagates negative into the node depending on its type<br/>
	 * <b>operation</b> - do nothing<br/>
	 * <b>number or variable</b> - change symbol<br/>
	 * <b>term</b> - propagate to first child<br/>
	 * <b>sum</b> - propagate to each child<br/>
	 * <b>fraction</b> - propagate to first child<br/>
	 * <b>everything else</b> - encase in term with the number negative one<br/>
	 * 
	 * @param toNegate
	 */
	public static void propagateNegative(MathNode toNegate) {

		switch (toNegate.getType()) {
		case Operation:
			break;
		case Number:
		case Variable:
			String symbol = toNegate.getSymbol();
			if (symbol.startsWith(Operator.MINUS.getSign())) {
				symbol = symbol.replaceFirst(Operator.MINUS.getSign(), "");
			} else {
				symbol = Operator.MINUS.getSign() + symbol;
			}
			toNegate.setSymbol(symbol);
			break;
		case Term:
			propagateNegative(toNegate.getFirstChild());
			break;
		case Sum:
			for (MathNode sumChild : toNegate.getChildren()) {
				propagateNegative(sumChild);
			}
			break;
		case Fraction:
			propagateNegative(toNegate.getFirstChild());
			break;
		default:
			MathNode casing = toNegate.encase(TypeML.Term);
			casing.addBefore(0, TypeML.Operation, Operator.getMultiply()
					.getSign());
			casing.addBefore(0, TypeML.Number, "-1");
			break;
		}
	}

	/**
	 * Reciprocates the given node and simplifies in one of two ways:<br/>
	 * 1) If the node is in a fraction or a term in a fraction, it is moved over
	 * to the other side of the fraction.<br/>
	 * 2) Encases the node in a fraction and adds the number one to the
	 * numerator
	 */
	public static void reciprocate(MathNode toReciprocate) {

		MathNode parent = toReciprocate.getParent();

		if (TypeML.Fraction.equals(parent.getType())) {

			boolean inNumerator = toReciprocate.getIndex() == 0;
			MathNode otherSide = inNumerator ? toReciprocate.getNextSibling()
					: toReciprocate.getPrevSibling();
			otherSide = otherSide.encase(TypeML.Term);

			otherSide
					.append(TypeML.Operation, Operator.getMultiply().getSign());
			otherSide.append(toReciprocate);

			if (inNumerator) {
				parent.addBefore(0, TypeML.Number, "1");
			} else {
				parent.replace(otherSide);
			}

		} else if (TypeML.Term.equals(parent.getType())
				&& TypeML.Fraction.equals(parent.getParentType())) {

			if (toReciprocate.getIndex() == 0) {
				toReciprocate.getNextSibling().remove();
			} else {
				toReciprocate.getPrevSibling().remove();
			}

			boolean inNumerator = parent.getIndex() == 0;
			MathNode otherSide = inNumerator ? parent.getNextSibling() : parent
					.getPrevSibling();

			otherSide = otherSide.encase(TypeML.Term);
			otherSide
					.append(TypeML.Operation, Operator.getMultiply().getSign());
			otherSide.append(toReciprocate);

			parent.decase();

		} else {
			MathNode frac = toReciprocate.encase(TypeML.Fraction);
			frac.addBefore(0, TypeML.Number, "1");
		}
	}

	public static LinkedList<TransformationButton> isolatedVariable_check(MathNode isolatedVar) {
		if (TypeML.Equation.equals(isolatedVar.getParentType())) {
			TransformationList list = new TransformationList();
			list.add(new EvaluatePromptButton(isolatedVar));
			list.add(new SubstituteButton(isolatedVar));
			return list;
		}
		return null;
	}

	/**
	 * Separates a negative number into the product of -1 and the absolute value
	 * of the number
	 * 
	 * @param negNode
	 *            - node of negative number
	 */
	public static TransformationButton separateNegative_check(MathNode negNode) {
		if (negNode.getSymbol().startsWith(TypeML.Operator.MINUS.getSign())
				&& !negNode.getSymbol().equals("-1")) {
			return new SeperateNegButton(negNode);
		}
		return null;
	}

	/**
	 * Place drop targets on drag controller, allowing for operations between
	 * terms of the numerator and denominator. This allows users to cancel,
	 * divide, and combine terms on either side of the fraction
	 * 
	 * @param node
	 */
	public static void interFractionDrop_check(MathNode node) {

		MathNode thisSide = null;
		MathNode parent = node.getParent();
		switch (parent.getType()) {
		case Fraction:
			thisSide = node;
			break;
		case Term:
			if (TypeML.Fraction.equals(parent.getParentType())) {
				thisSide = parent;
				break;
			}// else fall through
		default:
			return;
		}

		MathNode otherSide = thisSide.getIndex() == 0 ? thisSide
				.getNextSibling() : thisSide.getPrevSibling();

		HashMap<MathNode, DropType> dropTargets = new HashMap<MathTree.MathNode, DropType>();

		if (node.isLike(otherSide)) {// Cancel drop on entire other sides
			dropTargets.put(otherSide, DropType.CANCEL);

		} else if (TypeML.Term.equals(otherSide.getType())) {

			for (MathNode child : otherSide.getChildren()) {
				if (node.isLike(child)) {// Cancel drop on child
					dropTargets.put(child, DropType.CANCEL);
				} else {// Drop on child
					addDropTarget(child, node, dropTargets);
				}
			}

		} else {// Drop on entire other side
			addDropTarget(otherSide, node, dropTargets);
		}

		if (dropTargets.size() > 0) {
			WrapDragController dragController = node.getWrapper()
					.addDragController();
			for (Entry<MathNode, DropType> dropTarget : dropTargets.entrySet()) {

				dragController.registerDropController(new InterFractionDrop(
						(AlgebaWrapper) dropTarget.getKey().getWrapper(),
						dropTarget.getValue()));
			}
		}

	}

	/**
	 * To be used by {@link #interFractionDrop_check}
	 */
	private static void addDropTarget(MathNode target, MathNode drag,
			HashMap<MathTree.MathNode, DropType> dropTargets) {

		if (!drag.getType().equals(target.getType())) {
			return;
		}

		switch (target.getType()) {
		case Number:
			dropTargets.put(target, DropType.DIVIDE);
			break;
		case Exponential:
			if (drag.getFirstChild().isLike(target.getFirstChild())) {
				dropTargets.put(target, DropType.EXPONENTIAL);
			}
			break;
		case Log:
			if (drag.getAttribute(MathAttribute.LogBase).equals(
					target.getAttribute(MathAttribute.LogBase))
					&& TypeML.Number.equals(drag.getFirstChild().getType())) {
				dropTargets.put(target, DropType.LOG_COMBINE);
			}
			break;
		case Trig:
			if (target.getFirstChild().isLike(drag.getFirstChild())) {
				// Make sure (drag or target) is sin and other is cos
				TrigFunctions dragFunc = TrigFunctions.valueOf(drag
						.getAttribute(MathAttribute.Function));
				TrigFunctions targetfunc = TrigFunctions.valueOf(target
						.getAttribute(MathAttribute.Function));
				TrigFunctions sin = TrigFunctions.sin;
				TrigFunctions cos = TrigFunctions.cos;
				if ((sin.equals(dragFunc) && cos.equals(targetfunc))
						|| (cos.equals(dragFunc) && sin.equals(targetfunc))) {
					dropTargets.put(target, DropType.TRIG_COMBINE);
				}
			}
			break;
		}
	}

	/**
	 * List the factors of the number as buttons to choose in a prompt
	 * 
	 * @param node
	 */
	public static TransformationButton factorizeNumbers_check(MathNode node) {
		Integer number;
		try {
			number = Integer.parseInt(node.getSymbol());
		} catch (NumberFormatException e) {
			return null;
		}
		if (number == 1) {
			return null;
		}

		return new FactorNumberPromptButton(number, node);
	}

	/**
	 * Checks if there is a unit attribute
	 * 
	 * @param node
	 */
	public static TransformationButton unitConversion_check(MathNode node) {
		if (!"".equals(node.getUnitAttribute())) {
			return new UnitConversionButton(node);
		}
		return null;
	}

	public static TransformationButton denominatorFlip_check(MathNode node) {
		return new DenominatorFlipButton(node);
	}

	/**
	 * Check if: (log base = exponential base)<br/>
	 * log<sub>b</sub>(b<sup>x</sup>) = x
	 */
	public static TransformationButton unravelLogExp_check(MathNode log) {
		MathNode exponential = log.getFirstChild();
		if (TypeML.Exponential.equals(exponential.getType())) {
			MathNode exponentialBase = exponential.getFirstChild();
			if (TypeML.Number.equals(exponentialBase.getType())
					&& exponentialBase.getSymbol().equals(
							log.getAttribute(MathAttribute.LogBase))) {
				MathNode exponentialExp = exponential.getChildAt(1);
				return new UnravelButton(log, exponentialExp, Rule.LOGARITHM);
			}
		}
		return null;
	}

	/**
	 * Check if: (log base = exponential base)<br/>
	 * b<sup>log<sub>b</sub>(x)</sup> = x
	 */
	public static TransformationButton unravelExpLog_check(MathNode exponential) {
		MathNode log = exponential.getChildAt(1);
		if (TypeML.Log.equals(log.getType())) {
			String logBase = log.getAttribute(MathAttribute.LogBase);
			MathNode exponentialBase = exponential.getFirstChild();
			if (TypeML.Number.equals(exponentialBase.getType())
					&& exponentialBase.getSymbol().equals(logBase)) {
				return new UnravelButton(exponential, log.getFirstChild(),
						Rule.LOGARITHM);

			}
		}
		return null;
	}

	/**
	 * Check if function within its inverse function or function of its inverse<br/>
	 * sin(arcsin(x)) = x<br/>
	 * arcsin(sin(x)) = x<br/>
	 */
	public static TransformationButton inverseTrig_check(MathNode trig) {
		MathNode trigChild = trig.getFirstChild();
		if (TypeML.Trig.equals(trigChild.getType())) {
			String trigChildFunc = trigChild
					.getAttribute(MathAttribute.Function);
			String trigChildFuncInverse = TrigFunctions
					.getInverseName(trigChildFunc);
			String trigFunc = trig.getAttribute(MathAttribute.Function);
			if (trigFunc.equals(trigChildFuncInverse)) {
				return new UnravelButton(trig, trigChild.getFirstChild(),
						Rule.INVERSE_TRIGONOMETRIC_FUNCTIONS);
			}
		}
		return null;
	}

}

class VariableEvaluateSpec extends FlowPanel {
	protected final DoubleBox valueInput = new DoubleBox();
	protected UnitSelection unitSelect = null;;
	protected MathNode mathNode = null;
	protected Double value = null;
	protected String unit;

	VariableEvaluateSpec(MathNode mathNode) {
		this.mathNode = mathNode;
		unitSelect = new UnitSelection(mathNode.getUnitAttribute());
		Label symbol = new Label(mathNode.getSymbol() + " =");

		symbol.addStyleName(CSS.LAYOUT_ROW);
		valueInput.addStyleName(CSS.LAYOUT_ROW);
		unitSelect.addStyleName(CSS.LAYOUT_ROW);
		unitSelect.setWidth("50%");

		add(symbol);
		add(valueInput);
		add(unitSelect);

	}
}

// /////////////////////////////////////////////////////////////
// Transformation Buttons
// ////////////////////////////////////////////////////////////

/**
 * Decompose
 * 
 */
class FactorNumberPromptButton extends TransformationButton {
	public FactorNumberPromptButton(final Integer number, final MathNode node) {
		setHTML("Factor");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				LinkedHashSet<Integer> primeFactors = findPrimeFactors(number);

				Prompt prompt = new Prompt(false);
				Label title = new Label();
				title.setText("" + number);
				title.setHeight("20%");
				prompt.add(title);
				for (Integer factor : primeFactors) {
					prompt.add(new FactorNumberButton(factor, number / factor,
							node, prompt));
				}
				prompt.appear();
			}
		});

	}

	LinkedHashSet<Integer> findPrimeFactors(Integer number) {
		LinkedHashSet<Integer> factors = new LinkedHashSet<Integer>();

		if (number < 0) {
			number = Math.abs(number);
		}
		factors.add(1);

		int start = 2;
		byte inc = 1;
		if (number % 2 == 1) {// odd numbers can't have even factors
			start = 3;
			inc = 2;
		}
		for (int i = start; i <= Math.sqrt(number); i = i + inc) {
			if (number % i == 0) {
				factors.add(i);
			}
		}
		return factors;
	}

	class FactorNumberButton extends TransformationButton {
		FactorNumberButton(final int factor, final int cofactor,
				final MathNode node, final Prompt prompt) {

			setHTML(factor + " " + Operator.getMultiply().getSign() + " "
					+ cofactor);
			setSize("50%", "50%");

			this.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {

					String original = node.getSymbol();
					int factored = Integer.parseInt(original) / factor;

					node.highlight();

					MathNode parent = node.encase(TypeML.Term);
					int index = node.getIndex();
					parent.addBefore(index, TypeML.Operation, Operator
							.getMultiply().getSign());
					parent.addBefore(index, TypeML.Number, factor + "");

					node.setSymbol(factored + "");

					Moderator.reloadEquationPanel(original + " = " + factor
							+ " " + Operator.getMultiply().getSign() + " "
							+ factored, Rule.INTEGER_FACTORIZATION);

					prompt.disappear();
				}
			});
		}
	}
}

/**
 * -x = -1 &middot; x
 * 
 */
class SeperateNegButton extends TransformationButton {
	SeperateNegButton(final MathNode negNode) {

		setHTML("Seperate (-)");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MathNode prevSib = negNode.getPrevSibling();
				String newSymbol = negNode.getSymbol().replaceFirst(
						TypeML.Operator.MINUS.getSign(), "");
				negNode.setSymbol(newSymbol);

				if (prevSib != null
						&& TypeML.Operation.equals(prevSib.getType())) {
					if (Operator.PLUS.getSign().equals(prevSib.getSymbol())) {
						prevSib.setSymbol(Operator.MINUS.getSign());
					} else if (Operator.MINUS.getSign().equals(
							prevSib.getSymbol())) {
						prevSib.setSymbol(Operator.PLUS.getSign());
					} else {
						JSNICalls.error("The previous operator contains "
								+ "neither a plus or minus");
					}
				} else {
					MathNode parent = negNode.getParent();
					parent = negNode.encase(TypeML.Term);
					int nodeIndex = negNode.getIndex();
					parent.addBefore(nodeIndex, TypeML.Operation,
							TypeML.Operator.getMultiply().getSign());
					parent.addBefore(nodeIndex, TypeML.Number, "-1");
				}
				Moderator.reloadEquationPanel("-" + newSymbol + " = -1"
						+ Operator.getMultiply().getSign() + newSymbol, null);
			}
		});
	}
}

/**
 * x / (y/z) = x &middot; (z/y)<br/>
 * x / y = x &middot; (1/y)
 */
class DenominatorFlipButton extends TransformationButton {
	DenominatorFlipButton(final MathNode node) {

		setHTML("Flip Denominator");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				node.highlight();

				MathNode frac = node;
				if (!TypeML.Fraction.equals(node.getType())) {
					frac = node.encase(TypeML.Fraction);
					frac.append(TypeML.Number, "1");
				}
				// Flip
				frac.append(frac.getChildAt(0));

				MathNode parentFraction = frac.getParent();
				MathNode grandParent = parentFraction.getParent();
				int index = parentFraction.getIndex();

				if (!TypeML.Term.equals(grandParent.getType())) {
					grandParent = parentFraction.encase(TypeML.Term);
					index = 0;
				}

				grandParent.addBefore(index, parentFraction.getChildAt(1));
				grandParent.addBefore(index, TypeML.Operation, Operator
						.getMultiply().getSign());
				grandParent.addBefore(index, parentFraction.getChildAt(0));

				parentFraction.remove();

				Moderator.reloadEquationPanel("Multiply by Resiprocal",
						Rule.FRACTION_DIVISION);
			}
		});
	}
}

/**
 * Switches to unit conversion mode
 * 
 */
class UnitConversionButton extends TransformationButton {
	UnitConversionButton(final MathNode node) {

		setHTML("Convert Units");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Moderator.switchToConversion(node);
			}
		});
	}
}

/**
 * Allows for the evaluation of the other side by substituting numbers into the
 * appropriate variables
 * 
 */
class EvaluatePromptButton extends TransformationButton {
	final static HashSet<VariableEvaluateSpec> varSpecs = new HashSet<VariableEvaluateSpec>();

	EvaluatePromptButton(final MathNode isolatedVar) {

		setHTML("Evaluate");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final Prompt prompt = new Prompt();

				varSpecs.clear();

				MathTree tree = isolatedVar.getTree();
				LinkedList<Wrapper> wrappers = tree.getWrappers();
				for (Wrapper wrap : wrappers) {
					MathNode node = wrap.getNode();
					if (TypeML.Variable.equals(node.getType())
							&& !node.equals(isolatedVar)) {
						VariableEvaluateSpec varSpec = new VariableEvaluateSpec(
								node);
						varSpecs.add(varSpec);
						prompt.add(varSpec);
					}
				}
				if (varSpecs.size() == 0) {
					Window.alert("There are no variables to evaluate");
					return;
				}

				prompt.addOkHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						// Make sure the entire spec is filled
						boolean completelyFilled = true;
						for (VariableEvaluateSpec varSpec : varSpecs) {
							Double value = varSpec.valueInput.getValue();
							if (value == null) {
								varSpec.valueInput.getElement().getStyle()
										.setBackgroundColor("red");
								completelyFilled = false;
							} else {
								varSpec.valueInput.getElement().getStyle()
										.clearBackgroundColor();
								varSpec.value = value;
							}
							String unit = varSpec.unitSelect.unitBox
									.getSelectedValue();
							if (unit == null) {
								varSpec.unitSelect.getElement().getStyle()
										.setColor("red");
								completelyFilled = false;
							} else {
								varSpec.unitSelect.getElement().getStyle()
										.clearColor();
								varSpec.unit = unit;
							}
						}

						// Execute
						if (completelyFilled) {
							prompt.disappear();
							for (VariableEvaluateSpec varSpec : varSpecs) {

								String value = String.valueOf(varSpec.value)
										.replaceAll((String) "\\.0$", "");
								String unit = varSpec.unit;

								MathNode quantityPlugIn = varSpec.mathNode
										.replace(TypeML.Number, value);
								quantityPlugIn.setAttribute(MathAttribute.Unit,
										unit);
							}
							Moderator.reloadEquationPanel(null, null);
						}

					}
				});

				prompt.appear();

			}
		});
	}
}

/**
 * Allows for substitution method with another equation into another variable of
 * the same quantity kind
 * 
 */
class SubstituteButton extends TransformationButton {
	SubstituteButton(final MathNode isolatedVar) {

		setHTML("Substitute");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String quantityKind = isolatedVar.getUnitAttribute();
				if (quantityKind == null || quantityKind.equals("")) {
					Window.alert("No similar other equations with similar variables available");
					return;
				}
				final DialogBox dialog = new DialogBox(true, true);
				SelectionPanel eqSelect = new SelectionPanel(CSS.EQUATION);
				dialog.add(eqSelect);
				DataModerator
						.fill_EquationsByQuantities(quantityKind, eqSelect);
				eqSelect.addSelectionHandler(new SelectionHandler() {
					@Override
					public void onSelect(Cell selected) {

						MathNode eqNode = isolatedVar.getParent();

						Element substitute = null;
						if (isolatedVar.isLeftSide()) {
							substitute = isolatedVar.getTree().getRightSide()
									.getXMLNode();
						} else if (isolatedVar.isRightSide()) {
							substitute = isolatedVar.getTree().getLeftSide()
									.getXMLNode();
						} else {
							JSNICalls
									.error("Could not find the element to substitute"
											+ " in: \n" + eqNode);
							dialog.removeFromParent();
							dialog.hide();
							return;
						}

						String subIntoEqStr = selected.getValue();
						Element subIntoEqEl = new HTML(subIntoEqStr)
								.getElement().getFirstChildElement();

						substitute(isolatedVar, subIntoEqEl, substitute);

						dialog.removeFromParent();
						dialog.hide();
					}
				});
				dialog.setGlassEnabled(true);
				dialog.setAnimationEnabled(true);
				dialog.center();
			}
		});
	}

	private void substitute(final MathNode isolatedVar, Element subIntoEqEl,
			Element substitute) {

		// Find the variable to substutute
		NodeList<Element> possibleSubs = subIntoEqEl
				.getElementsByTagName(TypeML.Variable.getTag());
		findSub: for (int j = 0; j < possibleSubs.getLength(); j++) {
			Element possibleSub = possibleSubs.getItem(j);
			if (!isolatedVar.getUnitAttribute().equals(
					possibleSub.getAttribute(MathAttribute.Unit.getAttributeName()))) {
				continue findSub;
			}
			Element subParent = possibleSub.getParentElement();
			String plugTag = substitute.getTagName();
			if (plugTag.equals(subParent.getTagName())
					&& (plugTag.equals(TypeML.Sum.getTag()) || plugTag
							.equals(TypeML.Term.getTag()))) {
				Node subPrev = possibleSub.getPreviousSibling();
				if (subPrev != null && subPrev.getNodeName().equals("mo")
						&& "-".equals(((Element) subPrev).getInnerText())) {
					// if a sum is plugged into a variable preceded by minus
					((Element) subPrev).setInnerText("+");
					Element negativeSubstitute = new HTML(
							"<mrow><mn>-1</mn><mo>\u00B7</mo><mfenced><substitute></substitute></mfenced></mrow>")
							.getElement().getFirstChildElement();
					subParent.replaceChild(negativeSubstitute, possibleSub);
					possibleSub = subParent.getElementsByTagName("substitute")
							.getItem(0);
					subParent = possibleSub.getParentElement();
				}
				NodeList<Node> substituteChildren = substitute.getChildNodes();
				// Must save count because it will change during the
				// loop
				int childCount = substituteChildren.getLength();
				for (int k = 0; k < childCount; k++) {
					subParent.insertBefore(substituteChildren.getItem(0),
							possibleSub);
				}
				possibleSub.removeFromParent();
			} else {
				subParent.replaceChild(substitute, possibleSub);
			}
			Moderator.makeAlgebra(subIntoEqEl, false);
			break;

		}
	}
}

/**
 * Unravel function within inverse function or inverse function within function<br/>
 * sin(arcsin(x)) = x<br/>
 * arcsin(sin(x)) = x<br/>
 * log<sub>b</sub>(b<sup>x</sup>) = x<br/>
 * b<sup>log<sub>b</sub>(x)</sup> = x<br/>
 */
class UnravelButton extends TransformationButton {

	public UnravelButton(final MathNode toReplace, final MathNode replacement,
			final Rule rule) {
		super();

		setHTML(replacement.getHTMLString());

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String changeComment = toReplace.getHTMLString() + " = "
						+ replacement.getHTMLString();
				toReplace.replace(replacement);
				Moderator.reloadEquationPanel(changeComment, rule);
			}
		});
	}
}
