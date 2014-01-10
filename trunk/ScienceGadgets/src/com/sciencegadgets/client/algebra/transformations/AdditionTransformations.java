package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class AdditionTransformations {

	MathNode left;
	MathNode operation;
	MathNode right;

	MathNode parent;

	private TypeML leftType;
	private TypeML rightType;

	boolean isMinus;
	boolean isMinusBeforeLeft = false;

	LinkedList<AddTransformButton> transformations = new LinkedList<AddTransformButton>();

	public AdditionTransformations(MathNode left, MathNode operation,
			MathNode right,

			boolean isPlusSign) {
		try {

			this.left = left;
			this.operation = operation;
			this.right = right;
			this.parent = operation.getParent();
			isMinus = !isPlusSign;

			MathNode leftPrev = left.getPrevSibling();
			if (leftPrev != null && "-".equals(leftPrev.getSymbol())
					&& TypeML.Operation.equals(leftPrev.getType())) {
				isMinusBeforeLeft = true;
			}

			this.leftType = left.getType();
			this.rightType = right.getType();

			check(addNumbers_check());
			check(addSimilar_check());
			check(factorLikeTerms_check());
			check(factorWithBase_check());
			check(factorWithTermChild_check());
			check(addFractions_check());
			check(logCombination_check());

			if (AlgebraActivity.isInEasyMode && transformations.size() == 1) {
				transformations.getFirst().click();
			} else {
				for (AddTransformButton transform : transformations) {
					AlgebraActivity.addTransformation(transform);
				}
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
			JSNICalls
					.error("A number node couldn't be parsed: " + e.toString());
		}
	}

	private void check(AddTransformButton tButt) {
		if (tButt != null) {
			transformations.add(tButt);
		}
	}

	private AddTransformButton addNumbers_check() {

		if (!TypeML.Number.equals(leftType) || !TypeML.Number.equals(rightType)) {
			return null;
		}

		final int same = 0;

		BigDecimal leftValue = new BigDecimal(left.getSymbol());
		if (leftValue.compareTo(new BigDecimal(0)) == same) {
			return new AddZeroTransform(this, right, left);
		}
		BigDecimal rightValue = new BigDecimal(right.getSymbol());
		if (rightValue.compareTo(new BigDecimal(0)) == same) {
			return new AddZeroTransform(this, left, right);
		}
		return new AddNumbersTransform(this);
	}

	private AddTransformButton addSimilar_check() {
		if (leftType != rightType) {
			return null;
		}
		if (left.isLike(right)) {
			return new AddSimilarTramsform(this);
		} else {
			return null;
		}
	}

	/**
	 * xy + xz = x(y + z)<br/>
	 * Factors out all like entities within the term or numerator<br/>
	 */
	private AddTransformButton factorLikeTerms_check() {

		// If sides are fractions, like terms come from numerators
		MathNode leftTerm = getTerm(left);
		if (leftTerm == null) {
			return null;
		}
		MathNode rightTerm = getTerm(right);
		if (rightTerm == null) {
			return null;
		}

		// Collect like terms
		final LinkedHashMap<MathNode, MathNode> likeTerms = new LinkedHashMap<MathNode, MathNode>();
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
			return null;
		}

		String factorString = "";
		for (MathNode fact : likeTerms.keySet()) {
			factorString.concat(fact.getHTMLString());
		}

		return new FactorLikeTermsTransform(this, leftTerm, rightTerm,
				likeTerms);
	}

	private AddTransformButton factorWithBase_check() {

		if (TypeML.Exponential.equals(leftType)) {
			if (right.isLike(left.getChildAt(0))) {
				return new FactorBaseTransform(this, right, left);
			}
		} else if (TypeML.Exponential.equals(rightType)) {
			if (left.isLike(right.getChildAt(0))) {
				return new FactorBaseTransform(this, left, right);
			}
		}
		return null;
	}

	private AddTransformButton factorWithTermChild_check() {
		MathNode term, other;

		// If sides are fractions, like terms come from numerators
		MathNode leftTerm = getTerm(left);
		MathNode rightTerm = getTerm(right);
		if (leftTerm != null && rightTerm == null) {
			term = leftTerm;
			other = right;
		} else if (leftTerm == null && rightTerm != null) {
			term = rightTerm;
			other = left;
		} else {
			return null;
		}

		for (final MathNode termChild : term.getChildren()) {
			if (termChild.isLike(other)) {
				return new FactorWithTermChildTransform(this, other, term,
						termChild);
			}
		}
		return null;
	}

	private AddTransformButton addFractions_check() {
		// Common denominators
		if (!TypeML.Fraction.equals(leftType) || !TypeML.Fraction.equals(right)) {
			return null;
		}
		if (left.getChildAt(1).isLike(right.getChildAt(1))) {
			return new AddFractions(this);
		} else {
			return null;
		}
	}

	private AddTransformButton logCombination_check() {
		if (!TypeML.Log.equals(leftType) || !TypeML.Log.equals(rightType)) {
			return null;
		}
		if (left.getAttribute(MathAttribute.LogBase).equals(
				right.getAttribute(MathAttribute.LogBase))) {
			return new AddLogsTransform(this);
		} else {
			return null;
		}
	}

	/**
	 * Sub routine to be used at the end of the other factor methods
	 */
	void factor(Collection<MathNode> factors, MathNode inBinomialA,
			MathNode inBinomialB) {

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

	/**
	 * If the node is of type fraction, numerators are considered the term
	 */
	private MathNode getTerm(MathNode node) {
		switch (node.getType()) {
		case Fraction:
			if (TypeML.Term.equals(left.getChildAt(0).getType())) {
				return left.getChildAt(0);
			}
			return null;
		case Term:
			return node;
		default:
			return null;
		}
	}
}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////
class AddTransformButton extends Button {
	final MathNode left;
	final MathNode right;
	final MathNode operation;
	final MathNode parent;
	final MathNode grandParent;
	final boolean isMinus;
	final boolean isMinusBeforeLeft;

	AddTransformButton(AdditionTransformations context, String html) {
		super(html);
		this.left = context.left;
		this.right = context.right;
		this.operation = context.operation;
		this.parent = operation.getParent();
		this.grandParent = parent.getParent();
		this.isMinus = context.isMinus;
		this.isMinusBeforeLeft = context.isMinusBeforeLeft;
	}
}

/**
 * x + 0 = x<br/>
 * 0 + x = x
 */
class AddZeroTransform extends AddTransformButton {
	AddZeroTransform(AdditionTransformations context, final MathNode other,
			final MathNode zero) {
		super(context, "x+0=x");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				zero.highlight();

				if (isMinus && other.getIndex() > zero.getIndex()) {
					AlgebraicTransformations.propagateNegative(other);
				}
				operation.remove();
				zero.remove();

				parent.decase();

				AlgebraActivity.reloadEquationPanel(other.getHTML() + " + 0 = "
						+ other.getHTML(), Rule.ADDITION);

			}
		});
	}
}

/**
 * Numerical Addition, prompt for answer first or just execute if skills are
 * high enough<br/>
 * ex: 1+2=3
 */
class AddNumbersTransform extends AddTransformButton {
	AddNumbersTransform(AdditionTransformations context) {
		super(context, "# + #");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
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

				if (!isMinus) {
					total = leftValue.add(rightValue);
				} else {
					total = leftValue.subtract(rightValue);
				}
				final BigDecimal totalValue = total;

				if (AlgebraActivity.isInEasyMode) {
					addNumbers(left, right, totalValue, leftValue, rightValue);

				} else {// prompt

					String question = leftValue.toString() + " "
							+ operation.getSymbol() + " "
							+ rightValue.toString() + " = ";
					NumberPrompt prompt = new NumberPrompt(question, totalValue) {
						@Override
						void onCorrect() {
							addNumbers(left, right, totalValue, leftValue,
									rightValue);
						}
					};
					prompt.appear();
				}
			}
		});
	}

	private void addNumbers(MathNode left, MathNode right,
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
}

/**
 * xy + xz = x(y + z)<br/>
 * Factors out all like entities within the term or numerator<br/>
 */
class FactorLikeTermsTransform extends AddTransformButton {
	FactorLikeTermsTransform(final AdditionTransformations context,
			final MathNode leftTerm, final MathNode rightTerm,
			final LinkedHashMap<MathNode, MathNode> likeTerms) {
		super(context, "xy + xz = x(y + z)");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
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

				MathNode leftRemaining = leftTerm;
				// Remove operation on like term
				if (likeTerms.size() * 2 - 1 == leftTerm.getChildCount()) {
					leftRemaining = leftTerm.replace(TypeML.Term, "");// will
																		// be
																		// MathNode.decase[d]
					leftRemaining.append(TypeML.Number, "1");
				} else {
					for (MathNode extraFactor : likeTerms.keySet()) {

						if (extraFactor.getIndex() == 0) {
							MathNode nextOp = extraFactor.getNextSibling();
							if (nextOp != null
									&& TypeML.Operation
											.equals(nextOp.getType())) {
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

				MathNode rightRemaining = rightTerm;
				if (likeTerms.size() * 2 - 1 == rightTerm.getChildCount()) {
					rightRemaining = rightTerm.replace(TypeML.Term, "");
					rightRemaining.append(TypeML.Number, "1");
				} else {
					for (MathNode fact : likeTerms.values()) {
						if (fact.getIndex() == 0) {
							MathNode nextOp = fact.getNextSibling();
							if (nextOp != null
									&& TypeML.Operation
											.equals(nextOp.getType())) {
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

				context.factor(factors, leftTerm, rightTerm);

				leftRemaining.decase();
				rightRemaining.decase();

				AlgebraActivity.reloadEquationPanel("Factor Like Terms",
						Rule.COMBINING_LIKE_TERMS);
			}
		});
	}
}

/**
 * x + x<sup>y</sup> = x·(1 + x<sup>y-1</sup>) <br/>
 * Factors out a single multiple of the base and other entity if equal
 */
class FactorBaseTransform extends AddTransformButton {
	FactorBaseTransform(final AdditionTransformations context,
			final MathNode other, final MathNode exponential) {
		super(context, "x + x<sup>y</sup> = x·(1 + x<sup>y-1</sup>)");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				other.highlight();
				operation.highlight();
				exponential.getChildAt(0).highlight();

				MathNode inBinomialB = exponential;
				MathNode inBinomialA = other.getParent().addBefore(
						other.getIndex(), TypeML.Number, "1");

				LinkedList<MathNode> factors = new LinkedList<MathNode>();
				factors.add(other);
				context.factor(factors, inBinomialA, inBinomialB);

				MathNode exp = exponential.getChildAt(1).encase(TypeML.Sum);
				exp.append(TypeML.Operation, Operator.MINUS.getSign());
				exp.append(TypeML.Number, "1");

				AlgebraActivity.reloadEquationPanel("Factor with Base",
						Rule.FACTORIZATION);

			}
		});
	}
}

/**
 * x + (x·y) = x·(1 + y) <br/>
 * Factors out a child of the term if similar to the other entity
 */
class FactorWithTermChildTransform extends AddTransformButton {
	FactorWithTermChildTransform(final AdditionTransformations context,
			final MathNode other, final MathNode term, final MathNode termChild) {
		super(context, "x + (x·y) = x·(1 + y)");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				other.highlight();
				operation.highlight();
				termChild.highlight();

				MathNode inBinomialA = other;
				MathNode inBinomialB = term;

				if (termChild.getIndex() == 0) {
					MathNode nextOp = termChild.getNextSibling();
					if (nextOp != null
							&& TypeML.Operation.equals(nextOp.getType())) {
						nextOp.remove();
					}
				} else {
					MathNode prevOp = termChild.getPrevSibling();
					if (TypeML.Operation.equals(prevOp.getType())) {
						prevOp.remove();
					}
				}

				// Idea: factor a single multiple of a base, other must be
				// base

				// if (Type.Exponential.equals(other.getParentType())) {
				// inBinomialA = other.getParent();
				// MathNode exp = other.getNextSibling().encase(Type.Sum);
				// exp.add(Type.Operation, Operator.MINUS.getSign());
				// exp.add(Type.Number, "1");
				// } else {
				inBinomialA = other.replace(TypeML.Number, "1");
				other.remove();
				// }

				LinkedList<MathNode> factors = new LinkedList<MathNode>();
				factors.add(termChild);
				context.factor(factors, inBinomialA, inBinomialB);

				term.decase();

				AlgebraActivity.reloadEquationPanel("Factor",
						Rule.FACTORIZATION);
			}
		});
	}
}

/**
 * a/b +c/b = (a+c)/b
 */
class AddFractions extends AddTransformButton {
	AddFractions(AdditionTransformations context) {
		super(context, "a/b +c/b = (a+c)/b");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				right.highlight();
				operation.highlight();
				left.highlight();

				if (isMinusBeforeLeft) {
					left.getPrevSibling().setSymbol(Operator.PLUS.getSign());
					AlgebraicTransformations.propagateNegative(left);
				}

				MathNode numeratorCasing = right.getChildAt(0).encase(
						TypeML.Sum);
				numeratorCasing.addBefore(0, operation);
				numeratorCasing.addBefore(0, left.getChildAt(0));

				left.remove();
				parent.decase();

				AlgebraActivity.reloadEquationPanel("Add Fractions",
						Rule.FRACTION_ADDITION);
			}
		});
	}
}

/**
 * log<sub>b</sub>(x) + log<sub>b</sub>(y) = log<sub>b</sub>(x·y)<br/>
 * log<sub>b</sub>(x) - log<sub>b</sub>(y) = log<sub>b</sub>(x/y)
 */
class AddLogsTransform extends AddTransformButton {
	AddLogsTransform(AdditionTransformations context) {
		super(context,
				"log<sub>b</sub>(x) + log<sub>b</sub>(y) = log<sub>b</sub>(x·y)");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				left.highlight();
				right.highlight();

				MathNode newLogChild;
				MathNode leftChild = left.getFirstChild();
				if (isMinus) {
					newLogChild = leftChild.encase(TypeML.Fraction);
				} else {
					newLogChild = leftChild.encase(TypeML.Term);
					newLogChild.append(TypeML.Operation, Operator.getMultiply()
							.getSign());
				}
				newLogChild.append(right.getFirstChild());

				right.remove();
				operation.remove();

				parent.decase();

				AlgebraActivity.reloadEquationPanel("Combine Log",
						Rule.LOGARITHM);
			}
		});
	}
}

/**
 * x+x = 2x
 */
class AddSimilarTramsform extends AddTransformButton {
	AddSimilarTramsform(AdditionTransformations context) {
		super(context, "x+x = 2x");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				right.highlight();
				operation.highlight();
				left.highlight();

				if (!isMinus && !isMinusBeforeLeft) {
					MathNode casing = right.encase(TypeML.Term);
					casing.addBefore(0, TypeML.Operation, Operator
							.getMultiply().getSign());
					casing.addBefore(0, TypeML.Number, "2");
				} else if (isMinus && isMinusBeforeLeft) {
					MathNode casing = right.encase(TypeML.Term);
					casing.addBefore(0, TypeML.Operation, Operator
							.getMultiply().getSign());
					casing.addBefore(0, TypeML.Number, "-2");
				} else if ((isMinus && !isMinusBeforeLeft)
						|| (!isMinus && isMinusBeforeLeft)) {
					// Remove residual operations
					MathNode leftOp = left.getPrevSibling();
					MathNode rightNext = right.getNextSibling();
					if (leftOp != null
							&& TypeML.Operation.equals(leftOp.getType())) {
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
		});
	}
}
