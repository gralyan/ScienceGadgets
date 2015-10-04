/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.specification.NumberQuiz;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitMap;

public class MultiplyTransformations extends
		TransformationList<MultiplyTransformButton> {

	private static final long serialVersionUID = 3127633894356779264L;

	EquationNode left;
	EquationNode operation;
	EquationNode right;
	EquationNode parent;

	TypeSGET leftType;
	TypeSGET rightType;

	public MultiplyTransformations(EquationNode multiplyNode) {
		super(multiplyNode);

		this.parent = multiplyNode.getParent();

		this.left = multiplyNode.getPrevSibling();
		this.operation = multiplyNode;
		this.right = multiplyNode.getNextSibling();

		this.leftType = left.getType();
		this.rightType = right.getType();

		if (add(multiplySpecialNumber_check())) {
			// return;
		}
		add(multiplyNumbers_check());
		add(multiplyFraction_check());
		add(multiplyDistribution_check());
		MultiplyCombineBasesButton[] multiplyCombineBases = multiplyCombineBases_check();
		add(multiplyCombineBases[0]);
		add(multiplyCombineBases[1]);
		add(multiplyCombineExponents_check());
		add(multiplyLogRule_check());

	}

	/**
	 * Multiplying by one of the specially designated numbers (-1, 0 1)
	 */
	MultiplyTransformButton multiplySpecialNumber_check() {

		if (!TypeSGET.Number.equals(leftType)
				&& !TypeSGET.Number.equals(rightType)) {
			return null;
		}

		String rightValue = right.getSymbol();
		String rightUnits = right.getAttribute(MathAttribute.Unit);
		if ("0".equals(rightValue)) {
			return new MultiplyZeroButton(this, left, right);
		} else if ("1".equals(rightValue) && "".equals(rightUnits)) {
			return new MultiplyOneButton(this, left, right);
		} else if ("-1".equals(rightValue) && "".equals(rightUnits)) {
			switch (left.getType()) {
			case Number:
			case Variable:
			case Sum:
			case Term:
			case Fraction:
				return new MultiplyNegOneButton(this, left, right);
			}
		}

		String leftValue = left.getSymbol();
		String leftUnits = right.getAttribute(MathAttribute.Unit);
		if ("0".equals(leftValue)) {
			return new MultiplyZeroButton(this, right, left);
		} else if ("1".equals(leftValue) && "".equals(leftUnits)) {
			return new MultiplyOneButton(this, right, left);
		} else if ("-1".equals(leftValue) && "".equals(leftUnits)) {
			switch (right.getType()) {
			case Number:
			case Variable:
			case Sum:
			case Term:
			case Fraction:
				return new MultiplyNegOneButton(this, right, left);
			}
		}
		return null;
	}

	/**
	 * Multiply two numbers, prompts for the product unless the user skill level
	 * is high enough
	 */
	MultiplyNumbersButton multiplyNumbers_check() {

		if (!(TypeSGET.Number.equals(leftType) && TypeSGET.Number
				.equals(rightType))) {
			return null;
		}
		try {
			new BigDecimal(left.getSymbol());
			new BigDecimal(right.getSymbol());
		}catch(NumberFormatException e) {
			return null;
		}
		
		return new MultiplyNumbersButton(this);
	}

	/**
	 * x &middot; (y+z) = xy + xz
	 */
	MultiplyDistributionButton multiplyDistribution_check() {

		if (TypeSGET.Sum.equals(rightType)) {
			return new MultiplyDistributionButton(this, left, right, true);
		} else if (TypeSGET.Sum.equals(leftType)) {
			return new MultiplyDistributionButton(this, right, left, false);
		} else {
			return null;
		}
	}

	/**
	 * x<sup>a</sup> &middot; y<sup>a</sup> = (x &middot; y)<sup>a</sup>
	 */
	MultiplyCombineExponentsButton multiplyCombineExponents_check() {

		if (!TypeSGET.Exponential.equals(leftType)
				|| !TypeSGET.Exponential.equals(rightType)) {
			return null;
		}

		if (left.getChildAt(1).isLike(right.getChildAt(1))) {
			return new MultiplyCombineExponentsButton(this);
		} else {
			return null;
		}
	}

	/**
	 * x<sup>a</sup> &middot; x<sup>b</sup> = x<sup>a+b</sup><br/>
	 * If either or both sides is not an exponential, it is encased and raised
	 * to the power of 1 first <br/>
	 * ex: x &middot; x = x<sup>1+1</sup> <br/>
	 * ex: x<sup>a</sup> &middot; x = x<sup>a+1</sup>
	 */
	MultiplyCombineBasesButton[] multiplyCombineBases_check() {
		
		MultiplyCombineBasesButton[] buttons = new MultiplyCombineBasesButton[2];

		// Combining numbers is rare and this is time consuming
		if (TypeSGET.Number.equals(leftType)
				|| TypeSGET.Number.equals(rightType)) {
			return buttons;
		}

		// May not already be in exponent eg. a = a^1
		// could factor out entire side rather than just base
		EquationNode leftBase;
		TypeSGET leftExpType;
		String leftExpValue;
		if (TypeSGET.Exponential.equals(left.getType())) {
			leftBase = left.getChildAt(0);
			leftExpType = left.getChildAt(1).getType();
			leftExpValue = left.getChildAt(1).getSymbol();
		} else {
			leftBase = left;
			leftExpType = TypeSGET.Number;
			leftExpValue = "1";
		}
		EquationNode rightBase;
		TypeSGET rightExpType;
		String rightExpValue;
		if (TypeSGET.Exponential.equals(right.getType())) {
			rightBase = right.getChildAt(0);
			rightExpType = right.getChildAt(1).getType();
			rightExpValue = right.getChildAt(1).getSymbol();
		} else {
			rightBase = right;
			rightExpType = TypeSGET.Number;
			rightExpValue = "1";
		}

		if (leftBase.isLike(rightBase)) {
			buttons[0] = new MultiplyCombineBasesButton(this, false);
			if (TypeSGET.Number.equals(leftExpType)
					&& TypeSGET.Number.equals(rightExpType)) {
				try {
					new BigDecimal(leftExpValue);
					new BigDecimal(rightExpValue);
					buttons[1] = new MultiplyCombineBasesButton(this, true);
				}catch(NumberFormatException e) {
				}
			}
		}
		return buttons;
	}

	/**
	 * x/y &middot; a/b = (xa)/(yb)<br/>
	 * x &middot; a/b = (xa)/b
	 */
	MultiplyTransformButton multiplyFraction_check() {
		boolean isLeftFraction = TypeSGET.Fraction.equals(leftType);
		boolean isRightFraction = TypeSGET.Fraction.equals(rightType);

		if (!isRightFraction && !isLeftFraction) {
			return null;
		} else if (isRightFraction && isLeftFraction) {
			return new MultiplyFractionsButton(this);
		} else if (isRightFraction) {
			return new MultiplyWithFractionButton(this, left, right, true);
		} else if (isLeftFraction) {
			return new MultiplyWithFractionButton(this, right, left, false);
		} else {
			return null;
		}
	}

	/**
	 * x &middot; log<sub>b</sub>(a) = log<sub>b</sub>(a<sup>x</sup>)
	 */
	MultiplyLogRuleButton multiplyLogRule_check() {

		if (TypeSGET.Log.equals(rightType)) {
			return new MultiplyLogRuleButton(this, left, right);
		} else if (TypeSGET.Log.equals(leftType)) {
			return new MultiplyLogRuleButton(this, right, left);
		} else {
			return null;
		}
	}

}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////
abstract class MultiplyTransformButton extends TransformationButton {
	EquationNode left;
	EquationNode right;
	EquationNode operation;
	EquationNode parent;

	protected boolean reloadAlgebraActivity;
	protected MultiplyTransformations previewContext;

	MultiplyTransformButton(MultiplyTransformations context, String html) {
		super(context);
//		super(html, context);
		addStyleName(CSS.TERM);

		this.left = context.left;
		this.right = context.right;
		this.operation = context.operation;
		this.parent = context.parent;

		this.reloadAlgebraActivity = context.reloadAlgebraActivity;

	}
	
	@Override
	protected void onTransformationEnd(String changeComment,
			EquationNode nodeToSelect) {
		if(reloadAlgebraActivity) {
			left.highlight();
			operation.highlight();
			right.highlight();
		}
		super.onTransformationEnd(changeComment, nodeToSelect);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		previewContext = new MultiplyTransformations(operation);
		previewContext.reloadAlgebraActivity = false;
		return null;
	}
}

/**
 * 0 &middot; x = 0<br/>
 * x &middot; 0 = 0
 */
class MultiplyZeroButton extends MultiplyTransformButton {
	private EquationNode zero;
	private EquationNode other;

	MultiplyZeroButton(MultiplyTransformations context,
			final EquationNode other, final EquationNode zero) {
		super(context, "x·0=0");
		this.zero = zero;
		this.other = other;
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.MULTIPLY_ZERO;
	}

	@Override
	public void transform() {
		String otherSymbol = other.getSymbol();

		// Remove residual operations
		EquationNode first = zero.getIndex() < other.getIndex() ? zero : other;
		EquationNode second = zero.getIndex() > other.getIndex() ? zero : other;
		EquationNode firstOp = first.getPrevSibling();
		EquationNode secondNext = second.getNextSibling();
		if (firstOp != null && TypeSGET.Operation.equals(firstOp.getType())) {
			firstOp.remove();
		} else if (secondNext != null
				&& TypeSGET.Operation.equals(secondNext.getType())) {
			secondNext.remove();
		}

		zero.remove();
		operation.remove();
		other.remove();

		parent.decase();

		onTransformationEnd(otherSymbol + " " + operation.toString() + " 0 = 0", other);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.multiplySpecialNumber_check();
	}
}

/**
 * x &middot; 1 = x<br/>
 * 1 &middot; x = x
 */
class MultiplyOneButton extends MultiplyTransformButton {
	private EquationNode other;
	private EquationNode one;

	MultiplyOneButton(MultiplyTransformations context,
			final EquationNode other, final EquationNode one) {
		super(context, "x·1=x");
		this.one = one;
		this.other = other;
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.MULTIPLY_ONE;
	}

	@Override
	public void transform() {
		String otherSymbol = other.getSymbol();

		one.remove();
		operation.remove();

		parent.decase();

		onTransformationEnd(otherSymbol + " " + operation.toString() + " 1 = "
				+ otherSymbol, other);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.multiplySpecialNumber_check();
	}
}

/**
 * x &middot; -1 = -x<br/>
 * -1 &middot; x = -x
 */
class MultiplyNegOneButton extends MultiplyTransformButton {
	private EquationNode other;
	private EquationNode negOne;

	MultiplyNegOneButton(MultiplyTransformations context,
			final EquationNode other, final EquationNode negOne) {
		super(context, "-1·x=-x");
		this.negOne = negOne;
		this.other = other;
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.MULTIPLY_NEGATIVE_ONE;
	}

	@Override
	public void transform() {
		String otherSymbol = other.getSymbol();

		AlgebraicTransformations.propagateNegative(other);

		operation.remove();
		negOne.remove();

		parent.decase();

		onTransformationEnd(otherSymbol + " " + operation.toString()
				+ " -1 = -" + otherSymbol, other);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.multiplySpecialNumber_check();
	}
}

/**
 * Prompt for product or automatically multiply if user skill level is high
 * enough
 */
class MultiplyNumbersButton extends MultiplyTransformButton {
	MultiplyNumbersButton(MultiplyTransformations context) {
		super(context, "# · #");
		this.isEvaluation = true;
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.MULTIPLY;
	}

	@Override
	public boolean meetsAutoTransform() {
		return true;
	}

	@Override
	public void transform() {
		final BigDecimal leftValue = new BigDecimal(left.getSymbol());
		final BigDecimal rightValue = new BigDecimal(right.getSymbol());
		final BigDecimal totalValue = leftValue.multiply(rightValue);

		Skill nMagSkill = Skill.MULTIPLY;
		Badge numberMagnitudeBadge = Badge.MULTIPLY_NUMBERS_LARGE;
		int totalAbs = totalValue.abs().intValue();
		if (totalAbs <= 10) {
			nMagSkill = Skill.MULTIPLY_NUMBERS_TO_10;
			numberMagnitudeBadge = Badge.ADD_NUMBERS_10;
		} else if (totalAbs <= 100) {
			nMagSkill = Skill.MULTIPLY_NUMBERS_TO_100;
			numberMagnitudeBadge = Badge.ADD_NUMBERS_100;
		}else {
			nMagSkill = Skill.MULTIPLY_NUMBERS_LARGE;
			numberMagnitudeBadge = Badge.MULTIPLY_NUMBERS_LARGE;
		}
		final Skill numberMagnitudeSkill = nMagSkill;

		boolean meetsRequirements = Moderator
				.meetsRequirement(numberMagnitudeBadge);

		if (meetsRequirements) {
			multiplyNumbers(left, right, totalValue, leftValue, rightValue);

		} else if (!reloadAlgebraActivity) {
//			parent.replace(TypeSGET.Term, "");
//			parent.append(TypeSGET.Variable, "# ");
//			parent.append(TypeSGET.Variable, operation.getSymbol());
//			parent.append(TypeSGET.Variable, " #");

		} else {// prompt

			String question = leftValue.toPlainString() + " "
					+ operation.getSymbol() + " " + rightValue.toPlainString();

			final HashMap<Skill, Integer> skillsIncrease = new HashMap<Skill, Integer>();
			skillsIncrease.put(numberMagnitudeSkill, 0);

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
					multiplyNumbers(left, right, totalValue, leftValue,
							rightValue);
				}
			};

			prompt.appear();
		}
	}

	void multiplyNumbers(EquationNode left, EquationNode right,
			BigDecimal totalValue, BigDecimal leftValue, BigDecimal rightValue) {

		totalValue = totalValue.stripTrailingZeros();

		right.setSymbol(totalValue.stripTrailingZeros().toEngineeringString());

		// Combine Units
		UnitMap combinedmap = new UnitMap(left).getMultiple(new UnitMap(right));
		if(totalValue.compareTo(new BigDecimal("0")) == 0) {
			combinedmap = new UnitMap(true);
		}
		UnitAttribute combinedUnit = combinedmap.getUnitAttribute();
		right.setAttribute(MathAttribute.Unit, combinedUnit.toString());

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
				+ totalValue.stripTrailingZeros().toEngineeringString(), right);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.multiplyNumbers_check();
	}
}

/**
 * x &middot; (y+z) = xy + xz
 */
class MultiplyDistributionButton extends MultiplyTransformButton {
	private EquationNode dist;
	private EquationNode sum;
	private boolean isRightSum;

	MultiplyDistributionButton(MultiplyTransformations context,
			final EquationNode dist, final EquationNode sum,
			final boolean isRightSum) {
		super(context, "x·(y+z)=xy+xz");
		this.dist = dist;
		this.sum = sum;
		this.isRightSum = isRightSum;
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.MULTIPLY_DISTRIBUTE;
	}

	@Override
	public void transform() {
		for (EquationNode sumChild : sum.getChildren()) {
			if (!TypeSGET.Operation.equals(sumChild.getType())) {

				EquationNode sumChildCasings = sumChild.encase(TypeSGET.Term);

				int index = isRightSum ? 0 : -1;
				sumChildCasings.addBefore(index, operation.getType(),
						operation.getSymbol());
				sumChildCasings.addBefore(index, dist.clone());
			}
		}

		dist.remove();
		operation.remove();

		parent.decase();

		onTransformationEnd("Distribute", sum);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.multiplyDistribution_check();
	}
}

/**
 * x<sup>a</sup> &middot; y<sup>a</sup> = (x &middot; y)<sup>a</sup>
 */
class MultiplyCombineExponentsButton extends MultiplyTransformButton {
	MultiplyCombineExponentsButton(MultiplyTransformations context) {
		super(context, "x<sup>a</sup>·y<sup>a</sup>=(x·y)<sup>a</sup>");
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.MULTIPLY_COMBINE_EXPONENTS;
	}

	@Override
	public void transform() {
		EquationNode leftBase = left.getChildAt(0);
		EquationNode rightBase = right.getChildAt(0);

		EquationNode rightCasing = rightBase.encase(TypeSGET.Term);

		rightCasing.addFirst(operation);
		rightCasing.addFirst(leftBase);

		left.remove();

		parent.decase();

		onTransformationEnd("Combine Exponents", right);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.multiplyCombineExponents_check();
	}
}

/**
 * x<sup>a</sup> &middot; x<sup>b</sup> = x<sup>a+b</sup><br/>
 * If either or both sides is not an exponential, it is encased and raised to
 * the power of 1 first <br/>
 * ex: x &middot; x = x<sup>1+1</sup> <br/>
 * ex: x<sup>a</sup> &middot; x = x<sup>a+1</sup>
 */
class MultiplyCombineBasesButton extends MultiplyTransformButton {
	boolean withArithmetic;

	MultiplyCombineBasesButton(MultiplyTransformations context,
			boolean withArithmetic) {
		super(context, "x<sup>a</sup>·x<sup>b</sup>=x<sup>a+b</sup>");
		this.withArithmetic = withArithmetic;
	}

	@Override
	public Badge getAssociatedBadge() {
		if (withArithmetic) {
			return Badge.MULTIPLY_COMBINE_BASES_ARITHMETIC;
		} else {
			return Badge.MULTIPLY_COMBINE_BASES;
		}
	}

	@Override
	public void transform() {
		EquationNode leftExponential = left;
		EquationNode rightExponential = right;
		if (!TypeSGET.Exponential.equals(left.getType())) {
			leftExponential = left.encase(TypeSGET.Exponential);
			leftExponential.append(TypeSGET.Number, "1");
		}
		if (!TypeSGET.Exponential.equals(right.getType())) {
			rightExponential = right.encase(TypeSGET.Exponential);
			rightExponential.append(TypeSGET.Number, "1");
		}

		EquationNode leftExp = leftExponential.getChildAt(1);
		EquationNode rightExp = rightExponential.getChildAt(1);

		if (withArithmetic) {
			BigDecimal leftValue = new BigDecimal(
					leftExp.getAttribute(MathAttribute.Value));
			BigDecimal rightValue = new BigDecimal(
					rightExp.getAttribute(MathAttribute.Value));
			BigDecimal combinedValue = leftValue.add(rightValue);
			rightExp.replace(TypeSGET.Number, combinedValue.toPlainString());

		} else {
			EquationNode rightCasing = rightExp.encase(TypeSGET.Sum);

			rightCasing.addFirst(TypeSGET.Operation, Operator.PLUS.getSign());
			rightCasing.addFirst(leftExp);
		}

		leftExponential.remove();
		operation.remove();

		parent.decase();

		onTransformationEnd("Combine Bases", rightExponential);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		if (withArithmetic) {
			return previewContext.multiplyCombineBases_check()[1];
		} else {
			return previewContext.multiplyCombineBases_check()[0];
		}
	}
}

/**
 * x &middot; a/b = (xa)/b
 */
class MultiplyWithFractionButton extends MultiplyTransformButton {
	private EquationNode nonFrac;
	private EquationNode fraction;
	private boolean isRightFraction;

	MultiplyWithFractionButton(MultiplyTransformations context,
			final EquationNode nonFrac, final EquationNode fraction,
			final boolean isRightFraction) {
		super(context, "x·a/b=(xa)/b");
		this.nonFrac = nonFrac;
		this.fraction = fraction;
		this.isRightFraction = isRightFraction;
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.MULTIPLY_WITH_FRACTION;
	}

	@Override
	public void transform() {
		EquationNode numerator = fraction.getChildAt(0);
		numerator = numerator.encase(TypeSGET.Term);

		int index = isRightFraction ? 0 : -1;
		numerator.addBefore(index, operation);
		numerator.addBefore(index, nonFrac);

		parent.decase();

		onTransformationEnd("Multiply with Fraction", fraction);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.multiplyFraction_check();
	}
}

/**
 * x/y &middot; a/b = (xa)/(yb)
 */
class MultiplyFractionsButton extends MultiplyTransformButton {
	MultiplyFractionsButton(MultiplyTransformations context) {
		super(context, "x/y·a/b=(xa)/(yb)");
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.MULTIPLY_FRACTIONS;
	}

	@Override
	public void transform() {
		EquationNode numerator = right.getChildAt(0);
		numerator = numerator.encase(TypeSGET.Term);
		numerator.addFirst(operation);
		numerator.addFirst(left.getChildAt(0));

		EquationNode denominator = right.getChildAt(1);
		denominator = denominator.encase(TypeSGET.Term);
		denominator.addFirst(TypeSGET.Operation, operation.getSymbol());
		denominator.addFirst(left.getChildAt(0));

		left.remove();

		parent.decase();

		onTransformationEnd("Multiply Fractions", right);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.multiplyFraction_check();
	}
}

/**
 * x &middot; log<sub>b</sub>(a) = log<sub>b</sub>(a<sup>x</sup>)
 */
class MultiplyLogRuleButton extends MultiplyTransformButton {
	private EquationNode other;
	private EquationNode log;

	MultiplyLogRuleButton(MultiplyTransformations context,
			final EquationNode other, final EquationNode log) {
		super(context, "x·log<sub>b</sub>(a)=log<sub>b</sub>(a<sup>x</sup>)");
		this.other = other;
		this.log = log;
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.MULTIPLY_LOG_RULE;
	}

	@Override
	public void transform() {
		EquationNode exp = log.getFirstChild().encase(TypeSGET.Exponential);
		exp.append(other);

		operation.remove();

		parent.decase();

		onTransformationEnd("Log Power Rule", other);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.multiplyLogRule_check();
	}
}
