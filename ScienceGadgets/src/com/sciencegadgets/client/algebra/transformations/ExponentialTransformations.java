package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.math.MathContext;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.TypeML;

public class ExponentialTransformations {
	static MathNode base;
	static MathNode exponent;
	static MathNode exponential;

	public static void assign(MathNode exponentialNode) {
		try {
			exponential = exponentialNode;
			base = exponential.getChildAt(0);
			exponent = exponential.getChildAt(1);
			TypeML baseType = base.getType();
			TypeML exponentType = exponent.getType();

			exp: switch (exponentType) {
			case Number:
				
				expandExponential_check();

				base: switch (baseType) {
				case Number:
					evaluateExponential_prompt();
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

	private static void evaluateExponential_prompt() {

		AlgebraActivity.algTransformMenu.add(new Button("Expand Exponential", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				
				final BigDecimal baseValue = new BigDecimal(base.getSymbol());
				final int expValue = Integer.parseInt(exponent.getSymbol());
				final BigDecimal totalValue = baseValue.pow(expValue,
						MathContext.DECIMAL32);
				
				if (AlgebraActivity.isInEasyMode) {
					evaluateExponential(baseValue, expValue, totalValue);
					
				} else {// prompt
					
					String question = exponential.getHTMLString() + " = ";
					NumberPrompt prompt = new NumberPrompt(question, totalValue) {
						@Override
						void onCorrect() {
							evaluateExponential(baseValue, expValue, totalValue);
						}
					};
					prompt.appear();
				}
				
			}
		}));
		
	}

	private static void evaluateExponential(BigDecimal baseValue, int expValue,
			BigDecimal totalValue) {

		exponential.highlight();

		exponential.replace(TypeML.Number, totalValue.stripTrailingZeros()
				.toEngineeringString());

		AlgebraActivity.reloadEquationPanel(baseValue.stripTrailingZeros()
				.toEngineeringString()
				+ " ^ "
				+ expValue
				+ " = "
				+ totalValue.stripTrailingZeros().toEngineeringString(),
				Rule.EXPONENT_PROPERTIES);
	}

	private static void expandExponential_check() {
		final double exp = Double.parseDouble(exponent.getSymbol());
		// Integer between 0-11
		if ((exp == Math.floor(exp)) && !Double.isInfinite(exp) && exp < 11
				&& exp > 0) {
			
			AlgebraActivity.algTransformMenu.add(new Button("Expand Exponential", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					MathNode term = exponential.getParent().addBefore(
							exponential.getIndex(), TypeML.Term, "");
					MathNode base = term.append(exponential.getChildAt(0));
					for (int i = 1; i < exp; i++) {
						term.append(TypeML.Operation, TypeML.Operator.getMultiply()
								.getSign());
						term.append(base.clone());
					}
					exponential.remove();
					AlgebraActivity.reloadEquationPanel("Expand Exponent",
							Rule.EXPONENT_PROPERTIES);
				}
			}));
		}
	}

}
