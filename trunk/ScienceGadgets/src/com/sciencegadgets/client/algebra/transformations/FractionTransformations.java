package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;

public class FractionTransformations extends
		TransformationList<FractionTransformButton> {

	private static final long serialVersionUID = 6836733328945521750L;

	EquationNode fraction;
	EquationNode numerator;
	EquationNode denominator;

	TypeSGET numeratorType;
	TypeSGET denominatorType;

	public FractionTransformations(EquationNode fraction) {
		super(fraction);
		this.fraction = fraction;
		this.numerator = fraction.getChildAt(0);
		this.denominator = fraction.getChildAt(1);
		this.numeratorType = numerator.getType();
		this.denominatorType = denominator.getType();

		add(denominatorFlip_check());
		add(simplifyFraction_check());

	}

	public DenominatorFlipButton denominatorFlip_check() {
		return new DenominatorFlipButton(this);
	}

	public SimplifyFractionButton simplifyFraction_check() {
		try {
			if (SIMPLIFY_FRACTION(numerator, denominator, false)) {
				return new SimplifyFractionButton(this);
			} else {
				return null;
			}
		} catch (ArithmeticException|NumberFormatException e) {
			return null;
		}
	}

	public static boolean SIMPLIFY_FRACTION(EquationNode numerator,
			EquationNode denominator, boolean execute)
			throws ArithmeticException, NumberFormatException {
		Integer numValue = Integer.parseInt(new BigDecimal(numerator
				.getSymbol()).toPlainString());
		Integer denValue = Integer.parseInt(new BigDecimal(denominator
				.getSymbol()).toPlainString());

		// Whole Division
		if ((numValue > denValue && numValue % denValue == 0)
				|| (denValue > numValue && denValue % numValue == 0)) {
			if (execute) {
				int division = numValue > denValue ? numValue / denValue
						: denValue / numValue;
				EquationNode fraction = numerator.getParent();
				fraction.replace(TypeSGET.Number, division + "");
			}
			return true;
		}

		// Division by common factor
		LinkedHashSet<Integer> numFactors, denFactors;
		numFactors = AlgebraicTransformations.FIND_PRIME_FACTORS(numValue);
		denFactors = AlgebraicTransformations.FIND_PRIME_FACTORS(denValue);

		LinkedHashSet<Integer> matches = new LinkedHashSet<Integer>();
		a: for (Integer num : numFactors) {
			for (Integer den : denFactors) {
				if (num.equals(den) && !matches.contains(den)) {
					matches.add(den);
					continue a;
				}
			}
		}

		if (matches.size() < 2) {// "1" is always a factor
			System.out.println("no simpl: " + numValue + " " + denValue);
			return false;
		} else if (!execute) {
			return true;
		}
		Integer numNewValue = numValue.intValue();
		Integer denNewValue = denValue.intValue();
		for (Integer match : matches) {
			numNewValue /= match;
			denNewValue /= match;
		}
		numerator.setSymbol(numNewValue.toString());
		denominator.setSymbol(denNewValue.toString());
		System.out.println("simpl: " + numValue + "-" + numNewValue + " "
				+ denValue + "-" + denNewValue);

		return true;
	}

}

abstract class FractionTransformButton extends TransformationButton {

	EquationNode fraction;
	EquationNode numerator;
	EquationNode denominator;

	TypeSGET numeratorType;
	TypeSGET denominatorType;

	protected boolean reloadAlgebraActivity;

	public FractionTransformButton(String html, FractionTransformations context) {
		super(html, context);
		this.fraction = context.fraction;
		this.numerator = context.numerator;
		this.denominator = context.denominator;

		this.numeratorType = context.numeratorType;
		this.denominatorType = context.denominatorType;

		this.reloadAlgebraActivity = context.reloadAlgebraActivity;
	}

}

class SimplifyFractionButton extends FractionTransformButton {

	public SimplifyFractionButton(FractionTransformations context) {
		super("Simplify Fraction", context);
	}

	@Override
	public void transform() {
		FractionTransformations.SIMPLIFY_FRACTION(numerator, denominator, true);

		numerator.highlight();
		denominator.highlight();

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel("Simplify Fraction",
					Rule.SIMPLIFY_FRACTIONS);
		}
	}

}

/**
 * x / (y/z) = x &middot; (z/y)<br/>
 * x / y = x &middot; (1/y)
 */
class DenominatorFlipButton extends FractionTransformButton {
	DenominatorFlipButton(FractionTransformations context) {
		super("Flip Denominator", context);
	}

	@Override
	public void transform() {

		denominator.highlight();

		EquationNode frac = denominator;
		if (!TypeSGET.Fraction.equals(denominator.getType())) {
			frac = denominator.encase(TypeSGET.Fraction);
			frac.append(TypeSGET.Number, "1");
		}
		// Flip
		frac.append(frac.getChildAt(0));

		EquationNode parentFraction = frac.getParent();
		EquationNode grandParent = parentFraction.getParent();
		int index = parentFraction.getIndex();

		if (!TypeSGET.Term.equals(grandParent.getType())) {
			grandParent = parentFraction.encase(TypeSGET.Term);
			index = 0;
		}

		grandParent.addBefore(index, parentFraction.getChildAt(1));
		grandParent.addBefore(index, TypeSGET.Operation, Operator.getMultiply()
				.getSign());
		grandParent.addBefore(index, parentFraction.getChildAt(0));

		parentFraction.remove();

		Moderator.reloadEquationPanel("Multiply by Resiprocal",
				Rule.FRACTION_DIVISION);
	}
}
