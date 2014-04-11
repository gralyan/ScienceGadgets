package com.sciencegadgets.client.algebra.transformations;

import java.util.LinkedHashSet;

import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;

public class NumberTransformations extends
		TransformationList<NumberTransformationButton> {
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
	private FactorNumberPromptButton factorizeNumbers_check() {
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
	private UnitConversionButton unitConversion_check() {
		if (!"".equals(numberNode.getUnitAttribute().toString())) {
			return new UnitConversionButton(this);
		}
		return null;
	}

}

// /////////////////////////////////////////////////////////////
// Transformation Buttons
// ////////////////////////////////////////////////////////////

abstract class NumberTransformationButton extends TransformationButton {

	protected EquationNode numberNode;

	NumberTransformationButton(String html, final NumberTransformations context) {
		super(html, context);

		this.numberNode = context.numberNode;
	}
}

/**
 * Decompose - factor number
 */
class FactorNumberPromptButton extends NumberTransformationButton {
	private Integer number;
	private NumberTransformations context;

	public FactorNumberPromptButton(final Integer number,
			final NumberTransformations context) {
		super("Factor", context);
		this.number = number;
		this.context = context;
	}

	@Override
	public
	void transform() {
		LinkedHashSet<Integer> primeFactors = AlgebraicTransformations.FIND_PRIME_FACTORS(number);

		Prompt prompt = new Prompt(false);
		Label title = new Label();
		title.setText("" + number);
		title.setHeight("20%");
		prompt.add(title);
		for (Integer factor : primeFactors) {
			prompt.add(new FactorNumberButton(factor, number / factor, prompt,
					context));
		}
		prompt.appear();
	}

	class FactorNumberButton extends NumberTransformationButton {
		private int factor;
		private Prompt prompt;

		FactorNumberButton(final int factor, final int cofactor,
				final Prompt prompt, final NumberTransformations context) {
			super(factor + " " + Operator.getMultiply().getSign() + " "
					+ cofactor, context);
			this.factor = factor;
			this.prompt = prompt;
			setSize("50%", "50%");
		}

		@Override
		public
		void transform() {
			String original = numberNode.getSymbol();
			int factored = Integer.parseInt(original) / factor;

			numberNode.highlight();

			EquationNode parent = numberNode.encase(TypeSGET.Term);
			int index = numberNode.getIndex();
			parent.addBefore(index, TypeSGET.Operation, Operator
					.getMultiply().getSign());
			parent.addBefore(index, TypeSGET.Number, factor + "");

			numberNode.setSymbol(factored + "");

			Moderator.reloadEquationPanel(original + " = " + factor + " "
					+ Operator.getMultiply().getSign() + " " + factored,
					Skill.INTEGER_FACTORIZATION);

			prompt.disappear();
		}
	}

}

/**
 * Switches to unit conversion mode
 * 
 */
class UnitConversionButton extends NumberTransformationButton {
	UnitConversionButton(final NumberTransformations context) {
		super("Convert Units", context);
	}

	@Override
	public
	void transform() {
		Moderator.switchToConversion(numberNode);
	}
}
