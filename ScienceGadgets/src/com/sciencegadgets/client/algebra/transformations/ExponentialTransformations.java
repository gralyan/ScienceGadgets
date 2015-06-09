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
import java.math.MathContext;
import java.util.LinkedList;

import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.specification.NumberQuiz;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;
import com.sciencegadgets.shared.dimensions.UnitMap;

public class ExponentialTransformations extends
		TransformationList<ExponentialTransformButton> {

	private static final long serialVersionUID = -4899066857084144484L;

	EquationNode exponential;
	EquationNode base;
	EquationNode exponent;

	TypeSGET baseType;
	TypeSGET exponentType;

	public ExponentialTransformations(EquationNode exponentialNode) {
		super(exponentialNode);

		this.exponential = exponentialNode;
		this.base = exponential.getChildAt(0);
		this.exponent = exponential.getChildAt(1);

		this.baseType = base.getType();
		this.exponentType = exponent.getType();

		if (add(zeroOrOneBase_check())) {
			return;
		}
		add(exponentialExpand_check());
		add(exponentialEvaluate_check());
		add(exponentialExponentiate_check());
		add(exponentialPropagate_check());
		add(exponentialFlip_check());
		add(unravelExpLog_check());
	}

	ZeroOrOneBaseButton zeroOrOneBase_check() {
		if (!TypeSGET.Number.equals(baseType)) {
			return null;
		}
		if ("0".equals(base.getSymbol())) {
			return new ZeroOrOneBaseButton(this, 0);
		} else if ("1".equals(base.getSymbol())) {
			return new ZeroOrOneBaseButton(this, 1);
		}
		return null;
	}

	ExponentialEvaluateButton exponentialEvaluate_check() {
		if (TypeSGET.Number.equals(baseType)
				&& TypeSGET.Number.equals(exponentType)) {
			// 0 and 1 are taken care of in the expand transformation
			String expValue = exponent.getSymbol();
			if ("0".equals(expValue) || "1".equals(expValue)) {
				return null;
			}
			try {
				new BigDecimal(base.getSymbol());
				int exp = Integer.parseInt(expValue);
				return new ExponentialEvaluateButton(this, (int) exp);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	ExponentialExponentiateButton exponentialExponentiate_check() {
		if (TypeSGET.Exponential.equals(baseType)) {
			return new ExponentialExponentiateButton(this);
		}
		return null;
	}

	ExponentialPropagateButton exponentialPropagate_check() {
		if (TypeSGET.Fraction.equals(baseType)
				|| TypeSGET.Term.equals(baseType)) {
			return new ExponentialPropagateButton(this);
		}
		return null;
	}

	ExponentialFlipButton exponentialFlip_check() {
		if (TypeSGET.Number.equals(exponentType)
				|| TypeSGET.Variable.equals(exponentType)) {
			if (exponent.getSymbol().startsWith(Operator.MINUS.getSign())) {
				return new ExponentialFlipButton(this);
			}
		}
		return null;
	}

	ExponentialExpandButton exponentialExpand_check() {
		if (TypeSGET.Number.equals(exponentType)) {
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
	ExponentialUnravelButton unravelExpLog_check() {
		EquationNode log = exponential.getChildAt(1);
		if (TypeSGET.Log.equals(log.getType())) {
			String logBase = log.getAttribute(MathAttribute.LogBase);
			EquationNode exponentialBase = exponential.getFirstChild();
			if (TypeSGET.Number.equals(exponentialBase.getType())
					&& exponentialBase.getSymbol().equals(logBase)) {
				return new ExponentialUnravelButton(exponential,
						log.getFirstChild(), this);

			}
		}
		return null;
	}
}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////
abstract class ExponentialTransformButton extends TransformationButton {
	final EquationNode exponential;
	final EquationNode base;
	final EquationNode exponent;

	protected boolean reloadAlgebraActivity;
	protected ExponentialTransformations previewContext;

	ExponentialTransformButton(ExponentialTransformations context, String html) {
		super(context);
		addStyleName(CSS.EXPONENTIAL);

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
class ZeroOrOneBaseButton extends ExponentialTransformButton {
	int baseValue;

	ZeroOrOneBaseButton(ExponentialTransformations context, int baseValue) {
		super(context, getHTML(baseValue));
		this.baseValue = baseValue;
	}

	static String getHTML(int baseValue) {
		switch (baseValue) {
		case 0:
			return "0<sup>x</sup> = 0";
		case 1:
			return "1<sup>x</sup> = 1";
		default:
			return "should be base 0 or 1???";
		}
	}

	@Override
	public Badge getAssociatedBadge() {
		switch (baseValue) {
		case 0:
			return Badge.EXPONENT_BASE_ZERO;
		case 1:
			return Badge.EXPONENT_BASE_ONE;
		default:
			return null;
		}
	}

	@Override
	public void transform() {
		exponential.replace(base);

		onTransformationEnd(getHTML(baseValue), base);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.zeroOrOneBase_check();
	}
}

/**
 * b<sup>a</sup> = b.pow(a)<br/>
 * ex: 2<sup>3</sup> = 8
 */
class ExponentialEvaluateButton extends ExponentialTransformButton {
	private int exp;

	public ExponentialEvaluateButton(ExponentialTransformations context,
			final int exp) {
		super(context, "Evaluate Exponential");
		this.isEvaluation = true;
		this.exp = exp;
	}

	@Override
	public boolean meetsAutoTransform() {
		return true;
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.EXPONENTIATE_NUMBERS;
	}

	@Override
	public void transform() {
		final BigDecimal baseValue = new BigDecimal(base.getSymbol());
		final BigDecimal totalValue = baseValue
				.pow(exp, MathContext.DECIMAL128);

		final UnitMap totalUnitMap = new UnitMap(base).getExponential(exp);

		boolean meetsRequirements = Moderator
				.meetsRequirements(Badge.EXPONENTIATE_NUMBERS);

		if (meetsRequirements) {
			evaluateExponential(baseValue, exp, totalValue, totalUnitMap);

		} else if (!reloadAlgebraActivity) {
			// base.replace(TypeSGET.Variable, "#");
			// exponent.replace(TypeSGET.Variable, "#");

		} else {// prompt

			String question = exponential.getHTMLString(true, true);
			NumberQuiz prompt = new NumberQuiz(question, totalValue) {
				@Override
				public void onIncorrect() {
					super.onIncorrect();
				}

				@Override
				public void onCorrect() {
					super.onCorrect();
					evaluateExponential(baseValue, exp, totalValue,
							totalUnitMap);
					Moderator.getStudent().increaseSkill(
							Skill.EXPONENTIATE_NUMBERS, 1);
				}
			};
			prompt.appear();
		}
	}

	private void evaluateExponential(BigDecimal baseValue, int expValue,
			BigDecimal totalValue, UnitMap newUnitMap) {

		EquationNode evaluated = exponential.replace(TypeSGET.Number,
				totalValue.stripTrailingZeros().toEngineeringString());
		String newUnit = newUnitMap.getUnitAttribute().toString();
		evaluated.setAttribute(MathAttribute.Unit, newUnit);

		onTransformationEnd(baseValue.stripTrailingZeros()
				.toEngineeringString()
				+ " ^ "
				+ expValue
				+ " = "
				+ totalValue.stripTrailingZeros().toEngineeringString(),evaluated);
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

	private int exp;

	ExponentialExpandButton(ExponentialTransformations context, final int exp) {
		super(context, "Expand Exponential");
		this.exp = exp;
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.EXPONENT_EXPAND;
	}

	@Override
	public void transform() {
		if (exp == 0) {
			if (TypeSGET.Number.equals(base.getType())) {
				if ("0".equals(base.getSymbol())) {
					exponential.replace(TypeSGET.Number, "0");
				} else {
					exponential.replace(TypeSGET.Number, "1");
				}
			} else {
				if (reloadAlgebraActivity) {
					Window.alert("Warning: You are now assuming that the base is not equivalent to 0");
				}
				exponential.replace(TypeSGET.Number, "1");
			}

		} else if (exp == 1) {
			exponential.replace(base);

		} else if (exp > 1) {
			if (TypeSGET.Term.equals(base.getType())) {
				LinkedList<EquationNode> baseChildren = base.getChildren();
				for (int i = 1; i < exp; i++) {
					base.append(TypeSGET.Operation, TypeSGET.Operator
							.getMultiply().getSign());
					for (EquationNode baseChild : baseChildren) {
						base.append(baseChild.clone());
					}
				}
				exponential.replace(base);
			} else {
				EquationNode term = exponential.encase(TypeSGET.Term);
				int exponIndex = exponential.getIndex();
				term.addBefore(exponIndex, base);
				for (int i = 1; i < exp; i++) {
					term.addBefore(exponIndex, TypeSGET.Operation,
							TypeSGET.Operator.getMultiply().getSign());
					term.addBefore(exponIndex, base.clone());
				}
				exponential.remove();
			}
		}

		onTransformationEnd("Expand", base);
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
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.EXPONENT_EXPONENTIATE;
	}

	@Override
	public void transform() {
		EquationNode innerBase = base.getChildAt(0);
		EquationNode innerExp = base.getChildAt(1);

		EquationNode expTerm = exponent.encase(TypeSGET.Term);
		expTerm.addFirst(TypeSGET.Operation, Operator.getMultiply().getSign());
		expTerm.addFirst(innerExp);

		base.replace(innerBase);

		onTransformationEnd("(x<sup>a</sup>)<sup>b</sup> = x<sup>a &middot; b</sup>", innerBase);
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
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.EXPONENT_EXPRESSION;
	}

	@Override
	public void transform() {
		for (EquationNode baseChild : base.getChildren()) {
			if (TypeSGET.Operation.equals(baseChild.getType())) {
				continue;
			}
			baseChild = baseChild.encase(TypeSGET.Exponential);
			baseChild.append(exponent.clone());
		}
		exponential.replace(base);

		onTransformationEnd("(x*y)<sup>a</sup> = x<sup>a</sup>*y<sup>a</sup>", base);
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
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.EXPONENT_NEGATIVE;
	}

	@Override
	public void transform() {
		if (TypeSGET.Fraction.equals(base.getType())) {
			base.append(base.getFirstChild());

		} else {

			AlgebraicTransformations.reciprocate(exponential);
		}

		String expSymbol = exponent.getSymbol();
		if (Moderator.meetsRequirement(Badge.EXP_ONE) && "-1".equals(expSymbol)) {
			exponential.replace(base);
		} else {
			exponent.setSymbol(expSymbol.replace(Operator.MINUS.getSign(), ""));
		}

		onTransformationEnd("x<sup>-b</sup> = (1/x)<sup>b</sup>", base);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.exponentialFlip_check();
	}
}

/**
 * b<sup>log<sub>b</sub>(x)</sup> = x<br/>
 */
class ExponentialUnravelButton extends ExponentialTransformButton {

	private EquationNode toReplace;
	private EquationNode replacement;

	public ExponentialUnravelButton(final EquationNode toReplace,
			final EquationNode replacement, ExponentialTransformations context) {
		super(context, replacement.getHTMLString(true, true));
		this.toReplace = toReplace;
		this.replacement = replacement;
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.EXPONENTIAL_INVERSE;
	}

	@Override
	public void transform() {
		String changeComment = toReplace.getHTMLString(true, true) + " = "
				+ replacement.getHTMLString(true, true);
		toReplace.replace(replacement);
		onTransformationEnd(changeComment, replacement);
	}
}