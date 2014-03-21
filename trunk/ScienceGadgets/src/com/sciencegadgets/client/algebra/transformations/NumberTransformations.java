package com.sciencegadgets.client.algebra.transformations;

import java.util.LinkedHashSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;

public class NumberTransformations extends TransformationList {
	private static final long serialVersionUID = -7824994598553847333L;

	EquationNode numberNode;

	NumberTransformations(EquationNode numberNode) {
		super(numberNode);

		this.numberNode = numberNode;

		add(factorizeNumbers_check());
		add(unitConversion_check());
	}

	/**
	 * List the factors of the number as buttons to choose in a prompt
	 */
	private TransformationButton factorizeNumbers_check() {
		Integer number;
		try {
			number = Integer.parseInt(numberNode.getSymbol());
		} catch (NumberFormatException e) {
			return null;
		}
		if (number == 1) {
			return null;
		}

		return new FactorNumberPromptButton(number, this);
	}

	/**
	 * Checks if there is a unit attribute
	 */
	private TransformationButton unitConversion_check() {
		if (!"".equals(numberNode.getUnitAttribute().toString())) {
			return new UnitConversionButton(this);
		}
		return null;
	}

}

// /////////////////////////////////////////////////////////////
// Transformation Buttons
// ////////////////////////////////////////////////////////////

class NumberTransformationButton extends TransformationButton{
	
	protected EquationNode numberNode;

	NumberTransformationButton(final NumberTransformations context){
		super(context);
		
		this.numberNode = context.numberNode;
	}
}

/**
 * Decompose - factor number
 */
class FactorNumberPromptButton extends NumberTransformationButton {
	public FactorNumberPromptButton(final Integer number, final NumberTransformations context) {
		super(context);
		
		setHTML("Factor");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				LinkedHashSet<Integer> primeFactors = findPrimeFactors(number);

				Prompt prompt = new Prompt(false);
				Label title = new Label();
				title.setText("" + number);
				title.setHeight("20%");
				prompt.add(title);
				for (Integer factor : primeFactors) {
					prompt.add(new FactorNumberButton(factor, number / factor,
							prompt, context));
				}
				prompt.appear();
			}
		});

	}

	LinkedHashSet<Integer> findPrimeFactors(Integer number) {
		LinkedHashSet<Integer> factors = new LinkedHashSet<Integer>();

		if (number < 0) {
			number = Math.abs(number);
		}
		factors.add(1);

		int start = 2;
		byte inc = 1;
		if (number % 2 == 1) {// odd numbers can't have even factors
			start = 3;
			inc = 2;
		}
		for (int i = start; i <= Math.sqrt(number); i = i + inc) {
			if (number % i == 0) {
				factors.add(i);
			}
		}
		return factors;
	}

	class FactorNumberButton extends NumberTransformationButton {
		FactorNumberButton(final int factor, final int cofactor,
				final Prompt prompt, final NumberTransformations context) {
			super(context);

			setHTML(factor + " " + Operator.getMultiply().getSign() + " "
					+ cofactor);
			setSize("50%", "50%");

			this.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {

					String original = numberNode.getSymbol();
					int factored = Integer.parseInt(original) / factor;

					numberNode.highlight();

					EquationNode parent = numberNode.encase(TypeEquationXML.Term);
					int index = numberNode.getIndex();
					parent.addBefore(index, TypeEquationXML.Operation, Operator
							.getMultiply().getSign());
					parent.addBefore(index, TypeEquationXML.Number, factor + "");

					numberNode.setSymbol(factored + "");

					Moderator.reloadEquationPanel(original + " = " + factor
							+ " " + Operator.getMultiply().getSign() + " "
							+ factored, Rule.INTEGER_FACTORIZATION);

					prompt.disappear();
				}
			});
		}
	}
}

/**
 * Switches to unit conversion mode
 * 
 */
class UnitConversionButton extends NumberTransformationButton {
	UnitConversionButton(final NumberTransformations context) {
		super(context);

		setHTML("Convert Units");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Moderator.switchToConversion(numberNode);
			}
		});
	}
}
