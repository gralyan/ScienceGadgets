package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.math.MathContext;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;
import com.sciencegadgets.shared.UnitMap;

public class ExponentialTransformations extends Transformations {

	private static final long serialVersionUID = -4899066857084144484L;

	MathNode exponential;
	MathNode base;
	MathNode exponent;
	TypeML baseType;
	TypeML exponentType;

	public ExponentialTransformations(MathNode exponentialNode) {
		exponential = exponentialNode;
		base = exponential.getChildAt(0);
		exponent = exponential.getChildAt(1);
		baseType = base.getType();
		exponentType = exponent.getType();

		add(exponentialExpand_check());
		add(exponentialEvaluate_check());
		add(exponentialExponentiate_check());
		add(exponentialFraction_check());
		add(exponentialFlip_check());
	}

	private ExponentialTransformButton exponentialEvaluate_check() {
		if (TypeML.Number.equals(baseType)
				&& TypeML.Number.equals(exponentType)) {
			return new ExponentialEvaluateButton(this);
		}
		return null;
	}

	private ExponentialTransformButton exponentialExponentiate_check() {
		if (TypeML.Exponential.equals(baseType)) {
			return new ExponentialExponentialeButton(this);
		}
		return null;
	}

	private ExponentialTransformButton exponentialFraction_check() {
		if (TypeML.Fraction.equals(baseType)) {
			return new ExponentialFractionButton(this);
		}
		return null;
	}

	private ExponentialTransformButton exponentialFlip_check() {
		if (TypeML.Number.equals(exponentType)
				|| TypeML.Variable.equals(exponentType)) {
			if (exponent.getSymbol().startsWith(Operator.MINUS.getSign())) {
				return new ExponentialFlipButton(this);
			}
		}
		return null;
	}

	private ExponentialTransformButton exponentialExpand_check() {
		if (TypeML.Number.equals(exponentType)) {
			try {
				int exp = Integer.parseInt(exponent.getSymbol());
				// Integer from 0-10 inclusive, more than 10 is just too many
				if (exp < 10 && exp >= 0) {
					return new ExponentialExpandButton(this, (int) exp);
				}
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}
}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////
class ExponentialTransformButton extends Button {
	final MathNode exponential;
	final MathNode base;
	final MathNode exponent;

	ExponentialTransformButton(ExponentialTransformations context, String html) {
		super(html);
		this.exponential = context.exponential;
		this.base = context.base;
		this.exponent = context.exponent;

		exponential.highlight();
	}
}

/**
 * b<sup>a</sup> = b.pow(a)<br/>
 * ex: 2<sup>3</sup> = 8
 */
class ExponentialEvaluateButton extends ExponentialTransformButton {

	public ExponentialEvaluateButton(ExponentialTransformations context) {
		super(context, "Evaluate Exponential");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				final BigDecimal baseValue = new BigDecimal(base.getSymbol());
				final int expValue = Integer.parseInt(exponent.getSymbol());
				final BigDecimal totalValue = baseValue.pow(expValue,
						MathContext.DECIMAL128);

				final UnitMap totalUnitMap = new UnitMap(base)
						.getExponential(expValue);

				if (Moderator.isInEasyMode) {
					evaluateExponential(baseValue, expValue, totalValue,
							totalUnitMap);

				} else {// prompt

					String question = exponential.getHTMLString() + " = ";
					NumberPrompt prompt = new NumberPrompt(question, totalValue) {
						@Override
						void onCorrect() {
							evaluateExponential(baseValue, expValue,
									totalValue, totalUnitMap);
						}
					};
					prompt.appear();
				}

			}
		});
	}

	private void evaluateExponential(BigDecimal baseValue, int expValue,
			BigDecimal totalValue, UnitMap newUnitMap) {

		MathNode evaluated = exponential.replace(TypeML.Number, totalValue
				.stripTrailingZeros().toEngineeringString());
		String newUnit = newUnitMap.getUnitAttribute();
		evaluated.setAttribute(MathAttribute.Unit, newUnit);

		Moderator.reloadEquationPanel(baseValue.stripTrailingZeros()
				.toEngineeringString()
				+ " ^ "
				+ expValue
				+ " = "
				+ totalValue.stripTrailingZeros().toEngineeringString(),
				Rule.EXPONENT_PROPERTIES);
	}
}

/**
 * x<sup>a</sup> = x &middot; x &middot; ... x<sub>a</sub><br/>
 * ex: (y-1)<sup>2</sup> = (y-1) &middot; (y-1)
 */
class ExponentialExpandButton extends ExponentialTransformButton {

	ExponentialExpandButton(ExponentialTransformations context, final int exp) {
		super(context, "Expand Exponential");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (exp == 0) {
					if (TypeML.Number.equals(base.getType())) {
						if ("0".equals(base.getSymbol())) {
							exponential.replace(TypeML.Number, "0");
						} else {
							exponential.replace(TypeML.Number, "1");
						}
					} else {
						Window.alert("Warning: You are now assuming that the base is not equivalent to 0");
						exponential.replace(TypeML.Number, "1");
					}

				} else if (exp == 1) {
					exponential.replace(base);

				} else if (exp > 1) {
					MathNode term = exponential.encase(TypeML.Term);
					int exponIndex = exponential.getIndex();
					term.addBefore(exponIndex, base);
					for (int i = 1; i < exp; i++) {
						term.addBefore(exponIndex, TypeML.Operation,
								TypeML.Operator.getMultiply().getSign());
						term.addBefore(exponIndex, base.clone());
					}
					exponential.remove();
				}

				Moderator.reloadEquationPanel("Expand",
						Rule.EXPONENT_PROPERTIES);
			}
		});
	}

}

/**
 * (x<sup>a</sup>)<sup>b</sup> = x<sup>a &middot; b</sup>
 */
class ExponentialExponentialeButton extends ExponentialTransformButton {

	ExponentialExponentialeButton(ExponentialTransformations context) {
		super(context, "(x<sup>a</sup>)<sup>b</sup> = x<sup>a &middot; b</sup>");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				MathNode innerBase = base.getChildAt(0);
				MathNode innerExp = base.getChildAt(1);

				MathNode expTerm = exponent.encase(TypeML.Term);
				expTerm.addBefore(0, TypeML.Operation, Operator.getMultiply()
						.getSign());
				expTerm.addBefore(0, innerExp);

				base.replace(innerBase);

				Moderator
						.reloadEquationPanel(
								"(x<sup>a</sup>)<sup>b</sup> = x<sup>a &middot; b</sup>",
								Rule.EXPONENT_PROPERTIES);
			}
		});
	}

}

/**
 * (x/y)<sup>b</sup> = (x<sup>b</sup>)/(y<sup>b</sup>)
 */
class ExponentialFractionButton extends ExponentialTransformButton {

	ExponentialFractionButton(ExponentialTransformations context) {
		super(context, "(x/y)<sup>b</sup> = (x<sup>b</sup>)/(y<sup>b</sup>)");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				MathNode numerator = base.getChildAt(0).encase(
						TypeML.Exponential);
				MathNode denominator = base.getChildAt(1).encase(
						TypeML.Exponential);

				numerator.append(exponent.clone());
				denominator.append(exponent);

				exponential.replace(base);

				Moderator.reloadEquationPanel(
						"(x/y)<sup>b</sup> = (x<sup>b</sup>)/(y<sup>b</sup>)",
						Rule.EXPONENT_PROPERTIES);
			}
		});
	}

}

/**
 * (x/y)<sup>-b</sup> = (y/x)<sup>b</sup><br/>
 * x<sup>-b</sup> = (1/x)<sup>b</sup>
 */
class ExponentialFlipButton extends ExponentialTransformButton {

	ExponentialFlipButton(ExponentialTransformations context) {
		super(context, "x<sup>-b</sup> = (1/x)<sup>b</sup>");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				MathNode parent = exponential.getParent();

				if (TypeML.Fraction.equals(base.getType())) {
					base.append(base.getFirstChild());
					
				} else if (TypeML.Fraction.equals(parent.getType())) {
					
					boolean inNumerator = exponential.getIndex() == 0;
					int otherSideIndex = inNumerator ? 1 : 0;
					MathNode otherSide = parent.getChildAt(otherSideIndex).encase(TypeML.Term);
					
					otherSide.append(TypeML.Operation, Operator.getMultiply().getSign());
					otherSide.append(exponential);
					
					if (inNumerator) {
						parent.addBefore(0, TypeML.Number, "1");
					} else {
						parent.replace(otherSide);
					}
					
				} else if (TypeML.Term.equals(parent.getType())
						&& TypeML.Fraction.equals(parent.getParentType())) {
					
					boolean inNumerator = parent.getIndex() == 0;
					int otherSideIndex = inNumerator ? 1 : 0;
					
					if(exponential.getIndex()==0) {
						exponential.getNextSibling().remove();
					}else {
						exponential.getPrevSibling().remove();
					}

					MathNode otherSide = parent.getParent().getChildAt(otherSideIndex)
							.encase(TypeML.Term);
					otherSide.append(TypeML.Operation, Operator.getMultiply()
							.getSign());
					otherSide.append(exponential);
					
					parent.decase();
					
				} else {
					MathNode frac = base.encase(TypeML.Fraction);
					frac.addBefore(0, TypeML.Number, "1");
				}

				exponent.setSymbol(exponent.getSymbol().replace(
						Operator.MINUS.getSign(), ""));

				Moderator.reloadEquationPanel(
						"x<sup>-b</sup> = (1/x)<sup>b</sup>",
						Rule.EXPONENT_PROPERTIES);
			}
		});
	}

}