package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.specification.NumberQuiz;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;

public class AdditionTransformations extends
		TransformationList<AddTransformButton> {

	private static final long serialVersionUID = 1L;

	EquationNode left;
	EquationNode operation;
	EquationNode right;

	EquationNode parent;

	TypeSGET leftType;
	TypeSGET rightType;

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
				&& TypeSGET.Operation.equals(leftPrev.getType())) {
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

		if (!TypeSGET.Number.equals(leftType)
				&& !TypeSGET.Number.equals(rightType)) {
			return null;
		}

		final int same = 0;

		if (TypeSGET.Number.equals(leftType)) {
			BigDecimal leftValue = new BigDecimal(left.getSymbol());
			if (leftValue.compareTo(new BigDecimal(0)) == same) {
				if (!TypeSGET.Number.equals(rightType)
						|| Moderator.meetsRequirement(Badge.ADD_WITH_ZERO)) {
					return new AddZeroButton(this, right, left);
				}
			}
		} else if (TypeSGET.Number.equals(rightType)) {
			BigDecimal rightValue = new BigDecimal(right.getSymbol());
			if (rightValue.compareTo(new BigDecimal(0)) == same) {
				if (!TypeSGET.Number.equals(leftType)
						|| Moderator.meetsRequirement(Badge.ADD_WITH_ZERO)) {
					return new AddZeroButton(this, left, right);
				}
			}
		}
		return null;
	}

	AddNumbersButton addNumbers_check() {

		if (!TypeSGET.Number.equals(leftType)
				|| !TypeSGET.Number.equals(rightType)) {
			return null;
		}
		return new AddNumbersButton(this);
	}

	AddTransformButton addFractions_check() {
		boolean isLeftFraction = TypeSGET.Fraction.equals(leftType);
		boolean isRightFraction = TypeSGET.Fraction.equals(rightType);

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
		if (!TypeSGET.Log.equals(leftType) || !TypeSGET.Log.equals(rightType)) {
			return null;
		}
		if (left.getAttribute(MathAttribute.LogBase).equals(
				right.getAttribute(MathAttribute.LogBase))) {
			return new AddLogsButton(this);
		} else {
			return null;
		}
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

	AddTransformButton(AdditionTransformations context) {
		super(context);
		addStyleName(CSS.SUM + " " + CSS.PARENT_WRAPPER);

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
		super(context);
		this.other = other;
		this.zero = zero;
	}

	@Override
	public String getExampleHTML() {
		return "x + 0 = x<br/>0 + x = x";
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.ADD_WITH_ZERO;
	}

	@Override
	public void transform() {
		operation.highlight();
		zero.highlight();

		if (isMinus && other.getIndex() > zero.getIndex()) {
			AlgebraicTransformations.propagateNegative(other);
		}
		operation.remove();
		zero.remove();

		parent.decase();

		onTransformationEnd(other.getHTMLString(true, true) + " + 0 = "
				+ other.getHTMLString(true, true));
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
	private boolean includesNegatives;

	AddNumbersButton(AdditionTransformations context) {
		super(context);
		this.isEvaluation = true;
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.ADD;
	}

	@Override
	public boolean meetsAutoTransform() {
		return true;
	}

	@Override
	public void transform() {

		if (!left.getUnitAttribute().equals(right.getUnitAttribute())) {
			Window.alert("You can only add quantities with similar units.\nPlease convert first.");
			return;
		}

		BigDecimal leftV = new BigDecimal(left.getSymbol());
		final BigDecimal leftValue = isMinusBeforeLeft ? leftV.negate() : leftV;
		final BigDecimal rightValue = new BigDecimal(right.getSymbol());
		final BigDecimal totalValue = isMinus ? leftValue.subtract(rightValue)
				: leftValue.add(rightValue);

		Skill nMagSkill = Skill.ADD;
		Badge numberMagnitudeBadge = Badge.ADD_NUMBERS_LARGE;
		int totalAbs = totalValue.abs().intValue();
		if (totalAbs <= 10) {
			nMagSkill = Skill.ADD_NUMBERS_10;
			numberMagnitudeBadge = Badge.ADD_NUMBERS_10;
		} else if (totalAbs <= 100) {
			nMagSkill = Skill.ADD_NUMBERS_100;
			numberMagnitudeBadge = Badge.ADD_NUMBERS_100;
		}
		final Skill numberMagnitudeSkill = nMagSkill;

		includesNegatives = isMinusBeforeLeft || leftV.intValue() < 0
				|| rightValue.intValue() < 0;
		BigDecimal zero = new BigDecimal(0);
		final boolean includesZero = zero.compareTo(rightValue) == 0
				|| zero.compareTo(leftValue) == 0;

		HashSet<Badge> badgesRequired = new HashSet<Badge>();
		badgesRequired.add(numberMagnitudeBadge);
		if (includesNegatives) {
			badgesRequired.add(Badge.ADDITION_WITH_NEGATIVES);
		}
		if (isMinus) {
			badgesRequired.add(Badge.SUBTRACTION);
		}
		if (includesZero) {
			badgesRequired.add(Badge.ADD_WITH_ZERO);
		}

		boolean meetsRequirements = Moderator.meetsRequirements(badgesRequired);

		if (meetsRequirements) {
			addNumbers(left, right, totalValue, leftValue, rightValue);

		} else if (!reloadAlgebraActivity) {
//			parent.replace(TypeSGET.Sum, "");
//			parent.append(TypeSGET.Variable, "# ");
//			parent.append(TypeSGET.Variable, operation.getSymbol());
			// parent.append(TypeML.Variable, " #");

		} else {// prompt

			// leftValue was already negated for the calculation, this is for
			// display only
			String leftSideOfQuestion = isMinusBeforeLeft
					&& leftValue.compareTo(zero) > 0 ? "-("
					+ leftValue.negate().toPlainString() + ")" : leftValue
					.toString();
			String question = leftSideOfQuestion + " " + operation.getSymbol()
					+ " " + rightValue.toPlainString();

			final HashMap<Skill, Integer> skillsIncrease = new HashMap<Skill, Integer>();
			skillsIncrease.put(numberMagnitudeSkill, 0);
			if (isMinus) {
				skillsIncrease.put(Skill.SUBTRACTION, 0);
			}
			if (includesNegatives) {
				skillsIncrease.put(Skill.ADDITION_WITH_NEGATIVES, 0);
			}
			if (includesZero) {
				skillsIncrease.put(Skill.ADD_WITH_ZERO, 0);
			}
			if (includesZero) {
				skillsIncrease.put(Skill.ADD_NUMBERS_10, 0);
			}

			NumberQuiz prompt = new NumberQuiz(question, totalValue) {
				@Override
				public void onIncorrect() {
					super.onIncorrect();
					for (Entry<Skill, Integer> entry : skillsIncrease
							.entrySet()) {
						entry.setValue(-1);
					}
					Moderator.increaseSkills(skillsIncrease);
				}

				@Override
				public void onCorrect() {
					super.onCorrect();
					for (Entry<Skill, Integer> entry : skillsIncrease
							.entrySet()) {
						entry.setValue(1);
					}
					Moderator.increaseSkills(skillsIncrease);
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

		onTransformationEnd(leftValue.stripTrailingZeros()
				.toEngineeringString()
				+ " "
				+ operation.toString()
				+ " "
				+ rightValue.stripTrailingZeros().toEngineeringString()
				+ " = "
				+ totalValue.stripTrailingZeros().toEngineeringString());
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
		super(context);

	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.COMBINE_LIKE_TERMS;
	}

	@Override
	public void transform() {

		right.highlight();
		operation.highlight();
		left.highlight();

		if (!isMinus && !isMinusBeforeLeft) {
			EquationNode casing = right.encase(TypeSGET.Term);
			casing.addFirst(TypeSGET.Operation, Operator.getMultiply()
					.getSign());
			casing.addFirst(TypeSGET.Number, "2");
		} else if (isMinus && isMinusBeforeLeft) {
			EquationNode casing = right.encase(TypeSGET.Term);
			casing.addFirst(TypeSGET.Operation, Operator.getMultiply()
					.getSign());
			casing.addFirst(TypeSGET.Number, "-2");
			minusBeforeLeft.setSymbol(Operator.PLUS.getSign());
		} else if ((isMinus && !isMinusBeforeLeft)
				|| (!isMinus && isMinusBeforeLeft)) {
			// Remove residual operations
			EquationNode leftOp = left.getPrevSibling();
			EquationNode rightNext = right.getNextSibling();
			if (leftOp != null && TypeSGET.Operation.equals(leftOp.getType())) {
				leftOp.remove();
			} else if (rightNext != null
					&& TypeSGET.Operation.equals(rightNext.getType())) {
				rightNext.remove();
			}

			right.remove();
		}

		left.remove();
		operation.remove();
		parent.decase();

		onTransformationEnd("Add similar");
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
		super(context);
		setHTML("Common Denominator");
	}
@Override
public String getExampleHTML() {
	return null;
}
	@Override
	public Badge getAssociatedBadge() {
		return Badge.COMMON_DENOMINATOR;
	}

	@Override
	public void transform() {

		if (nonFrac != null && fraction != null) {// One Fraction
			nonFrac.highlight();

			EquationNode commonDenominator = fraction.getChildAt(1);

			EquationNode nonFracTerm = nonFrac.encase(TypeSGET.Term);
			nonFracTerm.append(TypeSGET.Operation, Operator.getMultiply()
					.getSign());
			nonFracTerm.append(commonDenominator.clone());

			EquationNode nonFracFraction = nonFracTerm
					.encase(TypeSGET.Fraction);
			nonFracFraction.append(commonDenominator.clone());

		} else {// Both left and right are fractions
			left.highlight();
			right.highlight();

			EquationNode commonLeft = left.getChildAt(1).clone();
			EquationNode commonRight = right.getChildAt(1).clone();

			EquationNode leftNumTerm = left.getChildAt(0).encase(TypeSGET.Term);
			leftNumTerm.append(TypeSGET.Operation, Operator.getMultiply()
					.getSign());
			leftNumTerm.append(commonRight.clone());

			EquationNode rightNumTerm = right.getChildAt(0).encase(
					TypeSGET.Term);
			rightNumTerm.append(TypeSGET.Operation, Operator.getMultiply()
					.getSign());
			rightNumTerm.append(commonLeft.clone());

			EquationNode leftDenTerm = left.getChildAt(1).encase(TypeSGET.Term);
			leftDenTerm.append(TypeSGET.Operation, Operator.getMultiply()
					.getSign());
			leftDenTerm.append(commonRight);

			EquationNode rightDenTerm = right.getChildAt(1).encase(
					TypeSGET.Term);
			rightDenTerm.append(TypeSGET.Operation, Operator.getMultiply()
					.getSign());
			rightDenTerm.append(commonLeft);
		}

		onTransformationEnd("Common Denominator");
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
		super(context);
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.ADD_FRACTIONS;
	}

	@Override
	public void transform() {

		right.highlight();
		operation.highlight();
		left.highlight();

		if (isMinusBeforeLeft) {
			minusBeforeLeft.setSymbol(Operator.PLUS.getSign());
			AlgebraicTransformations.propagateNegative(left);
		}

		EquationNode numeratorCasing = right.getChildAt(0).encase(TypeSGET.Sum);
		numeratorCasing.addFirst(operation);
		numeratorCasing.addFirst(left.getChildAt(0));

		left.remove();
		parent.decase();

		onTransformationEnd("Add Fractions");
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
		super(context);
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.ADD_LOGS;
	}

	@Override
	public void transform() {
		left.highlight();
		right.highlight();

		EquationNode newLogChild;
		EquationNode leftChild = left.getFirstChild();
		if (isMinus) {
			newLogChild = leftChild.encase(TypeSGET.Fraction);
		} else {
			newLogChild = leftChild.encase(TypeSGET.Term);
			newLogChild.append(TypeSGET.Operation, Operator.getMultiply()
					.getSign());
		}
		newLogChild.append(right.getFirstChild());

		right.remove();
		operation.remove();

		parent.decase();

		onTransformationEnd("Combine Log");

	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.addLogs_check();
	}
}
