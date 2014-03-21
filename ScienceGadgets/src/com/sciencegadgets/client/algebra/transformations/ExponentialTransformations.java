package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;
import com.sciencegadgets.shared.UnitMap;

public class ExponentialTransformations extends TransformationList {

	private static final long serialVersionUID = -4899066857084144484L;

	EquationNode exponential;
	EquationNode base;
	EquationNode exponent;

	TypeEquationXML baseType;
	TypeEquationXML exponentType;

	public ExponentialTransformations(EquationNode exponentialNode) {
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
		if (TypeEquationXML.Number.equals(baseType) && "0".equals(base.getSymbol())) {
			return new ZeroBaseButton(this);
		}
		return null;
	}

	ExponentialTransformButton exponentialEvaluate_check() {
		if (TypeEquationXML.Number.equals(baseType)
				&& TypeEquationXML.Number.equals(exponentType)) {
			// 0 and 1 are taken care of in the expand transformation
			String expValue = exponent.getSymbol();
			if("0".equals(expValue) || "1".equals(expValue)) {
				return null;
			}
			try {
				int exp = Integer.parseInt(expValue);
				return new ExponentialEvaluateButton(this, (int) exp);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	ExponentialTransformButton exponentialExponentiate_check() {
		if (TypeEquationXML.Exponential.equals(baseType)) {
			return new ExponentialExponentiateButton(this);
		}
		return null;
	}

	ExponentialTransformButton exponentialPropagate_check() {
		if (TypeEquationXML.Fraction.equals(baseType) || TypeEquationXML.Term.equals(baseType)) {
			return new ExponentialPropagateButton(this);
		}
		return null;
	}

	ExponentialTransformButton exponentialFlip_check() {
		if (TypeEquationXML.Number.equals(exponentType)
				|| TypeEquationXML.Variable.equals(exponentType)) {
			if (exponent.getSymbol().startsWith(Operator.MINUS.getSign())) {
				return new ExponentialFlipButton(this);
			}
		}
		return null;
	}

	ExponentialTransformButton exponentialExpand_check() {
		if (TypeEquationXML.Number.equals(exponentType)) {
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
		EquationNode log = exponential.getChildAt(1);
		if (TypeEquationXML.Log.equals(log.getType())) {
			String logBase = log.getAttribute(MathAttribute.LogBase);
			EquationNode exponentialBase = exponential.getFirstChild();
			if (TypeEquationXML.Number.equals(exponentialBase.getType())
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
	final EquationNode exponential;
	final EquationNode base;
	final EquationNode exponent;

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
	TransformationButton getPreviewButton(EquationNode exponential) {
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
	TransformationButton getPreviewButton(EquationNode operation) {
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
					base.replace(TypeEquationXML.Variable, "#");
					exponent.replace(TypeEquationXML.Variable, "#");
					

				} else {// prompt

					String question = exponential.getHTMLString(true, true) + " = ";
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

		EquationNode evaluated = exponential.replace(TypeEquationXML.Number, totalValue
				.stripTrailingZeros().toEngineeringString());
		String newUnit = newUnitMap.getUnitAttribute().toString();
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
	TransformationButton getPreviewButton(EquationNode operation) {
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
					if (TypeEquationXML.Number.equals(base.getType())) {
						if ("0".equals(base.getSymbol())) {
							exponential.replace(TypeEquationXML.Number, "0");
						} else {
							exponential.replace(TypeEquationXML.Number, "1");
						}
					} else {
						// Window.alert("Warning: You are now assuming that "+base.getHTMLString()+" is not equivalent to 0");
						Window.alert("Warning: You are now assuming that the base is not equivalent to 0");
						exponential.replace(TypeEquationXML.Number, "1");
					}

				} else if (exp == 1) {
					exponential.replace(base);

				} else if (exp > 1) {
					if (TypeEquationXML.Term.equals(base.getType())) {
						LinkedList<EquationNode> baseChildren = base.getChildren();
						for (int i = 1; i < exp; i++) {
							base.append(TypeEquationXML.Operation, TypeEquationXML.Operator
									.getMultiply().getSign());
							for (EquationNode baseChild : baseChildren) {
								base.append(baseChild.clone());
							}
						}
						exponential.replace(base);
					} else {
						EquationNode term = exponential.encase(TypeEquationXML.Term);
						int exponIndex = exponential.getIndex();
						term.addBefore(exponIndex, base);
						for (int i = 1; i < exp; i++) {
							term.addBefore(exponIndex, TypeEquationXML.Operation,
									TypeEquationXML.Operator.getMultiply().getSign());
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
	TransformationButton getPreviewButton(EquationNode operation) {
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

				EquationNode innerBase = base.getChildAt(0);
				EquationNode innerExp = base.getChildAt(1);

				EquationNode expTerm = exponent.encase(TypeEquationXML.Term);
				expTerm.addFirst(TypeEquationXML.Operation, Operator.getMultiply()
						.getSign());
				expTerm.addFirst(innerExp);

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
	TransformationButton getPreviewButton(EquationNode operation) {
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

				for (EquationNode baseChild : base.getChildren()) {
					if (TypeEquationXML.Operation.equals(baseChild.getType())) {
						continue;
					}
					baseChild = baseChild.encase(TypeEquationXML.Exponential);
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
	TransformationButton getPreviewButton(EquationNode operation) {
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

				if (TypeEquationXML.Fraction.equals(base.getType())) {
					base.append(base.getFirstChild());

				} else {

					AlgebraicTransformations.reciprocate(exponential);
				}

				String expSymbol = exponent.getSymbol();
				if (Moderator.isInEasyMode && "-1".equals(expSymbol)) {
					exponential.replace(base);
				} else {
					exponent.setSymbol(expSymbol.replace(
							Operator.MINUS.getSign(), ""));
				}

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel(
							"x<sup>-b</sup> = (1/x)<sup>b</sup>",
							Rule.EXPONENT_PROPERTIES);
				}
			}
		});
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.exponentialFlip_check();
	}
}
