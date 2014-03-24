package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.specification.NumberPrompt;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.ChildRequirement;
import com.sciencegadgets.shared.TypeEquationXML.Operator;

public class AdditionTransformations extends TransformationList {

	private static final long serialVersionUID = 1L;

	EquationNode left;
	EquationNode operation;
	EquationNode right;

	EquationNode parent;

	TypeEquationXML leftType;
	TypeEquationXML rightType;

	boolean isMinus;
	boolean isMinusBeforeLeft = false;
	EquationNode minusBeforeLeft = null;

	public AdditionTransformations(EquationNode operation) {
		super(operation);

		this.parent = operation.getParent();

		this.left = operation.getPrevSibling();
		this.operation = operation;
		this.right = operation.getNextSibling();

		this.leftType = left.getType();
		this.rightType = right.getType();

		isMinus = Operator.MINUS.getSign().equals(operation.getSymbol());

		EquationNode leftPrev = left.getPrevSibling();
		if (leftPrev != null
				&& Operator.MINUS.getSign().equals(leftPrev.getSymbol())
				&& TypeEquationXML.Operation.equals(leftPrev.getType())) {
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

		if (!TypeEquationXML.Number.equals(leftType) && !TypeEquationXML.Number.equals(rightType)) {
			return null;
		}

		final int same = 0;

		if (TypeEquationXML.Number.equals(leftType)) {
			BigDecimal leftValue = new BigDecimal(left.getSymbol());
			if (leftValue.compareTo(new BigDecimal(0)) == same) {
				return new AddZeroButton(this, right, left);
			}
		} else if (TypeEquationXML.Number.equals(rightType)) {
			BigDecimal rightValue = new BigDecimal(right.getSymbol());
			if (rightValue.compareTo(new BigDecimal(0)) == same) {
				return new AddZeroButton(this, left, right);
			}
		}
		return null;
	}

	AddTransformButton addNumbers_check() {

		if (!TypeEquationXML.Number.equals(leftType) || !TypeEquationXML.Number.equals(rightType)) {
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
		boolean isLeftFraction = TypeEquationXML.Fraction.equals(leftType);
		boolean isRightFraction = TypeEquationXML.Fraction.equals(rightType);

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
		if (!TypeEquationXML.Log.equals(leftType) || !TypeEquationXML.Log.equals(rightType)) {
			return null;
		}
		if (left.getAttribute(MathAttribute.LogBase).equals(
				right.getAttribute(MathAttribute.LogBase))) {
			return new AddLogsButton(this);
		} else {
			return null;
		}
	}

//	/**
//	 * xy + xz = x(y + z)<br/>
//	 * Factors out all like entities within the term or numerator<br/>
//	 */
//	AddTransformButton factorLikeTerms_check() {
//
//		// If sides are fractions, like terms come from numerators
//		EquationNode leftTerm = getTerm(left);
//		if (leftTerm == null) {
//			return null;
//		}
//		EquationNode rightTerm = getTerm(right);
//		if (rightTerm == null) {
//			return null;
//		}
//
//		// Collect like terms
//		final LinkedHashMap<EquationNode, EquationNode> likeTerms = new LinkedHashMap<EquationNode, EquationNode>();
//		a: for (EquationNode leftChild : leftTerm.getChildren()) {
//			if (TypeEquationXML.Operation.equals(leftChild.getType())) {
//				continue a;
//			}
//			b: for (EquationNode rightChild : rightTerm.getChildren()) {
//				if (likeTerms.containsValue(rightChild)
//						|| TypeEquationXML.Operation.equals(rightChild.getType())) {
//					continue b;
//				}
//				if (leftChild.isLike(rightChild)) {
//					likeTerms.put(leftChild, rightChild);
//					continue a;
//				}
//			}
//		}
//		// Check
//		if (likeTerms.size() == 0) {
//			return null;
//		}
//
//		String factorString = "";
//		for (EquationNode fact : likeTerms.keySet()) {
//			factorString.concat(fact.getHTMLString(true, true));
//		}
//
//		return new FactorLikeTermsButton(this, leftTerm, rightTerm, likeTerms);
//	}
//
//	AddTransformButton factorWithBase_check() {
//
//		if (TypeEquationXML.Exponential.equals(leftType)) {
//			if (right.isLike(left.getChildAt(0))) {
//				return new FactorBaseButton(this, right, left);
//			}
//		} else if (TypeEquationXML.Exponential.equals(rightType)) {
//			if (left.isLike(right.getChildAt(0))) {
//				return new FactorBaseButton(this, left, right);
//			}
//		}
//		return null;
//	}
//
//	AddTransformButton factorWithTermChild_check() {
//		// TODO the case where the factor matches the fraction numerator, but
//		// it's not in a term
//		// ex. a + a/b doesn't work here
//		// but a + (a x c)/b does work here
//		EquationNode term, other;
//		JSNICalls.debug(" ");
//		JSNICalls.debug("Left " + left.getType()
//				+ left.getXMLNode().getInnerText() + " Right "
//				+ right.getType() + right.getXMLNode().getInnerText());
//		// If sides are fractions, like terms come from numerators
//		EquationNode leftTerm = getTerm(left);
//		EquationNode rightTerm = getTerm(right);
//		JSNICalls.debug("LeftTerm " + leftTerm);
//		JSNICalls.debug("RightTerm " + rightTerm);
//		if (leftTerm != null && rightTerm == null) {
//			JSNICalls.debug("LeftTerm " + leftTerm.getXMLNode().getInnerText());
//			term = leftTerm;
//			other = right;
//		} else if (leftTerm == null && rightTerm != null) {
//			JSNICalls.debug("RightTerm "
//					+ rightTerm.getXMLNode().getInnerText());
//			term = rightTerm;
//			other = left;
//		} else {
//			return null;
//		}
//
//		JSNICalls.debug("----Other " + other.getXMLNode().getInnerText());
//		for (final EquationNode termChild : term.getChildren()) {
//			JSNICalls.debug("--TermChild "
//					+ termChild.getXMLNode().getInnerText());
//			if (termChild.isLike(other)) {
//				JSNICalls.debug("RETURNIJNG ");
//				return new FactorWithTermChildButton(this, other, term,
//						termChild);
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * Sub routine to be used at the end of the other factor methods
//	 */
//	void factor(LinkedList<EquationNode> factors, EquationNode inBinomialA,
//			EquationNode inBinomialB, EquationNode minusBeforeLeft) {
//
//		EquationNode inBinomialFirst = inBinomialA;
//		EquationNode inBinomialSecond = inBinomialB;
//		if (inBinomialA.getIndex() > inBinomialB.getIndex()) {
//			inBinomialFirst = inBinomialB;
//			inBinomialSecond = inBinomialA;
//		}
//
//		if (inBinomialFirst.getChildCount() == 1
//				&& !ChildRequirement.TERMINAL.equals(inBinomialFirst.getType()
//						.childRequirement())) {
//			inBinomialFirst = inBinomialFirst.getFirstChild();
//			inBinomialFirst.getParent().decase();
//		}
//		if (inBinomialSecond.getChildCount() == 1
//				&& !ChildRequirement.TERMINAL.equals(inBinomialSecond.getType()
//						.childRequirement())) {
//			inBinomialSecond = inBinomialSecond.getFirstChild();
//			inBinomialSecond.getParent().decase();
//		}
//
//		EquationNode termCasing = operation.getParent().addBefore(
//				operation.getIndex(), TypeEquationXML.Term, "");
//
//		if (Moderator.isInEasyMode
//				&& TypeEquationXML.Number.equals(inBinomialFirst.getType())
//				&& TypeEquationXML.Number.equals(inBinomialSecond.getType())) {
//
//			BigDecimal firstValue = new BigDecimal(inBinomialFirst.getSymbol());
//			BigDecimal secondValue = new BigDecimal(
//					inBinomialSecond.getSymbol());
//			BigDecimal total = firstValue.add(secondValue);
//
//			termCasing.append(TypeEquationXML.Number, "" + total);
//
//			inBinomialFirst.remove();
//			operation.remove();
//			inBinomialSecond.remove();
//
//			for (EquationNode factor : factors) {
//				termCasing.append(TypeEquationXML.Operation, Operator.getMultiply()
//						.getSign());
//				termCasing.append(factor);
//			}
//		} else {
//
//			for (EquationNode factor : factors) {
//				termCasing.append(factor);
//				termCasing.append(TypeEquationXML.Operation, Operator.getMultiply()
//						.getSign());
//			}
//
//			EquationNode binomialCasing = termCasing.append(TypeEquationXML.Sum, "");
//
//			if (minusBeforeLeft != null) {
//				if (minusBeforeLeft.getIndex() != 0) {
//					minusBeforeLeft.getParent().addBefore(
//							minusBeforeLeft.getIndex(), TypeEquationXML.Operation,
//							Operator.PLUS.getSign());
//				}
//				binomialCasing.append(minusBeforeLeft);
//			}
//			binomialCasing.append(inBinomialFirst);
//			binomialCasing.append(operation);
//			binomialCasing.append(inBinomialSecond);
//		}
//		inBinomialFirst.decase();
//		inBinomialSecond.decase();
//	}
//
//	/**
//	 * If the node is of type fraction, numerators are considered the term
//	 */
//	EquationNode getTerm(EquationNode node) {
//		switch (node.getType()) {
//		case Fraction:
//			if (TypeEquationXML.Term.equals(node.getChildAt(0).getType())) {
//				return node.getChildAt(0);
//			}
//			return null;
//		case Term:
//			return node;
//		default:
//			return null;
//		}
//	}
}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////
class AddTransformButton extends TransformationButton {
	EquationNode left;
	EquationNode right;
	EquationNode operation;
	EquationNode parent;
	EquationNode grandParent;
	EquationNode minusBeforeLeft;
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
	TransformationButton getPreviewButton(EquationNode operation) {
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
	AddZeroButton(AdditionTransformations context, final EquationNode other,
			final EquationNode zero) {
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
					Moderator.reloadEquationPanel(other.getHTML(true, true) + " + 0 = "
							+ other.getHTML(true, true), Rule.ADDITION);
				}

			}
		});
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
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
					parent.replace(TypeEquationXML.Sum, "");
					parent.append(TypeEquationXML.Variable, "# ");
					parent.append(TypeEquationXML.Variable, operation.getSymbol());
//					parent.append(TypeML.Variable, " #");

				} else {// prompt
					String question = leftValue.toString() + " "
							+ operation.getSymbol() + " "
							+ rightValue.toString() + " = ";
					NumberPrompt prompt = new NumberPrompt(question, totalValue) {
						@Override
						public void onCorrect() {
							addNumbers(left, right, totalValue, leftValue,
									rightValue);
						}
					};
					prompt.appear();
				}
			}
		});
	}

	private void addNumbers(EquationNode left, EquationNode right,
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
	TransformationButton getPreviewButton(EquationNode operation) {
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
					EquationNode casing = right.encase(TypeEquationXML.Term);
					casing.addFirst(TypeEquationXML.Operation, Operator
							.getMultiply().getSign());
					casing.addFirst(TypeEquationXML.Number, "2");
				} else if (isMinus && isMinusBeforeLeft) {
					EquationNode casing = right.encase(TypeEquationXML.Term);
					casing.addFirst(TypeEquationXML.Operation, Operator
							.getMultiply().getSign());
					casing.addFirst(TypeEquationXML.Number, "-2");
					minusBeforeLeft.setSymbol(Operator.PLUS.getSign());
				} else if ((isMinus && !isMinusBeforeLeft)
						|| (!isMinus && isMinusBeforeLeft)) {
					// Remove residual operations
					EquationNode leftOp = left.getPrevSibling();
					EquationNode rightNext = right.getNextSibling();
					if (leftOp != null
							&& TypeEquationXML.Operation.equals(leftOp.getType())) {
						leftOp.remove();
					} else if (rightNext != null
							&& TypeEquationXML.Operation.equals(rightNext.getType())) {
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
	TransformationButton getPreviewButton(EquationNode operation) {
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
	EquationNode nonFrac = null;
	EquationNode fraction = null;

	ToCommonDenominatorButton(AdditionTransformations context,
			EquationNode nonFrac, EquationNode fraction) {
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

				EquationNode commonDenominator = fraction.getChildAt(1);

				EquationNode nonFracTerm = nonFrac.encase(TypeEquationXML.Term);
				nonFracTerm.append(TypeEquationXML.Operation, Operator.getMultiply()
						.getSign());
				nonFracTerm.append(commonDenominator.clone());

				EquationNode nonFracFraction = nonFracTerm.encase(TypeEquationXML.Fraction);
				nonFracFraction.append(commonDenominator.clone());

			} else {// Both left and right are fractions
				left.highlight();
				right.highlight();

				EquationNode commonLeft = left.getChildAt(1).clone();
				EquationNode commonRight = right.getChildAt(1).clone();

				EquationNode leftNumTerm = left.getChildAt(0).encase(TypeEquationXML.Term);
				leftNumTerm.append(TypeEquationXML.Operation, Operator.getMultiply()
						.getSign());
				leftNumTerm.append(commonRight.clone());

				EquationNode rightNumTerm = right.getChildAt(0).encase(TypeEquationXML.Term);
				rightNumTerm.append(TypeEquationXML.Operation, Operator.getMultiply()
						.getSign());
				rightNumTerm.append(commonLeft.clone());

				EquationNode leftDenTerm = left.getChildAt(1).encase(TypeEquationXML.Term);
				leftDenTerm.append(TypeEquationXML.Operation, Operator.getMultiply()
						.getSign());
				leftDenTerm.append(commonRight);

				EquationNode rightDenTerm = right.getChildAt(1).encase(TypeEquationXML.Term);
				rightDenTerm.append(TypeEquationXML.Operation, Operator.getMultiply()
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
	TransformationButton getPreviewButton(EquationNode operation) {
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

				EquationNode numeratorCasing = right.getChildAt(0).encase(
						TypeEquationXML.Sum);
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
	TransformationButton getPreviewButton(EquationNode operation) {
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

				EquationNode newLogChild;
				EquationNode leftChild = left.getFirstChild();
				if (isMinus) {
					newLogChild = leftChild.encase(TypeEquationXML.Fraction);
				} else {
					newLogChild = leftChild.encase(TypeEquationXML.Term);
					newLogChild.append(TypeEquationXML.Operation, Operator.getMultiply()
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
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.addLogs_check();
	}
}

///**
// * xy + xz = x(y + z)<br/>
// * Factors out all like entities within the term or numerator<br/>
// */
//class FactorLikeTermsButton extends AddTransformButton {
//	FactorLikeTermsButton(final AdditionTransformations context,
//			final EquationNode leftTerm, final EquationNode rightTerm,
//			final LinkedHashMap<EquationNode, EquationNode> likeTerms) {
//		super(context, "xy + xz = x(y + z)");
//
//		addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent arg0) {
//				// Highlight terms in AlgOut
//				for (EquationNode key : likeTerms.keySet()) {
//					likeTerms.get(key).highlight();
//					key.highlight();
//				}
//
//				// Like terms are cloned to simplify cleanup
//				LinkedList<EquationNode> factors = new LinkedList<EquationNode>();
//				for (EquationNode factor : likeTerms.values()) {
//					factors.add(factor.clone());
//				}
//
//				EquationNode leftRemaining = leftTerm;
//				// Remove operation on like term
//				if (likeTerms.size() * 2 - 1 == leftTerm.getChildCount()) {
//					// will be MathNode.decase[d]
//					leftRemaining = leftTerm.replace(TypeEquationXML.Term, "");
//					leftRemaining.append(TypeEquationXML.Number, "1");
//				} else {
//					for (EquationNode extraFactor : likeTerms.keySet()) {
//
//						if (extraFactor.getIndex() == 0) {
//							EquationNode nextOp = extraFactor.getNextSibling();
//							if (nextOp != null
//									&& TypeEquationXML.Operation
//											.equals(nextOp.getType())) {
//								nextOp.remove();
//							}
//						} else {
//							EquationNode prevOp = extraFactor.getPrevSibling();
//							if (TypeEquationXML.Operation.equals(prevOp.getType())) {
//								prevOp.remove();
//							}
//						}
//						extraFactor.remove();
//					}
//				}
//
//				EquationNode rightRemaining = rightTerm;
//				if (likeTerms.size() * 2 - 1 == rightTerm.getChildCount()) {
//					rightRemaining = rightTerm.replace(TypeEquationXML.Term, "");
//					rightRemaining.append(TypeEquationXML.Number, "1");
//				} else {
//					for (EquationNode fact : likeTerms.values()) {
//						if (fact.getIndex() == 0) {
//							EquationNode nextOp = fact.getNextSibling();
//							if (nextOp != null
//									&& TypeEquationXML.Operation
//											.equals(nextOp.getType())) {
//								nextOp.remove();
//							}
//						} else {
//							EquationNode prevOp = fact.getPrevSibling();
//							if (TypeEquationXML.Operation.equals(prevOp.getType())) {
//								prevOp.remove();
//							}
//						}
//						fact.remove();
//					}
//				}
//
//				context.factor(factors, leftRemaining, rightRemaining,
//						minusBeforeLeft);
//
//				if (reloadAlgebraActivity) {
//					Moderator.reloadEquationPanel("Factor Like Terms",
//							Rule.COMBINING_LIKE_TERMS);
//				}
//			}
//		});
//	}
//
//	@Override
//	TransformationButton getPreviewButton(EquationNode operation) {
//		super.getPreviewButton(operation);
//		return previewContext.factorLikeTerms_check();
//	}
//}
//
///**
// * x + x<sup>y</sup> = x &middot; (1 + x<sup>y-1</sup>) <br/>
// * Factors out a single multiple of the base and other entity if equal
// */
//class FactorBaseButton extends AddTransformButton {
//	FactorBaseButton(final AdditionTransformations context,
//			final EquationNode other, final EquationNode exponential) {
//		super(context, "x + x<sup>y</sup> = x·(1 + x<sup>y-1</sup>)");
//
//		addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent arg0) {
//				other.highlight();
//				operation.highlight();
//				exponential.getChildAt(0).highlight();
//
//				EquationNode inBinomialB = exponential;
//				EquationNode inBinomialA = other.getParent().addBefore(
//						other.getIndex(), TypeEquationXML.Number, "1");
//
//				LinkedList<EquationNode> factors = new LinkedList<EquationNode>();
//				factors.add(other);
//				context.factor(factors, inBinomialA, inBinomialB,
//						minusBeforeLeft);
//
//				EquationNode exp = exponential.getChildAt(1);
//				if (TypeEquationXML.Number.equals(exp.getType())
//						&& Moderator.isInEasyMode) {
//					BigDecimal expValue = new BigDecimal(exp.getSymbol());
//					exp.setSymbol(expValue.subtract(new BigDecimal("1")) + "");
//				} else {
//					exp = exp.encase(TypeEquationXML.Sum);
//					exp.append(TypeEquationXML.Operation, Operator.MINUS.getSign());
//					exp.append(TypeEquationXML.Number, "1");
//				}
//
//				if (reloadAlgebraActivity) {
//					Moderator.reloadEquationPanel("Factor with Base",
//							Rule.FACTORIZATION);
//				}
//
//			}
//		});
//	}
//
//	@Override
//	TransformationButton getPreviewButton(EquationNode operation) {
//		super.getPreviewButton(operation);
//		return previewContext.factorWithBase_check();
//	}
//}
//
///**
// * x + (x &middot; y) = x &middot; (1 + y) <br/>
// * Factors out a child of the term if similar to the other entity
// */
//class FactorWithTermChildButton extends AddTransformButton {
//	FactorWithTermChildButton(final AdditionTransformations context,
//			final EquationNode other, final EquationNode term, final EquationNode termChild) {
//		super(context, "x + (x·y) = x·(1 + y)");
//
//		addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent arg0) {
//				other.highlight();
//				operation.highlight();
//				termChild.highlight();
//
//				EquationNode inBinomialA = other;
//				EquationNode inBinomialB = term;
//
//				if (termChild.getIndex() == 0) {
//					EquationNode nextOp = termChild.getNextSibling();
//					if (nextOp != null
//							&& TypeEquationXML.Operation.equals(nextOp.getType())) {
//						nextOp.remove();
//					}
//				} else {
//					EquationNode prevOp = termChild.getPrevSibling();
//					if (TypeEquationXML.Operation.equals(prevOp.getType())) {
//						prevOp.remove();
//					}
//				}
//
//				// TODO The case where a term child and an exponential base
//				// would be factored out together
//
//				inBinomialA = other.replace(TypeEquationXML.Number, "1");
//
//				LinkedList<EquationNode> factors = new LinkedList<EquationNode>();
//				factors.add(termChild);
//				context.factor(factors, inBinomialA, inBinomialB,
//						minusBeforeLeft);
//
//				// term.decase();
//
//				if (reloadAlgebraActivity) {
//					Moderator.reloadEquationPanel("Factor", Rule.FACTORIZATION);
//				}
//			}
//		});
//	}
//
//	@Override
//	TransformationButton getPreviewButton(EquationNode operation) {
//		super.getPreviewButton(operation);
//		return previewContext.factorWithTermChild_check();
//	}
//}
