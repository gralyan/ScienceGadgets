package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;

import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.specification.NumberPrompt;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;

public class AdditionTransformations extends
		TransformationList<AddTransformButton> {

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
		addAll(new FactorTransformations(this));

	}

	AddZeroButton addZero_check() {

		if (!TypeEquationXML.Number.equals(leftType)
				&& !TypeEquationXML.Number.equals(rightType)) {
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

	AddNumbersButton addNumbers_check() {

		if (!TypeEquationXML.Number.equals(leftType)
				|| !TypeEquationXML.Number.equals(rightType)) {
			return null;
		}
		return new AddNumbersButton(this);
	}

	AddSimilarButton addSimilar_check() {
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

	AddLogsButton addLogs_check() {
		if (isMinusBeforeLeft) {
			return null;
		}
		if (!TypeEquationXML.Log.equals(leftType)
				|| !TypeEquationXML.Log.equals(rightType)) {
			return null;
		}
		if (left.getAttribute(MathAttribute.LogBase).equals(
				right.getAttribute(MathAttribute.LogBase))) {
			return new AddLogsButton(this);
		} else {
			return null;
		}
	}
}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////
abstract class AddTransformButton extends TransformationButton {
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
	private EquationNode other;
	private EquationNode zero;

	AddZeroButton(AdditionTransformations context, final EquationNode other,
			final EquationNode zero) {
		super(context, "x+0=x");
		this.other = other;
		this.zero = zero;
	}

	@Override
	protected
	void transform() {
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
	}

	@Override
	protected
	void transform() {

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
			// parent.append(TypeML.Variable, " #");

		} else {// prompt
			String question = leftValue.toString() + " "
					+ operation.getSymbol() + " " + rightValue.toString()
					+ " = ";
			NumberPrompt prompt = new NumberPrompt(question, totalValue) {
				@Override
				public void onCorrect() {
					addNumbers(left, right, totalValue, leftValue, rightValue);
				}
			};
			prompt.appear();
		}
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

	}

	@Override
	protected
	void transform() {

		right.highlight();
		operation.highlight();
		left.highlight();

		if (!isMinus && !isMinusBeforeLeft) {
			EquationNode casing = right.encase(TypeEquationXML.Term);
			casing.addFirst(TypeEquationXML.Operation, Operator.getMultiply()
					.getSign());
			casing.addFirst(TypeEquationXML.Number, "2");
		} else if (isMinus && isMinusBeforeLeft) {
			EquationNode casing = right.encase(TypeEquationXML.Term);
			casing.addFirst(TypeEquationXML.Operation, Operator.getMultiply()
					.getSign());
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
	}

	@Override
	protected
	void transform() {

		if (nonFrac != null && fraction != null) {// One Fraction
			nonFrac.highlight();

			EquationNode commonDenominator = fraction.getChildAt(1);

			EquationNode nonFracTerm = nonFrac.encase(TypeEquationXML.Term);
			nonFracTerm.append(TypeEquationXML.Operation, Operator
					.getMultiply().getSign());
			nonFracTerm.append(commonDenominator.clone());

			EquationNode nonFracFraction = nonFracTerm
					.encase(TypeEquationXML.Fraction);
			nonFracFraction.append(commonDenominator.clone());

		} else {// Both left and right are fractions
			left.highlight();
			right.highlight();

			EquationNode commonLeft = left.getChildAt(1).clone();
			EquationNode commonRight = right.getChildAt(1).clone();

			EquationNode leftNumTerm = left.getChildAt(0).encase(
					TypeEquationXML.Term);
			leftNumTerm.append(TypeEquationXML.Operation, Operator
					.getMultiply().getSign());
			leftNumTerm.append(commonRight.clone());

			EquationNode rightNumTerm = right.getChildAt(0).encase(
					TypeEquationXML.Term);
			rightNumTerm.append(TypeEquationXML.Operation, Operator
					.getMultiply().getSign());
			rightNumTerm.append(commonLeft.clone());

			EquationNode leftDenTerm = left.getChildAt(1).encase(
					TypeEquationXML.Term);
			leftDenTerm.append(TypeEquationXML.Operation, Operator
					.getMultiply().getSign());
			leftDenTerm.append(commonRight);

			EquationNode rightDenTerm = right.getChildAt(1).encase(
					TypeEquationXML.Term);
			rightDenTerm.append(TypeEquationXML.Operation, Operator
					.getMultiply().getSign());
			rightDenTerm.append(commonLeft);
		}

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel("Common Denominator",
					Rule.FRACTION_ADDITION);
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
	}

	@Override
	protected
	void transform() {

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
	}

	@Override
	protected
	void transform() {
		left.highlight();
		right.highlight();

		EquationNode newLogChild;
		EquationNode leftChild = left.getFirstChild();
		if (isMinus) {
			newLogChild = leftChild.encase(TypeEquationXML.Fraction);
		} else {
			newLogChild = leftChild.encase(TypeEquationXML.Term);
			newLogChild.append(TypeEquationXML.Operation, Operator
					.getMultiply().getSign());
		}
		newLogChild.append(right.getFirstChild());

		right.remove();
		operation.remove();

		parent.decase();

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel("Combine Log", Rule.LOGARITHM);
		}

	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.addLogs_check();
	}
}
