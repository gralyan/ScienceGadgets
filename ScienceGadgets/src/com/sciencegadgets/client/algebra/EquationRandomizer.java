package com.sciencegadgets.client.algebra;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Random;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;

public class EquationRandomizer {

	/**
	 * Replaces the randomness specifications in the equation with an
	 * appropriate randomly generated number. The specifications are in the form
	 * of <b>x_x_x_x</b> where each <b>x</b> is respectively means: <br/>
	 * Negativity - (A)lways, (S)ometimes, or (N)ever <br/>
	 * Lower Bound <br/>
	 * Upper Bound <br/>
	 * Decimals - how accurate
	 * 
	 * @param mathML
	 * @param randomize - true to generate random number, false to revert to the random symbol
	 * @return
	 */
	public static Element randomizeNumbers(Element mathML, boolean randomize) {

		NodeList<com.google.gwt.dom.client.Element> variables = mathML
				.getElementsByTagName("mn");

		for (int i = 0; i < variables.getLength(); i++) {
			com.google.gwt.dom.client.Element var = variables.getItem(i);
			String varRandomness = var.getAttribute("data-randomness");

			if (varRandomness.contains(RandomSpecPanel.DELIMITER)) {
				if(randomize){
				randomizeNumbers(var, varRandomness);
				}else{
					unrandomizeNumbers(var);
				}
			}
		}
		return mathML;
	}

	private static void randomizeNumbers(Element var, String varRandomness) {

		// negative_lowerBound_upperBound_decimal place
		String[] specs = varRandomness
				.split(RandomSpecPanel.DELIMITER);

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
			randomBigD = randomBigD.setScale(decPlace,
					BigDecimal.ROUND_DOWN);

			var.setInnerText(randomBigD.toString());

		} catch (NumberFormatException e) {
			e.printStackTrace();
			var.setInnerText(((int) (Math.random() * 10) + 1) + "");
		} catch (ArithmeticException e) {
			e.printStackTrace();
			var.setInnerText(((int) (Math.random() * 10) + 1) + "");
		}
	}
	
	private static void unrandomizeNumbers(Element var) {
		var.setInnerText(RandomSpecPanel.RANDOM_SYMBOL);
	}

}