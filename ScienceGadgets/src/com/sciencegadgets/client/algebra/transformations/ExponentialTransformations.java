package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedHashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.UnitUtil;

public class ExponentialTransformations {

	public static void assign(MathNode exponentialNode) {
		try {
			MathNode exponential = exponentialNode;
			MathNode base = exponential.getChildAt(0);
			MathNode exponent = exponential.getChildAt(1);
			TypeML baseType = base.getType();
			TypeML exponentType = exponent.getType();

			base: switch (baseType) {
			case Exponential:
				// TODO
				break;
			case Fraction:
				// TODO

				break;
			}

			exp: switch (exponentType) {
			case Number:

				expandExponential_check(base, exponent, exponential);

				base: switch (baseType) {
				case Number:
					AlgebraActivity.addTransformation(new EvaluateExponential_Button(
							base, exponent, exponential));
					break base;
				}
				break exp;

			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
			JSNICalls
					.error("A number node couldn't be parsed: " + e.toString());
		}
	}

	private static void expandExponential_check(MathNode base,
			MathNode exponent, MathNode exponential) {
		try {
			int exp = Integer.parseInt(exponent.getSymbol());
			// Integer from 0-10 inclusive, more than 10 is just too many
			if (exp < 10 && exp >= 0) {
				AlgebraActivity.addTransformation(new ExpandExponential_Button(
						(int) exp, base, exponent, exponential));
			}
		} catch (NumberFormatException e) {
			return;
		}

	}
}

/**
 * b<sup>a</sup> = b.pow(a)<br/>
 * ex: 2<sup>3</sup> = 8
 */
class EvaluateExponential_Button extends Button {
	private MathNode base, exponent, exponential;

	public EvaluateExponential_Button(final MathNode base,
			final MathNode exponent, final MathNode exponential) {
		super();
		this.base = base;
		this.exponent = exponent;
		this.exponential = exponential;

		setHTML("Evaluate Exponential");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				final BigDecimal baseValue = new BigDecimal(base.getSymbol());
				final int expValue = Integer.parseInt(exponent.getSymbol());
				final BigDecimal totalValue = baseValue.pow(expValue,
						MathContext.DECIMAL32);

				final LinkedHashMap<String, Integer> totalUnitMap = UnitUtil
						.getUnitMap(base);
				for (String unit : totalUnitMap.keySet()) {
					totalUnitMap.put(unit, ((Integer) totalUnitMap.get(unit))
							* expValue);
				}

				if (AlgebraActivity.isInEasyMode) {
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
			BigDecimal totalValue, LinkedHashMap<String, Integer> newUnitMap) {

		exponential.highlight();

		MathNode evaluated = exponential.replace(TypeML.Number, totalValue
				.stripTrailingZeros().toEngineeringString());
		String newUnit = UnitUtil.getUnitAttribute(newUnitMap);
		evaluated.setAttribute(MathAttribute.Unit, newUnit);

		AlgebraActivity.reloadEquationPanel(baseValue.stripTrailingZeros()
				.toEngineeringString()
				+ " ^ "
				+ expValue
				+ " = "
				+ totalValue.stripTrailingZeros().toEngineeringString(),
				Rule.EXPONENT_PROPERTIES);
	}
}

/**
 * x<sup>a</sup> = x·x·...x<sub>a</sub><br/>
 * ex: (y-1)<sup>5</sup> = (y-1)·(y-1)·(y-1)·(y-1)·(y-1)
 */
class ExpandExponential_Button extends Button {
	private MathNode base, exponent, exponential;
	private int exp;

	ExpandExponential_Button(final int exp, final MathNode base,
			MathNode exponent, final MathNode exponential) {
		this.exp = exp;
		this.base = base;
		this.exponent = exponent;
		this.exponential = exponential;

		setHTML("Expand Exponential");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (exp == 0) {
					if (!"0".equals(base.getSymbol())) {
						Window.alert("Warning: You are now assuming that the base is not equivalent to 0");
						exponential.replace(TypeML.Number, "1");
					} else {
						exponential.replace(TypeML.Number, "0");
					}

				} else if (exp == 1) {
					exponential.replace(base);

				} else if (exp > 1) {
					MathNode term = exponential.encase(TypeML.Term);
					int exponIndex = exponential.getIndex();
					term.addAfter(exponIndex, base);
					for (int i = 1; i < exp; i++) {
						term.addAfter(exponIndex, TypeML.Operation,
								TypeML.Operator.getMultiply().getSign());
						term.addAfter(exponIndex + 1, base.clone());
					}
					exponential.remove();
				}

				AlgebraActivity.reloadEquationPanel("Expand",
						Rule.EXPONENT_PROPERTIES);
			}
		});
	}

}