package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.specification.NumberPrompt;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;
import com.sciencegadgets.shared.UnitAttribute;
import com.sciencegadgets.shared.UnitMap;

public class MultiplyTransformations extends TransformationList {

	private static final long serialVersionUID = 3127633894356779264L;

	EquationNode left;
	EquationNode operation;
	EquationNode right;
	EquationNode parent;

	TypeEquationXML leftType;
	TypeEquationXML rightType;

	public MultiplyTransformations(EquationNode multiplyNode) {
		super(multiplyNode);

		this.parent = multiplyNode.getParent();

		this.left = multiplyNode.getPrevSibling();
		this.operation = multiplyNode;
		this.right = multiplyNode.getNextSibling();

		this.leftType = left.getType();
		this.rightType = right.getType();

		if(add(multiplySpecialNumber_check())) {
			return;
		}
		add(multiplyNumbers_check());
		add(multiplyFraction_check());
		add(multiplyDistribution_check());
		add(multiplyCombineBases_check());
		add(multiplyCombineExponents_check());
		add(multiplyLogRule_check());

	}

	/**
	 * Multiplying by one of the specially designated numbers (-1, 0 1)
	 */
	MultiplyTransformButton multiplySpecialNumber_check() {

		if (!TypeEquationXML.Number.equals(leftType) && !TypeEquationXML.Number.equals(rightType)) {
			return null;
		}

		final int same = 0;
		BigDecimal negOne = new BigDecimal(-1);
		BigDecimal zero = new BigDecimal(0);
		BigDecimal one = new BigDecimal(1);

		try {
			BigDecimal rightValue = new BigDecimal(right.getSymbol());
			String rightUnits = right.getAttribute(MathAttribute.Unit);
			if (rightValue.compareTo(zero) == same) {
				return new MultiplyZeroButton(this, left, right);
			} else if (rightValue.compareTo(one) == same
					&& "".equals(rightUnits)) {
				return new MultiplyOneButton(this, left, right);
			} else if (rightValue.compareTo(negOne) == same
					&& "".equals(rightUnits)) {
				return new MultiplyNegOneButton(this, left, right);
			}
		} catch (NumberFormatException e) {
		}

		try {
			BigDecimal leftValue = new BigDecimal(left.getSymbol());
			String leftUnits = right.getAttribute(MathAttribute.Unit);
			if (leftValue.compareTo(zero) == same) {
				return new MultiplyZeroButton(this, right, left);
			} else if (leftValue.compareTo(one) == same && "".equals(leftUnits)) {
				return new MultiplyOneButton(this, right, left);
			} else if (leftValue.compareTo(negOne) == same
					&& "".equals(leftUnits)) {
				return new MultiplyNegOneButton(this, right, left);
			}
		} catch (NumberFormatException e) {
		}
		return null;
	}

	/**
	 * Multiply two numbers, prompts for the product unless the user skill level
	 * is high enough
	 */
	MultiplyTransformButton multiplyNumbers_check() {

		if (!(TypeEquationXML.Number.equals(leftType) && TypeEquationXML.Number.equals(rightType))) {
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
	MultiplyTransformButton multiplyDistribution_check() {

		if (TypeEquationXML.Sum.equals(rightType)) {
			return new MultiplyDistributionButton(this, left, right, true);
		} else if (TypeEquationXML.Sum.equals(leftType)) {
			return new MultiplyDistributionButton(this, right, left, false);
		} else {
			return null;
		}
	}

	/**
	 * x<sup>a</sup> &middot; y<sup>a</sup> = (x &middot; y)<sup>a</sup>
	 */
	MultiplyTransformButton multiplyCombineExponents_check() {

		if (!TypeEquationXML.Exponential.equals(leftType)
				|| !TypeEquationXML.Exponential.equals(rightType)) {
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
	MultiplyTransformButton multiplyCombineBases_check() {

		// May not already be in exponent eg. a = a^1
		// could factor out entire side rather than just base
		EquationNode leftBase;
		if (TypeEquationXML.Exponential.equals(left.getType())) {
			leftBase = left.getChildAt(0);
		} else {
			leftBase = left;
		}
		EquationNode rightBase;
		if (TypeEquationXML.Exponential.equals(right.getType())) {
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
		boolean isLeftFraction = TypeEquationXML.Fraction.equals(leftType);
		boolean isRightFraction = TypeEquationXML.Fraction.equals(rightType);

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
	MultiplyTransformButton multiplyLogRule_check() {

		if (TypeEquationXML.Log.equals(rightType)) {
			return new MultiplyLogRuleButton(this, left, right);
		} else if (TypeEquationXML.Log.equals(leftType)) {
			return new MultiplyLogRuleButton(this, right, left);
		} else {
			return null;
		}
	}

}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////
class MultiplyTransformButton extends TransformationButton {
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
	MultiplyZeroButton(MultiplyTransformations context, final EquationNode other,
			final EquationNode zero) {
		super(context, "x·0=0");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				String otherSymbol = other.getSymbol();

				// Remove residual operations
				EquationNode first = zero.getIndex() < other.getIndex() ? zero
						: other;
				EquationNode second = zero.getIndex() > other.getIndex() ? zero
						: other;
				EquationNode firstOp = first.getPrevSibling();
				EquationNode secondNext = second.getNextSibling();
				if (firstOp != null
						&& TypeEquationXML.Operation.equals(firstOp.getType())) {
					firstOp.remove();
				} else if (secondNext != null
						&& TypeEquationXML.Operation.equals(secondNext.getType())) {
					secondNext.remove();
				}

				zero.remove();
				operation.remove();
				other.remove();

				parent.decase();

				if (reloadAlgebraActivity) {
					Moderator
							.reloadEquationPanel(
									otherSymbol + " " + operation.toString()
											+ " 0 = 0", Rule.MULTIPLICATION);

				}
			}
		});
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
	MultiplyOneButton(MultiplyTransformations context, final EquationNode other,
			final EquationNode one) {
		super(context, "x·1=x");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				String otherSymbol = other.getSymbol();

				one.remove();
				operation.remove();

				parent.decase();

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel(
							otherSymbol + " " + operation.toString() + " 1 = "
									+ otherSymbol, Rule.MULTIPLICATION);

				}
			}
		});
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
	MultiplyNegOneButton(MultiplyTransformations context, final EquationNode other,
			final EquationNode negOne) {
		super(context, "-1·x=-x");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				String otherSymbol = other.getSymbol();

				AlgebraicTransformations.propagateNegative(other);

				operation.remove();
				negOne.remove();

				parent.decase();

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel(
							otherSymbol + " " + operation.toString()
									+ " -1 = -" + otherSymbol,
							Rule.MULTIPLICATION);

				}
			}
		});
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

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				final BigDecimal leftValue = new BigDecimal(left.getSymbol());
				final BigDecimal rightValue = new BigDecimal(right.getSymbol());
				final BigDecimal totalValue = leftValue.multiply(rightValue);

				if (Moderator.isInEasyMode) {
					multiplyNumbers(left, right, totalValue, leftValue,
							rightValue);

				} else if (!reloadAlgebraActivity) {
					parent.replace(TypeEquationXML.Term, "");
					parent.append(TypeEquationXML.Variable, "# ");
					parent.append(TypeEquationXML.Variable, operation.getSymbol());
					parent.append(TypeEquationXML.Variable, " #");

				} else {// prompt

					String question = leftValue.toString() + " "
							+ operation.getSymbol() + " "
							+ rightValue.toString() + " = ";
					NumberPrompt prompt = new NumberPrompt(question, totalValue) {
						@Override
						public void onCorrect() {
							multiplyNumbers(left, right, totalValue, leftValue,
									rightValue);
						}
					};

					prompt.appear();
				}

			}
		});
	}

	void multiplyNumbers(EquationNode left, EquationNode right, BigDecimal totalValue,
			BigDecimal leftValue, BigDecimal rightValue) {

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
					Rule.MULTIPLICATION);
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
	MultiplyDistributionButton(MultiplyTransformations context,
			final EquationNode dist, final EquationNode sum, final boolean isRightSum) {
		super(context, "x·(y+z)=xy+xz");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				for (EquationNode sumChild : sum.getChildren()) {
					if (!TypeEquationXML.Operation.equals(sumChild.getType())) {

						EquationNode sumChildCasings = sumChild.encase(TypeEquationXML.Term);

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
							Rule.DISTRIBUTIVE_PROPERTY);

				}
			}
		});
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

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				EquationNode leftBase = left.getChildAt(0);
				EquationNode rightBase = right.getChildAt(0);

				EquationNode rightCasing = rightBase.encase(TypeEquationXML.Term);

				rightCasing.addFirst(operation);
				rightCasing.addFirst(leftBase);

				left.remove();

				parent.decase();

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("Combine Exponents",
							Rule.EXPONENT_PROPERTIES);

				}
			}
		});
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

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				EquationNode leftExponential = left;
				EquationNode rightExponential = right;
				if (!TypeEquationXML.Exponential.equals(left.getType())) {
					leftExponential = left.encase(TypeEquationXML.Exponential);
					leftExponential.append(TypeEquationXML.Number, "1");
				}
				if (!TypeEquationXML.Exponential.equals(right.getType())) {
					rightExponential = right.encase(TypeEquationXML.Exponential);
					rightExponential.append(TypeEquationXML.Number, "1");
				}

				EquationNode leftExp = leftExponential.getChildAt(1);
				EquationNode rightExp = rightExponential.getChildAt(1);

				if (Moderator.isInEasyMode
						&& TypeEquationXML.Number.equals(leftExp.getType())
						&& TypeEquationXML.Number.equals(rightExp.getType())) {
					BigDecimal leftValue = new BigDecimal(
							leftExp.getAttribute(MathAttribute.Value));
					BigDecimal rightValue = new BigDecimal(
							rightExp.getAttribute(MathAttribute.Value));
					BigDecimal combinedValue = leftValue.add(rightValue);
					rightExp.replace(TypeEquationXML.Number,
							combinedValue.toPlainString());

				} else {
					EquationNode rightCasing = rightExp.encase(TypeEquationXML.Sum);

					rightCasing.addFirst(TypeEquationXML.Operation,
							Operator.PLUS.getSign());
					rightCasing.addFirst(leftExp);
				}

				leftExponential.remove();
				operation.remove();

				parent.decase();

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("Combine Bases",
							Rule.EXPONENT_PROPERTIES);

				}
			}
		});
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
	MultiplyWithFractionButton(MultiplyTransformations context,
			final EquationNode nonFrac, final EquationNode fraction,
			final boolean isRightFraction) {
		super(context, "x·a/b=(xa)/b");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				EquationNode numerator = fraction.getChildAt(0);
				numerator = numerator.encase(TypeEquationXML.Term);

				int index = isRightFraction ? 0 : -1;
				numerator.addBefore(index, operation);
				numerator.addBefore(index, nonFrac);

				parent.decase();

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("Multiply with Fraction",
							Rule.FRACTION_MULTIPLICATION);

				}
			}
		});
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

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				EquationNode numerator = right.getChildAt(0);
				numerator = numerator.encase(TypeEquationXML.Term);
				numerator.addFirst(operation);
				numerator.addFirst(left.getChildAt(0));

				EquationNode denominator = right.getChildAt(1);
				denominator = denominator.encase(TypeEquationXML.Term);
				denominator.addFirst(TypeEquationXML.Operation,
						operation.getSymbol());
				denominator.addFirst(left.getChildAt(0));

				left.remove();

				parent.decase();

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("Multiply Fractions",
							Rule.FRACTION_MULTIPLICATION);

				}
			}
		});
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
	MultiplyLogRuleButton(MultiplyTransformations context,
			final EquationNode other, final EquationNode log) {
		super(context, "x·log<sub>b</sub>(a)=log<sub>b</sub>(a<sup>x</sup>)");

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {

				EquationNode exp = log.getFirstChild().encase(TypeEquationXML.Exponential);
				exp.append(other);

				operation.remove();

				parent.decase();

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("Log Power Rule",
							Rule.LOGARITHM);
				}
			}
		});
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.multiplyLogRule_check();
	}
}
