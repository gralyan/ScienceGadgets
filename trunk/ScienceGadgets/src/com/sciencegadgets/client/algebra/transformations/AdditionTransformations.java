package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.ChildRequirement;
import com.sciencegadgets.shared.TypeML.Operator;

public class AdditionTransformations extends TransformationList {

	private static final long serialVersionUID = 1L;

	MathNode left;
	MathNode operation;
	MathNode right;

	MathNode parent;

	TypeML leftType;
	TypeML rightType;

	boolean isMinus;
	boolean isMinusBeforeLeft = false;
	MathNode minusBeforeLeft = null;

	public AdditionTransformations(MathNode operation) {
		super(operation);

		this.parent = operation.getParent();

		this.left = operation.getPrevSibling();
		this.operation = operation;
		this.right = operation.getNextSibling();

		this.leftType = left.getType();
		this.rightType = right.getType();

		isMinus = Operator.MINUS.getSign().equals(operation.getSymbol());

		MathNode leftPrev = left.getPrevSibling();
		if (leftPrev != null
				&& Operator.MINUS.getSign().equals(leftPrev.getSymbol())
				&& TypeML.Operation.equals(leftPrev.getType())) {
			isMinusBeforeLeft = true;
			minusBeforeLeft = leftPrev;
		}

		if (add(addZero_check())) {
			return;
		}
		add(addNumbers_check());
		add(addFractions_check());
		add(addLogs_check());
		if (add(addSimilar_check())) {
			return;
		}
//		add(factorLikeTerms_check());
//		add(factorWithBase_check());
//		add(factorWithTermChild_check());

		addAll(new FactorTransformations(this));

	}

	AddTransformButton addZero_check() {

		if (!TypeML.Number.equals(leftType) && !TypeML.Number.equals(rightType)) {
			return null;
		}

		final int same = 0;

		if (TypeML.Number.equals(leftType)) {
			BigDecimal leftValue = new BigDecimal(left.getSymbol());
			if (leftValue.compareTo(new BigDecimal(0)) == same) {
				return new AddZeroButton(this, right, left);
			}
		} else if (TypeML.Number.equals(rightType)) {
			BigDecimal rightValue = new BigDecimal(right.getSymbol());
			if (rightValue.compareTo(new BigDecimal(0)) == same) {
				return new AddZeroButton(this, left, right);
			}
		}
		return null;
	}

	AddTransformButton addNumbers_check() {

		if (!TypeML.Number.equals(leftType) || !TypeML.Number.equals(rightType)) {
			return null;
		}
		return new AddNumbersButton(this);
	}

	AddTransformButton addSimilar_check() {
		if (leftType != rightType) {
			return null;
		}
		if (left.isLike(right)) {
			return new AddSimilarButton(this);
		} else {
			return null;
		}
	}

	AddTransformButton addFractions_check() {
		boolean isLeftFraction = TypeML.Fraction.equals(leftType);
		boolean isRightFraction = TypeML.Fraction.equals(rightType);

		if (!isRightFraction && !isLeftFraction) {
			return null;

		} else if (isRightFraction && isLeftFraction) {
			if (left.getChildAt(1).isLike(right.getChildAt(1))) {
				return new AddFractionsButton(this);// Common denominators
			} else {
				return new ToCommonDenominatorButton(this);
			}

		} else if (isRightFraction) {//
			return new ToCommonDenominatorButton(this, left, right);

		} else if (isLeftFraction) {
			return new ToCommonDenominatorButton(this, right, left);

		} else {
			return null;
		}
	}

	AddTransformButton addLogs_check() {
		if (isMinusBeforeLeft) {
			return null;
		}
		if (!TypeML.Log.equals(leftType) || !TypeML.Log.equals(rightType)) {
			return null;
		}
		if (left.getAttribute(MathAttribute.LogBase).equals(
				right.getAttribute(MathAttribute.LogBase))) {
			return new AddLogsButton(this);
		} else {
			return null;
		}
	}

	/**
	 * xy + xz = x(y + z)<br/>
	 * Factors out all like entities within the term or numerator<br/>
	 */
	AddTransformButton factorLikeTerms_check() {

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

		return new FactorLikeTermsButton(this, leftTerm, rightTerm, likeTerms);
	}

	AddTransformButton factorWithBase_check() {

		if (TypeML.Exponential.equals(leftType)) {
			if (right.isLike(left.getChildAt(0))) {
				return new FactorBaseButton(this, right, left);
			}
		} else if (TypeML.Exponential.equals(rightType)) {
			if (left.isLike(right.getChildAt(0))) {
				return new FactorBaseButton(this, left, right);
			}
		}
		return null;
	}

	AddTransformButton factorWithTermChild_check() {
		// TODO the case where the factor matches the fraction numerator, but
		// it's not in a term
		// ex. a + a/b doesn't work here
		// but a + (a x c)/b does work here
		MathNode term, other;
		JSNICalls.debug(" ");
		JSNICalls.debug("Left " + left.getType()
				+ left.getXMLNode().getInnerText() + " Right "
				+ right.getType() + right.getXMLNode().getInnerText());
		// If sides are fractions, like terms come from numerators
		MathNode leftTerm = getTerm(left);
		MathNode rightTerm = getTerm(right);
		JSNICalls.debug("LeftTerm " + leftTerm);
		JSNICalls.debug("RightTerm " + rightTerm);
		if (leftTerm != null && rightTerm == null) {
			JSNICalls.debug("LeftTerm " + leftTerm.getXMLNode().getInnerText());
			term = leftTerm;
			other = right;
		} else if (leftTerm == null && rightTerm != null) {
			JSNICalls.debug("RightTerm "
					+ rightTerm.getXMLNode().getInnerText());
			term = rightTerm;
			other = left;
		} else {
			return null;
		}

		JSNICalls.debug("----Other " + other.getXMLNode().getInnerText());
		for (final MathNode termChild : term.getChildren()) {
			JSNICalls.debug("--TermChild "
					+ termChild.getXMLNode().getInnerText());
			if (termChild.isLike(other)) {
				JSNICalls.debug("RETURNIJNG ");
				return new FactorWithTermChildButton(this, other, term,
						termChild);
			}
		}
		return null;
	}

	/**
	 * Sub routine to be used at the end of the other factor methods
	 */
	void factor(LinkedList<MathNode> factors, MathNode inBinomialA,
			MathNode inBinomialB, MathNode minusBeforeLeft) {

		MathNode inBinomialFirst = inBinomialA;
		MathNode inBinomialSecond = inBinomialB;
		if (inBinomialA.getIndex() > inBinomialB.getIndex()) {
			inBinomialFirst = inBinomialB;
			inBinomialSecond = inBinomialA;
		}

		if (inBinomialFirst.getChildCount() == 1
				&& !ChildRequirement.TERMINAL.equals(inBinomialFirst.getType()
						.childRequirement())) {
			inBinomialFirst = inBinomialFirst.getFirstChild();
			inBinomialFirst.getParent().decase();
		}
		if (inBinomialSecond.getChildCount() == 1
				&& !ChildRequirement.TERMINAL.equals(inBinomialSecond.getType()
						.childRequirement())) {
			inBinomialSecond = inBinomialSecond.getFirstChild();
			inBinomialSecond.getParent().decase();
		}

		MathNode termCasing = operation.getParent().addBefore(
				operation.getIndex(), TypeML.Term, "");

		if (Moderator.isInEasyMode
				&& TypeML.Number.equals(inBinomialFirst.getType())
				&& TypeML.Number.equals(inBinomialSecond.getType())) {

			BigDecimal firstValue = new BigDecimal(inBinomialFirst.getSymbol());
			BigDecimal secondValue = new BigDecimal(
					inBinomialSecond.getSymbol());
			BigDecimal total = firstValue.add(secondValue);

			termCasing.append(TypeML.Number, "" + total);

			inBinomialFirst.remove();
			operation.remove();
			inBinomialSecond.remove();

			for (MathNode factor : factors) {
				termCasing.append(TypeML.Operation, Operator.getMultiply()
						.getSign());
				termCasing.append(factor);
			}
		} else {

			for (MathNode factor : factors) {
				termCasing.append(factor);
				termCasing.append(TypeML.Operation, Operator.getMultiply()
						.getSign());
			}

			MathNode binomialCasing = termCasing.append(TypeML.Sum, "");

			if (minusBeforeLeft != null) {
				if (minusBeforeLeft.getIndex() != 0) {
					minusBeforeLeft.getParent().addBefore(
							minusBeforeLeft.getIndex(), TypeML.Operation,
							Operator.PLUS.getSign());
				}
				binomialCasing.append(minusBeforeLeft);
			}
			binomialCasing.append(inBinomialFirst);
			binomialCasing.append(operation);
			binomialCasing.append(inBinomialSecond);
		}
		inBinomialFirst.decase();
		inBinomialSecond.decase();
	}

	/**
	 * If the node is of type fraction, numerators are considered the term
	 */
	MathNode getTerm(MathNode node) {
		switch (node.getType()) {
		case Fraction:
			if (TypeML.Term.equals(node.getChildAt(0).getType())) {
				return node.getChildAt(0);
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
class AddTransformButton extends TransformationButton {
	MathNode left;
	MathNode right;
	MathNode operation;
	MathNode parent;
	MathNode grandParent;
	MathNode minusBeforeLeft;
	final boolean isMinus;
	final boolean isMinusBeforeLeft;

	protected boolean reloadAlgebraActivity;
	protected AdditionTransformations previewContext;

	AddTransformButton(AdditionTransformations context, String html) {
		super(html, context);
		addStyleName(CSS.SUM + " " + CSS.DISPLAY_WRAPPER);

		this.isMinus = context.isMinus;
		this.isMinusBeforeLeft = context.isMinusBeforeLeft;
		this.minusBeforeLeft = context.minusBeforeLeft;

		this.left = context.left;
		this.right = context.right;
		this.operation = context.operation;
		this.parent = context.parent;
		this.grandParent = this.parent.getParent();

		this.reloadAlgebraActivity = context.reloadAlgebraActivity;
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		previewContext = new AdditionTransformations(operation);
		previewContext.reloadAlgebraActivity = false;
		return null;
	}
}

/**
 * x + 0 = x<br/>
 * 0 + x = x
 */
class AddZeroButton extends AddTransformButton {
	AddZeroButton(AdditionTransformations context, final MathNode other,
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

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel(other.getHTML() + " + 0 = "
							+ other.getHTML(), Rule.ADDITION);
				}

			}
		});
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.addZero_check();
	}
}

/**
 * Numerical Addition, prompt for answer first or just execute if skills are
 * high enough<br/>
 * ex: 1+2=3
 */
class AddNumbersButton extends AddTransformButton {
	AddNumbersButton(AdditionTransformations context) {
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

				if (Moderator.isInEasyMode) {
					addNumbers(left, right, totalValue, leftValue, rightValue);

				} else if (!reloadAlgebraActivity) {
					parent.replace(TypeML.Variable,
							"# " + operation.getSymbol() + " #");

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
			minusBeforeLeft.setSymbol(Operator.PLUS.getSign());
		}

		left.remove();
		operation.remove();
		parent.decase();

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel(leftValue.stripTrailingZeros()
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

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.addNumbers_check();
	}
}

/**
 * x+x = 2x<br/>
 * -x-x = -2x<br/>
 * x-x = 0<br/>
 * -x+x = 0<br/>
 */
class AddSimilarButton extends AddTransformButton {
	AddSimilarButton(AdditionTransformations context) {
		super(context, "x+x = 2x");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				right.highlight();
				operation.highlight();
				left.highlight();

				if (!isMinus && !isMinusBeforeLeft) {
					MathNode casing = right.encase(TypeML.Term);
					casing.addFirst(TypeML.Operation, Operator
							.getMultiply().getSign());
					casing.addFirst(TypeML.Number, "2");
				} else if (isMinus && isMinusBeforeLeft) {
					MathNode casing = right.encase(TypeML.Term);
					casing.addFirst(TypeML.Operation, Operator
							.getMultiply().getSign());
					casing.addFirst(TypeML.Number, "-2");
					minusBeforeLeft.setSymbol(Operator.PLUS.getSign());
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

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("Add similar",
							Rule.COMBINING_LIKE_TERMS);
				}
			}
		});
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.addSimilar_check();
	}
}

/**
 * x + a/b = xb/b + a/b<br/>
 * a/b + x = a/b + xb/b<br/>
 * a/b + x/y = ay/by + xb/by<br/>
 */
class ToCommonDenominatorButton extends AddTransformButton {
	MathNode nonFrac = null;
	MathNode fraction = null;

	ToCommonDenominatorButton(AdditionTransformations context,
			MathNode nonFrac, MathNode fraction) {
		this(context);
		this.nonFrac = nonFrac;
		this.fraction = fraction;
	}

	ToCommonDenominatorButton(AdditionTransformations context) {
		super(context, "Common Denominator");
		addClickHandler(new ToCommonDenominatorClickHandler());
	}

	class ToCommonDenominatorClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {

			if (nonFrac != null && fraction != null) {// One Fraction
				nonFrac.highlight();

				MathNode commonDenominator = fraction.getChildAt(1);

				MathNode nonFracTerm = nonFrac.encase(TypeML.Term);
				nonFracTerm.append(TypeML.Operation, Operator.getMultiply()
						.getSign());
				nonFracTerm.append(commonDenominator.clone());

				MathNode nonFracFraction = nonFracTerm.encase(TypeML.Fraction);
				nonFracFraction.append(commonDenominator.clone());

			} else {// Both left and right are fractions
				left.highlight();
				right.highlight();

				MathNode commonLeft = left.getChildAt(1).clone();
				MathNode commonRight = right.getChildAt(1).clone();

				MathNode leftNumTerm = left.getChildAt(0).encase(TypeML.Term);
				leftNumTerm.append(TypeML.Operation, Operator.getMultiply()
						.getSign());
				leftNumTerm.append(commonRight.clone());

				MathNode rightNumTerm = right.getChildAt(0).encase(TypeML.Term);
				rightNumTerm.append(TypeML.Operation, Operator.getMultiply()
						.getSign());
				rightNumTerm.append(commonLeft.clone());

				MathNode leftDenTerm = left.getChildAt(1).encase(TypeML.Term);
				leftDenTerm.append(TypeML.Operation, Operator.getMultiply()
						.getSign());
				leftDenTerm.append(commonRight);

				MathNode rightDenTerm = right.getChildAt(1).encase(TypeML.Term);
				rightDenTerm.append(TypeML.Operation, Operator.getMultiply()
						.getSign());
				rightDenTerm.append(commonLeft);
			}

			if (reloadAlgebraActivity) {
				Moderator.reloadEquationPanel("Common Denominator",
						Rule.FRACTION_ADDITION);
			}
		}

	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.addFractions_check();
	}
}

/**
 * a/b +c/b = (a+c)/b
 */
class AddFractionsButton extends AddTransformButton {
	AddFractionsButton(AdditionTransformations context) {
		super(context, "a/b +c/b = (a+c)/b");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				right.highlight();
				operation.highlight();
				left.highlight();

				if (isMinusBeforeLeft) {
					minusBeforeLeft.setSymbol(Operator.PLUS.getSign());
					AlgebraicTransformations.propagateNegative(left);
				}

				MathNode numeratorCasing = right.getChildAt(0).encase(
						TypeML.Sum);
				numeratorCasing.addFirst(operation);
				numeratorCasing.addFirst(left.getChildAt(0));

				left.remove();
				parent.decase();

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("Add Fractions",
							Rule.FRACTION_ADDITION);
				}
			}
		});
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.addFractions_check();
	}
}

/**
 * log<sub>b</sub>(x) + log<sub>b</sub>(y) = log<sub>b</sub>(x &middot; y)<br/>
 * log<sub>b</sub>(x) - log<sub>b</sub>(y) = log<sub>b</sub>(x/y)
 */
class AddLogsButton extends AddTransformButton {
	AddLogsButton(AdditionTransformations context) {
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

				if (reloadAlgebraActivity) {
					Moderator
							.reloadEquationPanel("Combine Log", Rule.LOGARITHM);
				}
			}
		});
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.addLogs_check();
	}
}

/**
 * xy + xz = x(y + z)<br/>
 * Factors out all like entities within the term or numerator<br/>
 */
class FactorLikeTermsButton extends AddTransformButton {
	FactorLikeTermsButton(final AdditionTransformations context,
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
					// will be MathNode.decase[d]
					leftRemaining = leftTerm.replace(TypeML.Term, "");
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

				context.factor(factors, leftRemaining, rightRemaining,
						minusBeforeLeft);

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("Factor Like Terms",
							Rule.COMBINING_LIKE_TERMS);
				}
			}
		});
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.factorLikeTerms_check();
	}
}

/**
 * x + x<sup>y</sup> = x &middot; (1 + x<sup>y-1</sup>) <br/>
 * Factors out a single multiple of the base and other entity if equal
 */
class FactorBaseButton extends AddTransformButton {
	FactorBaseButton(final AdditionTransformations context,
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
				context.factor(factors, inBinomialA, inBinomialB,
						minusBeforeLeft);

				MathNode exp = exponential.getChildAt(1);
				if (TypeML.Number.equals(exp.getType())
						&& Moderator.isInEasyMode) {
					BigDecimal expValue = new BigDecimal(exp.getSymbol());
					exp.setSymbol(expValue.subtract(new BigDecimal("1")) + "");
				} else {
					exp = exp.encase(TypeML.Sum);
					exp.append(TypeML.Operation, Operator.MINUS.getSign());
					exp.append(TypeML.Number, "1");
				}

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("Factor with Base",
							Rule.FACTORIZATION);
				}

			}
		});
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.factorWithBase_check();
	}
}

/**
 * x + (x &middot; y) = x &middot; (1 + y) <br/>
 * Factors out a child of the term if similar to the other entity
 */
class FactorWithTermChildButton extends AddTransformButton {
	FactorWithTermChildButton(final AdditionTransformations context,
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

				// TODO The case where a term child and an exponential base
				// would be factored out together

				inBinomialA = other.replace(TypeML.Number, "1");

				LinkedList<MathNode> factors = new LinkedList<MathNode>();
				factors.add(termChild);
				context.factor(factors, inBinomialA, inBinomialB,
						minusBeforeLeft);

				// term.decase();

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("Factor", Rule.FACTORIZATION);
				}
			}
		});
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.factorWithTermChild_check();
	}
}
