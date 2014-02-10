package com.sciencegadgets.client.algebra;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.google.gwt.user.client.Random;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;

public class EquationRandomizer {

	/**
	 * Replaces the randomness specifications in the equation with an
	 * appropriate randomly generated number. The specifications are in the form
	 * of <b>x_x_x_x</b> where each <b>x</b> is respectively means: <br/>
	 * Negativity - (A)lways, (S)ometimes, or (N)ever <br/>
	 * Lower Bound <br/>
	 * Upper Bound <br/>
	 * Decimals - how many decimal places
	 * 
	 * @param randomize
	 *            - true to generate random number, false to revert to the
	 *            random symbol
	 * @return
	 */
	public static void randomizeNumbers(MathTree mTree, boolean randomize) {

		ArrayList<MathNode> variables = mTree.getNodesByType(TypeML.Number);

		for (MathNode var : variables) {
			String varRandomness = var.getAttribute(MathAttribute.Randomness);

			if (varRandomness.contains(RandomSpecPanel.DELIMITER)) {
				if (randomize) {
					String randonNumber = getRandomNumber(varRandomness);
					var.setSymbol(randonNumber);
				} else {
					var.setSymbol(RandomSpecPanel.RANDOM_SYMBOL);
				}
			}
		} 
	}

	private static String getRandomNumber(String varRandomness) {

		// negative_lowerBound_upperBound_decimal place
		String[] specs = varRandomness.split(RandomSpecPanel.DELIMITER);
		try {
			String negativity = specs[0];
			double lowerBound = Double.parseDouble(specs[1]);
			double upperBound = Double.parseDouble(specs[2]);
			int decPlace = Integer.parseInt(specs[3]);

			// Randomize within bounds
			double randomNumber = (Math.random() * (upperBound - lowerBound))
					+ lowerBound;

			// Make negative
			if (RandomSpecPanel.ALWAYS.equals(negativity)
					|| RandomSpecPanel.SOMETIMES.equals(negativity)
					&& Random.nextBoolean()) {
				randomNumber *= -1;
			}

			BigDecimal randomBigD = new BigDecimal(randomNumber);
			randomBigD = randomBigD.setScale(decPlace, BigDecimal.ROUND_DOWN);

			return randomBigD.toString();

		} catch (NumberFormatException e) {
			e.printStackTrace();
			return ((int) (Math.random() * 10) + 1) + "";
		} catch (ArithmeticException e) {
			e.printStackTrace();
			return ((int) (Math.random() * 10) + 1) + "";
		}
	}

}
