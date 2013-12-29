package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.KeyPadNumerical;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class AdditionTransformations {

	protected static MathNode operation;
	protected static MathNode parent;
	protected static MathNode grandParent;
	protected static boolean isPlus;
	protected static boolean isMinusBeforeLeft = false;
	private static final int same = 0;

	public static void assign(MathNode left, MathNode sign, MathNode right,
			boolean isPlusSign) {
		try {

			operation = sign;
			parent = operation.getParent();
			grandParent = parent.getParent();
			isPlus = isPlusSign;// Operator.PLUS.getSign().equals(sign.getSymbol());
			boolean assigned = false;

			// propagate the negative to the left node if preceded by a minus
			MathNode leftPrev = left.getPrevSibling();
			if (leftPrev != null && "-".equals(leftPrev.getSymbol())
					&& TypeML.Operation.equals(leftPrev.getType())) {
				isMinusBeforeLeft = true;
			}

			TypeML leftType = left.getType();
			TypeML rightType = right.getType();

			// Check for improper types for left and right
			switch (rightType) {
			case Equation:
			case Sum:
				JSNICalls.error("Illegal node within Sum: " + rightType);
				break;
			case Operation:
				JSNICalls.error("Operation in wrong place: "
						+ right.getParent().toString());
				break;
			case Number:
				BigDecimal rightValue = new BigDecimal(right.getSymbol());
				if (rightValue.compareTo(new BigDecimal(0)) == same) {
					addZero(left, right);
					return;
				}
				break;
			}
			switch (leftType) {
			case Equation:
			case Sum:
				JSNICalls.error("Illegal node within Sum: " + leftType);
				break;
			case Operation:
				JSNICalls.error("Operation in wrong place: "
						+ left.getParent().toString());
				break;
			case Number:
				BigDecimal leftValue = new BigDecimal(left.getSymbol());
				if (leftValue.compareTo(new BigDecimal(0)) == same) {
					addZero(right, left);
					return;
				}
				break;
			}

			// Assignments in 2d switch (leftType, rightType)
			first: switch (leftType) {
			case Term:
				second: switch (rightType) {
				case Term:
					assigned = addSimilar_check(left, right);
					if (!AlgebraActivity.isInEasyMode || !assigned)
						factorLikeTerms_check(left, right);
					break second;
				case Exponential:
					assigned = factorWithBase_check(left, right);
					if (!AlgebraActivity.isInEasyMode || !assigned)
						factorWithTermChild_check(right, left);
					break second;
				case Fraction:
					assigned = factorWithTermChild_check(right, left);
					if (!AlgebraActivity.isInEasyMode || !assigned)
						factorLikeTerms_check(left, right);
					break second;
				case Variable:
					factorWithTermChild_check(right, left);
					break second;
				case Number:
					factorWithTermChild_check(right, left);
					break second;
				}
				break first;
			case Exponential:
				second: switch (rightType) {
				case Term:
					assigned = factorWithBase_check(right, left);
					if (!AlgebraActivity.isInEasyMode || !assigned)
						factorWithTermChild_check(left, right);
					break second;
				case Exponential:
					assigned = addSimilar_check(left, right);
					if (!AlgebraActivity.isInEasyMode || !assigned)
						assigned = factorWithBase_check(left, right);
					if (!AlgebraActivity.isInEasyMode || !assigned)
						factorWithBase_check(right, left);
					break second;
				case Fraction:
					assigned = factorWithTermChild_check(left, right);
					if (!AlgebraActivity.isInEasyMode || !assigned)
						factorWithBase_check(right, left);
					break second;
				case Variable:
					factorWithBase_check(right, left);
					break second;
				case Number:
					factorWithBase_check(right, left);
					break second;
				}
				break first;
			case Fraction:
				second: switch (rightType) {
				case Term:
					assigned = factorWithTermChild_check(left, right);
					if (!AlgebraActivity.isInEasyMode || !assigned)
						factorLikeTerms_check(left, right);
					break second;
				case Exponential:
					assigned = factorWithTermChild_check(right, left);
					if (!AlgebraActivity.isInEasyMode || !assigned)
						factorWithBase_check(right, left);
					break second;
				case Fraction:
					assigned = addSimilar_check(left, right);
					if (!AlgebraActivity.isInEasyMode || !assigned)
						assigned = addFractions_check(left, right);
					if (!AlgebraActivity.isInEasyMode || !assigned)
						factorLikeTerms_check(left, right);
					break second;
				case Variable:
					factorWithTermChild_check(right, left);
					break second;
				case Number:
					factorWithTermChild_check(right, left);
					break second;
				}
				break first;
			case Variable:
				second: switch (rightType) {
				case Term:
					factorWithTermChild_check(left, right);
					break second;
				case Exponential:
					factorWithBase_check(left, right);
					break second;
				case Fraction:
					factorWithTermChild_check(left, right);
					break second;
				case Variable:
					addSimilar_check(left, right);
					break second;
				}
				break first;
			case Number:
				second: switch (rightType) {
				case Term:
					factorWithTermChild_check(left, right);
					break second;
				case Exponential:
					factorWithBase_check(left, right);
					break second;
				case Fraction:
					factorWithTermChild_check(left, right);
					break second;
				case Number:
					addNumbers_prompt(left, right);
					break second;
				}
				break first;
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
			JSNICalls
					.error("A number node couldn't be parsed: " + e.toString());
		}
	}

	private static void factor(Collection<MathNode> factors,
			MathNode inBinomialA, MathNode inBinomialB) {

		MathNode inBinomialFirst = inBinomialA;
		MathNode inBinomialSecond = inBinomialB;
		if (inBinomialA.getIndex() > inBinomialB.getIndex()) {
			inBinomialFirst = inBinomialB;
			inBinomialSecond = inBinomialA;
		}

		MathNode termCasing = operation.getParent().addBefore(
				operation.getIndex(), TypeML.Term, "");

		for (MathNode factor : factors) {
			termCasing.append(factor);
			termCasing.append(TypeML.Operation, Operator.getMultiply()
					.getSign());
		}

		MathNode binomialCasing = termCasing.append(TypeML.Sum, "");
		binomialCasing.append(inBinomialFirst);
		binomialCasing.append(operation);
		binomialCasing.append(inBinomialSecond);

		parent.decase();
	}

	private static boolean factorLikeTerms_check(MathNode left, MathNode right) {
		MathNode leftTerm = left;
		MathNode rightTerm = right;

		// If sides are fractions, like terms come from numerators
		if (TypeML.Fraction.equals(left.getType())) {
			if (TypeML.Term.equals(left.getChildAt(0).getType())) {
				leftTerm = left.getChildAt(0);
			} else {
				return false;
			}
		}
		if (TypeML.Fraction.equals(right.getType())) {
			if (TypeML.Term.equals(right.getChildAt(0).getType())) {
				rightTerm = right.getChildAt(0);
			} else {
				return false;
			}
		}

		// Collect like terms
		LinkedHashMap<MathNode, MathNode> likeTerms = new LinkedHashMap<MathNode, MathNode>();
		a: for (MathNode leftChild : leftTerm.getChildren()) {
			if (TypeML.Operation.equals(leftChild.getType())) {
				continue a;
			}
			b: for (MathNode rightChild : rightTerm.getChildren()) {
				if (likeTerms.containsValue(rightChild)
						|| TypeML.Operation.equals(rightChild.getType())) {
					continue b;
				}
				if (leftChild.isLike(rightChild)) {
					likeTerms.put(leftChild, rightChild);
					continue a;
				}
			}
		}
		// Check
		if (likeTerms.size() == 0) {
			return false;
		}

		if (AlgebraActivity.isInEasyMode) {
			factorLikeTerms(left, right, likeTerms);
		} else {
			AlgebraActivity.algTransformMenu.add(new FLTButton(left, right,
					likeTerms));
		}

		return true;
	}

	/**
	 * Factors out all like entities within the term or numerator
	 * 
	 * @param left
	 *            - term, fraction
	 * @param right
	 *            - term, fraction
	 */
	static void factorLikeTerms(MathNode left, MathNode right,
			LinkedHashMap<MathNode, MathNode> likeTerms) {
		// Highlight terms in AlgOut
		for (MathNode key : likeTerms.keySet()) {
			likeTerms.get(key).highlight();
			key.highlight();
		}

		// Like terms are cloned to simplify cleanup
		LinkedList<MathNode> factors = new LinkedList<MathNode>();
		for (MathNode factor : likeTerms.values()) {
			factors.add(factor.clone());
		}

		// Remove operation on like term
		if (likeTerms.size() * 2 - 1 == left.getChildCount()) {
			left = left.replace(TypeML.Term, "");// will be MathNode.decase[d]
			left.append(TypeML.Number, "1");
		} else {
			for (MathNode extraFactor : likeTerms.keySet()) {

				if (extraFactor.getIndex() == 0) {
					MathNode nextOp = extraFactor.getNextSibling();
					if (nextOp != null
							&& TypeML.Operation.equals(nextOp.getType())) {
						nextOp.remove();
					}
				} else {
					MathNode prevOp = extraFactor.getPrevSibling();
					if (TypeML.Operation.equals(prevOp.getType())) {
						prevOp.remove();
					}
				}
				extraFactor.remove();
			}
		}

		if (likeTerms.size() * 2 - 1 == right.getChildCount()) {
			right = right.replace(TypeML.Term, "");
			right.append(TypeML.Number, "1");
		} else {
			for (MathNode fact : likeTerms.values()) {
				if (fact.getIndex() == 0) {
					MathNode nextOp = fact.getNextSibling();
					if (nextOp != null
							&& TypeML.Operation.equals(nextOp.getType())) {
						nextOp.remove();
					}
				} else {
					MathNode prevOp = fact.getPrevSibling();
					if (TypeML.Operation.equals(prevOp.getType())) {
						prevOp.remove();
					}
				}
				fact.remove();
			}
		}

		factor(factors, left, right);

		left.decase();
		right.decase();

		AlgebraActivity.reloadEquationPanel("Factor Like Terms",
				Rule.COMBINING_LIKE_TERMS);
	}

	private static boolean factorWithBase_check(MathNode other,
			MathNode exponential) {

		if (!other.isLike(exponential.getChildAt(0))) {
			return false;
		}

		if (AlgebraActivity.isInEasyMode) {
			factorWithBase(other, exponential);
		} else {
			AlgebraActivity.algTransformMenu.add(new FWBButton(other,
					exponential));
		}

		return true;
	}

	/**
	 * Factors out a single multiple of the base and other entity if equal
	 * 
	 * @param other
	 *            - term, exponential, fraction, variable, number
	 * @param exponential
	 */
	static void factorWithBase(MathNode other, MathNode exponential) {
		other.highlight();
		operation.highlight();
		exponential.getChildAt(0).highlight();

		MathNode inBinomialB = exponential;
		MathNode inBinomialA = other.getParent().addBefore(other.getIndex(),
				TypeML.Number, "1");

		LinkedList<MathNode> factors = new LinkedList<MathNode>();
		factors.add(other);
		factor(factors, inBinomialA, inBinomialB);

		MathNode exp = exponential.getChildAt(1).encase(TypeML.Sum);
		exp.append(TypeML.Operation, Operator.MINUS.getSign());
		exp.append(TypeML.Number, "1");

		AlgebraActivity.reloadEquationPanel("Factor with Base",
				Rule.FACTORIZATION);
	}

	private static boolean factorWithTermChild_check(MathNode other,
			MathNode termContainer) {
		MathNode term = termContainer;

		// If sides are fractions, like terms come from numerators
		if (TypeML.Fraction.equals(termContainer.getType())) {
			if (TypeML.Term.equals(termContainer.getChildAt(0).getType())) {
				term = termContainer.getChildAt(0);
			} else {
				return false;
			}
		}

		for (MathNode termChild : term.getChildren()) {
			if (termChild.isLike(other)) {
				if (AlgebraActivity.isInEasyMode) {
					factorWithTermChild(other, termContainer, termChild);
				} else {
					AlgebraActivity.algTransformMenu.add(new FWTCButton(other,
							termContainer, termChild));
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Factors out a child of the term if similar to the other entity
	 * 
	 * @param other
	 *            - exponential, fraction, variable, number
	 * @param term
	 *            - term, fraction
	 */
	static void factorWithTermChild(MathNode other, MathNode term,
			MathNode termChild) {
		other.highlight();
		operation.highlight();
		termChild.highlight();

		MathNode inBinomialA = other;
		MathNode inBinomialB = term;

		if (termChild.getIndex() == 0) {
			MathNode nextOp = termChild.getNextSibling();
			if (nextOp != null && TypeML.Operation.equals(nextOp.getType())) {
				nextOp.remove();
			}
		} else {
			MathNode prevOp = termChild.getPrevSibling();
			if (TypeML.Operation.equals(prevOp.getType())) {
				prevOp.remove();
			}
		}

		// Idea: factor a single multiple of a base, other must be base

		// if (Type.Exponential.equals(other.getParentType())) {
		// inBinomialA = other.getParent();
		// MathNode exp = other.getNextSibling().encase(Type.Sum);
		// exp.add(Type.Operation, Operator.MINUS.getSign());
		// exp.add(Type.Number, "1");
		// } else {
		inBinomialA = other.getParent().addBefore(other.getIndex(),
				TypeML.Number, "1");
		other.remove();
		// }

		LinkedList<MathNode> factors = new LinkedList<MathNode>();
		factors.add(termChild);
		factor(factors, inBinomialA, inBinomialB);

		term.decase();

		AlgebraActivity.reloadEquationPanel("Factor", Rule.FACTORIZATION);
	}

	private static boolean addFractions_check(MathNode left, MathNode right) {
		// Common denominators
		if (!left.getChildAt(1).isLike(right.getChildAt(1))) {
			return false;
		}

		if (AlgebraActivity.isInEasyMode) {
			addFractions(left, right);
		} else {
			AlgebraActivity.algTransformMenu.add(new AddFractionsButton(left,
					right));
		}

		return true;

	}

	static void addFractions(MathNode left, MathNode right) {

		right.highlight();
		operation.highlight();
		left.highlight();

		if (isMinusBeforeLeft) {
			left.getPrevSibling().setSymbol(Operator.PLUS.getSign());
			AlgebraicTransformations.propagateNegative(left);
		}

		MathNode numeratorCasing = right.getChildAt(0).encase(TypeML.Sum);
		numeratorCasing.addBefore(0, operation);
		numeratorCasing.addBefore(0, left.getChildAt(0));

		left.remove();
		parent.decase();

		AlgebraActivity.reloadEquationPanel("Add Fractions",
				Rule.FRACTION_ADDITION);
	}

	private static boolean addSimilar_check(MathNode left, MathNode right) {
		if (!left.isLike(right)) {
			return false;
		}

		if (AlgebraActivity.isInEasyMode) {
			addSimilar(left, right);
		} else {
			AlgebraActivity.algTransformMenu.add(new AddSimilarButton(left,
					right));
		}
		return true;
	}

	static void addSimilar(MathNode left, MathNode right) {

		right.highlight();
		operation.highlight();
		left.highlight();

		if (isPlus && !isMinusBeforeLeft) {
			MathNode casing = right.encase(TypeML.Term);
			casing.addBefore(0, TypeML.Operation, Operator.getMultiply()
					.getSign());
			casing.addBefore(0, TypeML.Number, "2");
		} else if (!isPlus && isMinusBeforeLeft) {
			MathNode casing = right.encase(TypeML.Term);
			casing.addBefore(0, TypeML.Operation, Operator.getMultiply()
					.getSign());
			casing.addBefore(0, TypeML.Number, "-2");
		} else if ((!isPlus && !isMinusBeforeLeft)
				|| (isPlus && isMinusBeforeLeft)) {
			// Remove residual operations
			MathNode leftOp = left.getPrevSibling();
			MathNode rightNext = right.getNextSibling();
			if (leftOp != null && TypeML.Operation.equals(leftOp.getType())) {
				leftOp.remove();
			} else if (rightNext != null
					&& TypeML.Operation.equals(rightNext.getType())) {
				rightNext.remove();
			}

			right.remove();
		}

		left.remove();
		operation.remove();
		parent.decase();

		AlgebraActivity.reloadEquationPanel("Add similar",
				Rule.COMBINING_LIKE_TERMS);
	}

	private static void addNumbers_prompt(final MathNode left,
			final MathNode right) {

		if (!left.getUnitAttribute().equals(right.getUnitAttribute())) {
			Window.alert("You can only add quantities with similar units.\nPlease convert first.");
			return;
		}

		BigDecimal leftV;
		if (isMinusBeforeLeft) {
			leftV = new BigDecimal(left.getSymbol()).negate();
		} else {
			leftV = new BigDecimal(left.getSymbol());
		}
		final BigDecimal leftValue = leftV;
		final BigDecimal rightValue = new BigDecimal(right.getSymbol());
		BigDecimal total;

		if (isPlus) {
			total = leftValue.add(rightValue);
		} else {
			total = leftValue.subtract(rightValue);
		}
		final BigDecimal totalValue = total;

		if (AlgebraActivity.isInEasyMode) {
			addNumbers(left, right, totalValue, leftValue, rightValue);

		} else {// prompt

			String question = leftValue.toString() + " "
					+ operation.getSymbol() + " " + rightValue.toString()
					+ " = ";
			NumberPrompt prompt = new NumberPrompt(question, totalValue) {
				@Override
				void onCorrect() {
					addNumbers(left, right, totalValue, leftValue, rightValue);
				}
			};
			prompt.appear();
		}

	}

	static void addNumbers(MathNode left, MathNode right,
			BigDecimal totalValue, BigDecimal leftValue, BigDecimal rightValue) {

		right.highlight();
		operation.highlight();
		left.highlight();

		right.setSymbol(totalValue.stripTrailingZeros().toEngineeringString());

		if (isMinusBeforeLeft) {
			left.getPrevSibling().setSymbol(Operator.PLUS.getSign());
		}

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
				Rule.ADDITION);
	}

	static void addZero(MathNode other, MathNode zero) {

		zero.highlight();

		operation.remove();
		zero.remove();

		parent.decase();

		AlgebraActivity.reloadEquationPanel("Add zero", Rule.ADDITION);
	}
}

class FLTButton extends Button {

	FLTButton(final MathNode left, final MathNode right,
			final LinkedHashMap<MathNode, MathNode> likeTerms) {

		String factorString = "";
		for (MathNode fact : likeTerms.keySet()) {
			factorString.concat(fact.getHTMLString());
		}
		setHTML("Factor " + factorString);

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdditionTransformations.factorLikeTerms(left, right, likeTerms);
			}
		});
	}
}

class FWBButton extends Button {

	FWBButton(final MathNode other, final MathNode exponential) {

		setHTML("Factor " + other.getHTMLString());

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdditionTransformations.factorWithBase(other, exponential);
			}
		});
	}
}

class FWTCButton extends Button {

	FWTCButton(final MathNode other, final MathNode termContainer,
			final MathNode termChild) {

		setHTML("Factor " + other.getHTMLString());

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdditionTransformations.factorWithTermChild(other,
						termContainer, termChild);
			}
		});
	}
}

class AddFractionsButton extends Button {

	AddFractionsButton(final MathNode left, final MathNode right) {

		setHTML("Add Fractions");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdditionTransformations.addFractions(left, right);
			}
		});
	}
}

class AddSimilarButton extends Button {

	AddSimilarButton(final MathNode left, final MathNode right) {

		setHTML("Add Similar");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdditionTransformations.addSimilar(left, right);
			}
		});
	}
}
