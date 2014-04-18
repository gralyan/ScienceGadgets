package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;

import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.specification.NumberPrompt;
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

		JSNICalls.TIME_ELAPSED("MULT1 ");
		this.parent = multiplyNode.getParent();
		JSNICalls.TIME_ELAPSED("MULT2 ");

		this.left = multiplyNode.getPrevSibling();
		this.operation = multiplyNode;
		this.right = multiplyNode.getNextSibling();

		JSNICalls.TIME_ELAPSED("MULT3 ");
		this.leftType = left.getType();
		this.rightType = right.getType();

		JSNICalls.TIME_ELAPSED("MULT4 ");
		if (add(multiplySpecialNumber_check())) {
			return;
		}
		JSNICalls.TIME_ELAPSED("MULT5 ");
		add(multiplyNumbers_check());
		JSNICalls.TIME_ELAPSED("MULT6 ");
		add(multiplyFraction_check());
		JSNICalls.TIME_ELAPSED("MULT7 ");
		add(multiplyDistribution_check());
		JSNICalls.TIME_ELAPSED("MULT8 ");
		add(multiplyCombineBases_check());
		JSNICalls.TIME_ELAPSED("MULT9 ");
		add(multiplyCombineExponents_check());
		JSNICalls.TIME_ELAPSED("MULT10 ");
		add(multiplyLogRule_check());
		JSNICalls.TIME_ELAPSED("MULT11 ");

	}

	/**
	 * Multiplying by one of the specially designated numbers (-1, 0 1)
	 */
//	MultiplyTransformButton multiplySpecialNumber_check() {
//		
//		if (!TypeSGET.Number.equals(leftType)
//				&& !TypeSGET.Number.equals(rightType)) {
//			return null;
//		}
//		
//		final int same = 0;
//		BigDecimal negOne = new BigDecimal(-1);
//		BigDecimal zero = new BigDecimal(0);
//		BigDecimal one = new BigDecimal(1);
//		
//		try {
//			BigDecimal rightValue = new BigDecimal(right.getSymbol());
//			String rightUnits = right.getAttribute(MathAttribute.Unit);
//			if (rightValue.compareTo(zero) == same) {
//				return new MultiplyZeroButton(this, left, right);
//			} else if (rightValue.compareTo(one) == same
//					&& "".equals(rightUnits)) {
//				return new MultiplyOneButton(this, left, right);
//			} else if (rightValue.compareTo(negOne) == same
//					&& "".equals(rightUnits)) {
//				switch (left.getType()) {
//				case Number:
//				case Variable:
//				case Sum:
//				case Term:
//				case Fraction:
//					return new MultiplyNegOneButton(this, left, right);
//				}
//			}
//		} catch (NumberFormatException e) {
//		}
//		
//		try {
//			BigDecimal leftValue = new BigDecimal(left.getSymbol());
//			String leftUnits = right.getAttribute(MathAttribute.Unit);
//			if (leftValue.compareTo(zero) == same) {
//				return new MultiplyZeroButton(this, right, left);
//			} else if (leftValue.compareTo(one) == same && "".equals(leftUnits)) {
//				return new MultiplyOneButton(this, right, left);
//			} else if (leftValue.compareTo(negOne) == same
//					&& "".equals(leftUnits)) {
//				switch (right.getType()) {
//				case Number:
//				case Variable:
//				case Sum:
//				case Term:
//				case Fraction:
//					return new MultiplyNegOneButton(this, right, left);
//				}
//			}
//		} catch (NumberFormatException e) {
//		}
//		return null;
//	}
	MultiplyTransformButton multiplySpecialNumber_check() {

		if (!TypeSGET.Number.equals(leftType)
				&& !TypeSGET.Number.equals(rightType)) {
			return null;
		}

			String rightValue = right.getSymbol();
			String rightUnits = right.getAttribute(MathAttribute.Unit);
			if ("0".equals(rightValue)) {
				return new MultiplyZeroButton(this, left, right);
			} else if ("1".equals(rightValue)
					&& "".equals(rightUnits)) {
				return new MultiplyOneButton(this, left, right);
			} else if ("-1".equals(rightValue)
					&& "".equals(rightUnits)) {
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
			} else if ("-1".equals(leftValue)
					&& "".equals(leftUnits)) {
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
			return new MultiplyNumbersButton(this);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			JSNICalls
					.error("A number node couldn't be parsed: " + e.toString());
			return null;
		}
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
	MultiplyCombineBasesButton multiplyCombineBases_check() {
		
		//Combining numbers is rare and this is time consuming
		if(TypeSGET.Number.equals(leftType) || TypeSGET.Number.equals(rightType)) {
			return null;
		}

		// May not already be in exponent eg. a = a^1
		// could factor out entire side rather than just base
		EquationNode leftBase;
		if (TypeSGET.Exponential.equals(left.getType())) {
			leftBase = left.getChildAt(0);
		} else {
			leftBase = left;
		}
		EquationNode rightBase;
		if (TypeSGET.Exponential.equals(right.getType())) {
			rightBase = right.getChildAt(0);
		} else {
			rightBase = right;
		}

		if (leftBase.isLike(rightBase)) {
			return new MultiplyCombineBasesButton(this);
		} else {
			return null;
		}
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
		super(html, context);
		addStyleName(CSS.TERM + " " + CSS.DISPLAY_WRAPPER);

		this.left = context.left;
		this.right = context.right;
		this.operation = context.operation;
		this.parent = context.parent;

		this.reloadAlgebraActivity = context.reloadAlgebraActivity;

		left.highlight();
		operation.highlight();
		right.highlight();
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

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel(
					otherSymbol + " " + operation.toString() + " 0 = 0",
					Skill.MULTIPLY_WITH_ZERO);

		}
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
	public void transform() {
		String otherSymbol = other.getSymbol();

		one.remove();
		operation.remove();

		parent.decase();

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel(
					otherSymbol + " " + operation.toString() + " 1 = "
							+ otherSymbol, Skill.MULTIPLY_WITH_ONE);

		}
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
	public void transform() {
		String otherSymbol = other.getSymbol();

		AlgebraicTransformations.propagateNegative(other);

		operation.remove();
		negOne.remove();

		parent.decase();

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel(
					otherSymbol + " " + operation.toString() + " -1 = -"
							+ otherSymbol, Skill.MULTIPLY_WITH_NEGATIVE_ONE);

		}
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
	}

	@Override
	public void transform() {
		final BigDecimal leftValue = new BigDecimal(left.getSymbol());
		final BigDecimal rightValue = new BigDecimal(right.getSymbol());
		final BigDecimal totalValue = leftValue.multiply(rightValue);

		boolean meetsRequirements = Moderator.meetsRequirements(Badge
				.getRequiredBadges(operation.getOperation(), left, right));

		if (meetsRequirements) {
			multiplyNumbers(left, right, totalValue, leftValue, rightValue);

		} else if (!reloadAlgebraActivity) {
			parent.replace(TypeSGET.Term, "");
			parent.append(TypeSGET.Variable, "# ");
			parent.append(TypeSGET.Variable, operation.getSymbol());
			parent.append(TypeSGET.Variable, " #");

		} else {// prompt

			String question = leftValue.toString() + " "
					+ operation.getSymbol() + " " + rightValue.toString()
					+ " = ";
			NumberPrompt prompt = new NumberPrompt(question, totalValue) {
				@Override
				public void onCorrect(int skillIncrease) {
					multiplyNumbers(left, right, totalValue, leftValue,
							rightValue);
					Moderator.getStudent().increaseSkill(Skill.MULTIPLICATION, skillIncrease);
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
		UnitAttribute combinedUnit = combinedmap.getUnitAttribute();
		right.setAttribute(MathAttribute.Unit, combinedUnit.toString());

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
					Skill.MULTIPLICATION);
		}
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

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel("Distribute",
					Skill.DISTRIBUTIVE_PROPERTY);

		}
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
	public void transform() {
		EquationNode leftBase = left.getChildAt(0);
		EquationNode rightBase = right.getChildAt(0);

		EquationNode rightCasing = rightBase.encase(TypeSGET.Term);

		rightCasing.addFirst(operation);
		rightCasing.addFirst(leftBase);

		left.remove();

		parent.decase();

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel("Combine Exponents",
					Skill.MULTIPLY_SIMILAR_EXPONENTS);

		}
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
	MultiplyCombineBasesButton(MultiplyTransformations context) {
		super(context, "x<sup>a</sup>·x<sup>b</sup>=x<sup>a+b</sup>");
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

		if (Moderator.meetsRequirement(Badge.MULTIPLY_COMBINE_BASES) && TypeSGET.Number.equals(leftExp.getType())
				&& TypeSGET.Number.equals(rightExp.getType())) {
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

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel("Combine Bases",
					Skill.MULTIPLY_SIMILAR_BASES);

		}
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.multiplyCombineBases_check();
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
	public void transform() {
		EquationNode numerator = fraction.getChildAt(0);
		numerator = numerator.encase(TypeSGET.Term);

		int index = isRightFraction ? 0 : -1;
		numerator.addBefore(index, operation);
		numerator.addBefore(index, nonFrac);

		parent.decase();

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel("Multiply with Fraction",
					Skill.MULTIPLYING_WITH_FRACTIONs);

		}
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

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel("Multiply Fractions",
					Skill.MULTIPLYING_FRACTIONs);

		}
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
	public void transform() {
		EquationNode exp = log.getFirstChild().encase(TypeSGET.Exponential);
		exp.append(other);

		operation.remove();

		parent.decase();

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel("Log Power Rule", Skill.MULTIPLY_WITH_LOG);
		}
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.multiplyLogRule_check();
	}
}
