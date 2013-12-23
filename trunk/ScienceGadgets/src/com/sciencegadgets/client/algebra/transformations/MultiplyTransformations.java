package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class MultiplyTransformations {
	protected static MathNode operation;
	protected static MathNode parent;
	protected static MathNode grandParent;
	private static final int same = 0;

	public static void assign(MathNode left, MathNode multiplyNode,
			MathNode right) {
		operation = multiplyNode;
		parent = multiplyNode.getParent();
		grandParent = parent.getParent();

		TypeML leftType = left.getType();
		TypeML rightType = right.getType();

		// Check for improper types for left and right
		switch (leftType) {
		case Equation:
		case Term:
			JSNICalls.error("Illegal node within Term: " + leftType);
			break;
		case Operation:
			JSNICalls.error("Operation in wrong place: "
					+ left.getParent().toString());
			break;
		case Number:
			BigDecimal leftValue = new BigDecimal(left.getSymbol());
			if (leftValue.compareTo(new BigDecimal(0)) == same) {
				multiplyZero(right, left);
				return;
			} else if (leftValue.compareTo(new BigDecimal(1)) == same) {
				multiplyOne(right, left);
				return;
			} else if (leftValue.compareTo(new BigDecimal(-1)) == same) {
				multiplyNegOne(right, left);
				return;
			}
			break;
		}
		switch (rightType) {
		case Equation:
		case Term:
			JSNICalls.error("Illegal node within Term: " + rightType);
			break;
		case Operation:
			JSNICalls.error("Operation in wrong place: "
					+ right.getParent().toString());
			break;
		case Number:
			BigDecimal rightValue = new BigDecimal(right.getSymbol());
			if (rightValue.compareTo(new BigDecimal(0)) == same) {
				multiplyZero(left, right);
				return;
			} else if (rightValue.compareTo(new BigDecimal(1)) == same) {
				multiplyOne(left, right);
				return;
			} else if (rightValue.compareTo(new BigDecimal(-1)) == same) {
				multiplyNegOne(left, right);
				return;
			}
			break;
		}

		// Assignments in 2d switch (leftType, rightType)
		first: switch (leftType) {
		case Sum:
			second: switch (rightType) {
			case Sum:
				multiplyDistribution(left, right, false);
				break second;
			case Exponential:
			case Fraction:
			case Variable:
			case Number:
				multiplyDistribution(right, left, true);
				break second;
			}
			break first;
		case Exponential:
			second: switch (rightType) {
			case Sum:
				multiplyDistribution(left, right, false);
				break second;
			case Exponential:
				multiplyCombineBases(left, right);
				break second;
			case Fraction:
				multiplyWithFraction(left, right, false);
				break second;
			case Variable:
			case Number:
				multiplyCombineBases(left, right);
				break second;
			}
			break first;
		case Fraction:
			second: switch (rightType) {
			case Sum:
				multiplyDistribution(left, right, false);
				break second;
			case Fraction:
				multiplyFractions(left, right);
				break second;
			case Exponential:
			case Variable:
			case Number:
				multiplyWithFraction(right, left, true);
				break second;
			}
			break first;
		case Variable:
			if (TypeML.Variable.equals(rightType)) {
				multiplyCombineBases(left, right);
				break first;
			}
			// fall through
		case Number:
			second: switch (rightType) {
			case Sum:
				multiplyDistribution(left, right, false);
				break second;
			case Exponential:
				multiplyCombineBases(left, right);
				break second;
			case Fraction:
				multiplyWithFraction(left, right, false);
				break second;
			case Variable:
				// multiplyOperationToSpace();
				break second;
			case Number:
				multiplyNumbers_prompt(left, right);
				break second;
			}
			break first;
		}
	}

	private static void multiplyDistribution(MathNode dist, MathNode sum,
			boolean reversed) {

		dist.highlight();
		operation.highlight();

		for (MathNode sumChild : sum.getChildren()) {
			if (!TypeML.Operation.equals(sumChild.getType())) {

				MathNode sumChildCasings = sumChild.encase(TypeML.Term);

				int index = reversed ? -1 : 0;
				sumChildCasings.addBefore(index, operation.getType(),
						operation.getSymbol());
				sumChildCasings.addBefore(index, dist.clone());
			}
		}

		dist.remove();
		operation.remove();

		parent.decase();

		AlgebraActivity.reloadEquationPanel("Distribute", Rule.Distribution);
	}

	private static boolean multiplyCombineExponents(MathNode left,
			MathNode right) {

		left.getChildAt(0).highlight();
		operation.highlight();
		right.getChildAt(0).highlight();

		if (!left.getChildAt(1).isLike(right.getChildAt(1))) {
			return false;
		}

		MathNode leftBase = left.getChildAt(0);
		MathNode rightBase = right.getChildAt(0);

		MathNode rightCasing = rightBase.encase(TypeML.Term);

		rightCasing.addBefore(0, operation);
		rightCasing.addBefore(0, leftBase);

		left.remove();

		parent.decase();

		AlgebraActivity
				.reloadEquationPanel("Combine Exponents", Rule.Exponents);
		return true;
	}

	private static boolean multiplyCombineBases(MathNode left, MathNode right) {

		left.highlight();
		operation.highlight();
		right.highlight();

		// May not already be in exponent eg. a = a^1
		// could factor out entire side rather than just base
		MathNode leftBase;
		if (TypeML.Exponential.equals(left.getType())) {
			leftBase = left.getChildAt(0);
		} else {
			leftBase = left;
		}
		MathNode rightBase;
		if (TypeML.Exponential.equals(right.getType())) {
			rightBase = right.getChildAt(0);
		} else {
			rightBase = right;
		}

		if (!leftBase.isLike(rightBase)) {
			// TODO only in automatic mode
			if (TypeML.Exponential.equals(left.getType())
					&& TypeML.Exponential.equals(right.getType())) {
				multiplyCombineExponents(left, right);
			}
			// else if (Type.Variable.equals(left.getType())
			// && Type.Variable.equals(right.getType())) {
			// multiplyOperationToSpace();
			// }
			return false;
		} else {
			if (!TypeML.Exponential.equals(left.getType())) {
				left = left.encase(TypeML.Exponential);
				left.append(TypeML.Number, "1");
			}
			if (!TypeML.Exponential.equals(right.getType())) {
				right = right.encase(TypeML.Exponential);
				right.append(TypeML.Number, "1");
			}
		}

		MathNode leftExp = left.getChildAt(1);
		MathNode rightExp = right.getChildAt(1);

		MathNode rightCasing = rightExp.encase(TypeML.Sum);

		rightCasing.addBefore(0, TypeML.Operation, Operator.PLUS.getSign());
		rightCasing.addBefore(0, leftExp);

		left.remove();
		operation.remove();

		parent.decase();

		AlgebraActivity.reloadEquationPanel("Combine Bases", Rule.Exponents);
		return true;
	}

	private static void multiplyWithFraction(MathNode nonFrac,
			MathNode fraction, boolean reversed) {

		fraction.highlight();
		operation.highlight();
		nonFrac.highlight();

		MathNode numerator = fraction.getChildAt(0);
		numerator = numerator.encase(TypeML.Term);

		int index = reversed ? -1 : 0;
		numerator.addBefore(index, operation);
		numerator.addBefore(index, nonFrac);

		parent.decase();

		AlgebraActivity.reloadEquationPanel("Multiply with Fraction",
				Rule.FractionMultiplication);
	}

	private static void multiplyFractions(MathNode left, MathNode right) {

		left.highlight();
		operation.highlight();
		right.highlight();

		MathNode numerator = right.getChildAt(0);
		numerator = numerator.encase(TypeML.Term);
		numerator.addBefore(0, operation);
		numerator.addBefore(0, left.getChildAt(0));

		MathNode denominator = right.getChildAt(1);
		denominator = denominator.encase(TypeML.Term);
		denominator.addBefore(0, TypeML.Operation, operation.getSymbol());
		denominator.addBefore(0, left.getChildAt(1));

		left.remove();

		parent.decase();

		AlgebraActivity.reloadEquationPanel("Multiply Fractions",
				Rule.FractionMultiplication);
	}

	// private static void multiplyOperationToSpace() {
	//
	// operation.highlight();
	//
	// operation.setSymbol(Operator.SPACE.getSign());
	//
	// Moderator.reloadEquationPanel("Op to Space", null);
	// }

	private static void multiplyNumbers_prompt(final MathNode left,
			final MathNode right) {

		final BigDecimal leftValue = new BigDecimal(left.getSymbol());
		final BigDecimal rightValue = new BigDecimal(right.getSymbol());
		final BigDecimal totalValue = leftValue.multiply(rightValue);

		if (AlgebraActivity.isInEasyMode) {
			multiplyNumbers(left, right, totalValue, leftValue, rightValue);

		} else {// prompt

			final Prompt prompt = new Prompt();

			prompt.add(new HTML(leftValue.toString() + " "
					+ operation.getSymbol() + " " + rightValue.toString()));

			final TextBox input = new TextBox();
			input.getElement().setAttribute("type", "number");
			prompt.add(input);

			final HTML incorrectResponse = new HTML("<b>Incorrect</b>");

			prompt.addOkHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					try {
						BigDecimal inputValue = new BigDecimal(input.getValue());
						if (inputValue.compareTo(totalValue) == same) {
							// correct
							prompt.disappear();
							multiplyNumbers(left, right, totalValue, leftValue,
									rightValue);
						} else {
							// incorrect
							incorrectResponse.setHTML(incorrectResponse
									.getHTML() + "<br/>" + inputValue);
							prompt.add(incorrectResponse);
						}
					} catch (NumberFormatException e) {
						incorrectResponse.setHTML(incorrectResponse.getHTML()
								+ "<br/>Not a number");
						prompt.add(incorrectResponse);
					}
				}
			});
			prompt.appear();

			input.setFocus(true);

		}

	}

	static void multiplyNumbers(MathNode left, MathNode right,
			BigDecimal totalValue, BigDecimal leftValue, BigDecimal rightValue) {

		right.highlight();
		operation.highlight();
		left.highlight();

		totalValue = totalValue.stripTrailingZeros();

		right.setSymbol(totalValue.stripTrailingZeros().toEngineeringString());

		String leftUnit = left.getUnitAttribute();
		String rightUnit = right.getUnitAttribute();
		right.setAttribute(MathAttribute.Unit, rightUnit + "*" + leftUnit);

		left.remove();
		operation.remove();

		parent.decase();

		AlgebraActivity.reloadEquationPanel(leftValue.stripTrailingZeros()
				.toEngineeringString()
				+ " "
				+ operation.toString()
				+ " "
				+ rightValue.stripTrailingZeros().toEngineeringString()
				+ " = "
				+ totalValue.stripTrailingZeros().toEngineeringString(),
				Rule.Multiplication);
	}

	private static void multiplyZero(MathNode other, MathNode zero) {

		zero.highlight();
		operation.highlight();
		other.highlight();

		String otherSymbol = other.getSymbol();

		// Remove residual operations
		MathNode first = zero.getIndex() < other.getIndex() ? zero : other;
		MathNode second = zero.getIndex() > other.getIndex() ? zero : other;
		MathNode firstOp = first.getPrevSibling();
		MathNode secondNext = second.getNextSibling();
		if (firstOp != null && TypeML.Operation.equals(firstOp.getType())) {
			firstOp.remove();
		} else if (secondNext != null
				&& TypeML.Operation.equals(secondNext.getType())) {
			secondNext.remove();
		}

		zero.remove();
		operation.remove();
		other.remove();

		parent.decase();

		AlgebraActivity.reloadEquationPanel(
				otherSymbol + " " + operation.toString() + " 0 = 0",
				Rule.Multiplication);
	}

	private static void multiplyOne(MathNode other, MathNode one) {

		other.highlight();
		operation.highlight();
		one.highlight();

		String otherSymbol = other.getSymbol();

		one.remove();
		operation.remove();

		parent.decase();

		AlgebraActivity.reloadEquationPanel(
				otherSymbol + " " + operation.toString() + " 1 = "
						+ otherSymbol, Rule.Multiplication);
	}

	private static void multiplyNegOne(MathNode other, MathNode negOne) {

		other.highlight();
		operation.highlight();
		negOne.highlight();

		String otherSymbol = other.getSymbol();

		AlgebraicTransformations.propagateNegative(other);

		operation.remove();
		negOne.remove();

		parent.decase();

		AlgebraActivity.reloadEquationPanel(
				otherSymbol + " " + operation.toString() + " -1 = -"
						+ otherSymbol, Rule.Multiplication);
	}

}
