package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;
import com.sciencegadgets.shared.UnitMap;

public class ExponentialTransformations extends TransformationList {

	private static final long serialVersionUID = -4899066857084144484L;

	MathNode exponential;
	MathNode base;
	MathNode exponent;

	TypeML baseType;
	TypeML exponentType;

	public ExponentialTransformations(MathNode exponentialNode) {
		super(exponentialNode);

		this.exponential = exponentialNode;
		this.base = exponential.getChildAt(0);
		this.exponent = exponential.getChildAt(1);

		this.baseType = base.getType();
		this.exponentType = exponent.getType();

		if (add(zeroBase_check())) {
			return;
		}
		add(exponentialExpand_check());
		add(exponentialEvaluate_check());
		add(exponentialExponentiate_check());
		add(exponentialPropagate_check());
		add(exponentialFlip_check());
		add(unravelExpLog_check());
	}

	ExponentialTransformButton zeroBase_check() {
		if (TypeML.Number.equals(baseType) && "0".equals(base.getSymbol())) {
			return new ZeroBaseButton(this);
		}
		return null;
	}

	ExponentialTransformButton exponentialEvaluate_check() {
		if (TypeML.Number.equals(baseType)
				&& TypeML.Number.equals(exponentType)) {
			try {
				int exp = Integer.parseInt(exponent.getSymbol());
				return new ExponentialEvaluateButton(this, (int) exp);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	ExponentialTransformButton exponentialExponentiate_check() {
		if (TypeML.Exponential.equals(baseType)) {
			return new ExponentialExponentiateButton(this);
		}
		return null;
	}

	ExponentialTransformButton exponentialPropagate_check() {
		if (TypeML.Fraction.equals(baseType) || TypeML.Term.equals(baseType)) {
			return new ExponentialPropagateButton(this);
		}
		return null;
	}

	ExponentialTransformButton exponentialFlip_check() {
		if (TypeML.Number.equals(exponentType)
				|| TypeML.Variable.equals(exponentType)) {
			if (exponent.getSymbol().startsWith(Operator.MINUS.getSign())) {
				return new ExponentialFlipButton(this);
			}
		}
		return null;
	}

	ExponentialTransformButton exponentialExpand_check() {
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

	/**
	 * Check if: (log base = exponential base)<br/>
	 * b<sup>log<sub>b</sub>(x)</sup> = x
	 */
	TransformationButton unravelExpLog_check() {
		MathNode log = exponential.getChildAt(1);
		if (TypeML.Log.equals(log.getType())) {
			String logBase = log.getAttribute(MathAttribute.LogBase);
			MathNode exponentialBase = exponential.getFirstChild();
			if (TypeML.Number.equals(exponentialBase.getType())
					&& exponentialBase.getSymbol().equals(logBase)) {
				return new UnravelButton(exponential, log.getFirstChild(),
						Rule.LOGARITHM, this);

			}
		}
		return null;
	}
}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////
class ExponentialTransformButton extends TransformationButton {
	final MathNode exponential;
	final MathNode base;
	final MathNode exponent;

	protected boolean reloadAlgebraActivity;
	protected ExponentialTransformations previewContext;

	ExponentialTransformButton(ExponentialTransformations context, String html) {
		super(context);
		addStyleName(CSS.EXPONENTIAL + " " + CSS.DISPLAY_WRAPPER);

		this.exponential = context.exponential;
		this.base = context.base;
		this.exponent = context.exponent;

		this.reloadAlgebraActivity = context.reloadAlgebraActivity;

		exponential.highlight();
	}

	@Override
	TransformationButton getPreviewButton(MathNode exponential) {
		previewContext = new ExponentialTransformations(exponential);
		previewContext.reloadAlgebraActivity = false;
		return null;
	}
}

/**
 * 0<sup>x</sup> = 0
 */
class ZeroBaseButton extends ExponentialTransformButton {

	ZeroBaseButton(ExponentialTransformations context) {
		super(context, "0<sup>x</sup> = 0");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				exponential.replace(base);

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("0<sup>x</sup> = 0",
							Rule.EXPONENT_PROPERTIES);
				}
			}
		});
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.zeroBase_check();
	}
}

/**
 * b<sup>a</sup> = b.pow(a)<br/>
 * ex: 2<sup>3</sup> = 8
 */
class ExponentialEvaluateButton extends ExponentialTransformButton {

	public ExponentialEvaluateButton(ExponentialTransformations context,
			final int exp) {
		super(context, "Evaluate Exponential");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				final BigDecimal baseValue = new BigDecimal(base.getSymbol());
				final BigDecimal totalValue = baseValue.pow(exp,
						MathContext.DECIMAL128);

				final UnitMap totalUnitMap = new UnitMap(base)
						.getExponential(exp);

				if (Moderator.isInEasyMode) {
					evaluateExponential(baseValue, exp, totalValue,
							totalUnitMap);

				} else if (!reloadAlgebraActivity) {
					exponential.replace(TypeML.Variable, "# ^ #");

				} else {// prompt

					String question = exponential.getHTMLString() + " = ";
					NumberPrompt prompt = new NumberPrompt(question, totalValue) {
						@Override
						void onCorrect() {
							evaluateExponential(baseValue, exp, totalValue,
									totalUnitMap);
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

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel(baseValue.stripTrailingZeros()
					.toEngineeringString()
					+ " ^ "
					+ expValue
					+ " = "
					+ totalValue.stripTrailingZeros().toEngineeringString(),
					Rule.EXPONENT_PROPERTIES);
		}
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.exponentialEvaluate_check();
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
						// Window.alert("Warning: You are now assuming that "+base.getHTMLString()+" is not equivalent to 0");
						Window.alert("Warning: You are now assuming that the base is not equivalent to 0");
						exponential.replace(TypeML.Number, "1");
					}

				} else if (exp == 1) {
					exponential.replace(base);

				} else if (exp > 1) {
					if (TypeML.Term.equals(base.getType())) {
						LinkedList<MathNode> baseChildren = base.getChildren();
						for(int i = 1; i < exp; i++) {
							base.append(TypeML.Operation,
									TypeML.Operator.getMultiply().getSign());
							for(MathNode baseChild : baseChildren) {
								base.append(baseChild.clone());
							}
						}
						exponential.replace(base);
					} else {
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
				}

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("Expand",
							Rule.EXPONENT_PROPERTIES);
				}
			}
		});
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.exponentialExpand_check();
	}
}

/**
 * (x<sup>a</sup>)<sup>b</sup> = x<sup>a &middot; b</sup>
 */
class ExponentialExponentiateButton extends ExponentialTransformButton {

	ExponentialExponentiateButton(ExponentialTransformations context) {
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

				if (reloadAlgebraActivity) {
					Moderator
							.reloadEquationPanel(
									"(x<sup>a</sup>)<sup>b</sup> = x<sup>a &middot; b</sup>",
									Rule.EXPONENT_PROPERTIES);
				}
			}
		});
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.exponentialExponentiate_check();
	}
}

/**
 * (x/y)<sup>a</sup> = (x<sup>a</sup>)/(y<sup>a</sup>)<br/>
 * (x*y)<sup>a</sup> = x<sup>a</sup>*y<sup>a</sup>
 */
class ExponentialPropagateButton extends ExponentialTransformButton {

	ExponentialPropagateButton(ExponentialTransformations context) {
		super(context, "(x/y)<sup>b</sup> = (x<sup>b</sup>)/(y<sup>b</sup>)");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				for (MathNode baseChild : base.getChildren()) {
					if (TypeML.Operation.equals(baseChild.getType())) {
						continue;
					}
					baseChild = baseChild.encase(TypeML.Exponential);
					baseChild.append(exponent.clone());
				}
				exponential.replace(base);

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel(
							"(x*y)<sup>a</sup> = x<sup>a</sup>*y<sup>a</sup>",
							Rule.EXPONENT_PROPERTIES);
				}
			}
		});
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.exponentialPropagate_check();
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

				if (TypeML.Fraction.equals(base.getType())) {
					base.append(base.getFirstChild());

				} else {

					AlgebraicTransformations.reciprocate(exponential);
				}

				exponent.setSymbol(exponent.getSymbol().replace(
						Operator.MINUS.getSign(), ""));

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel(
							"x<sup>-b</sup> = (1/x)<sup>b</sup>",
							Rule.EXPONENT_PROPERTIES);
				}
			}
		});
	}

	@Override
	TransformationButton getPreviewButton(MathNode operation) {
		super.getPreviewButton(operation);
		return previewContext.exponentialFlip_check();
	}
}
