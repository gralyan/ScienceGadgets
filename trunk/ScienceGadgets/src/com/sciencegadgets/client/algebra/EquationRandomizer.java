package com.sciencegadgets.client.algebra;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Random;
import com.sciencegadgets.client.algebra.edit.RandomSpecification;

public class EquationRandomizer {

	/**
	 * Replaces the randomness specifications in the equation with an
	 * appropriate randomly generated number. The specifications are in the form
	 * of <b>x-x-x-x</b> where each <b>x</b> is respectively means: <br/>
	 * Negativity - (A)lways, (S)ometimes, or (N)ever <br/>
	 * Lower Bound <br/>
	 * Upper Bound <br/>
	 * Decimals - how accurate
	 * 
	 * @param mathML
	 * @return
	 */
	public static Element randomizeNumbers(Element mathML) {

		// HTML randomizedEquation = new HTML(equation);

		NodeList<com.google.gwt.dom.client.Element> variables = mathML
				.getElementsByTagName("mn");

		for (int i = 0; i < variables.getLength(); i++) {
			com.google.gwt.dom.client.Element var = variables.getItem(i);
			// String varText = var.getInnerText();
			String varRandomness = var.getAttribute("data-randomness");

			if (varRandomness.contains("-")) {

				// negative - lowerBound - upperBound - decimal place
				String[] specs = varRandomness.split("-");

				try {
					String negativity = specs[0];
					double lowerBound = Double.parseDouble(specs[1]);
					double upperBound = Double.parseDouble(specs[2]);
					int decPlace = Integer.parseInt(specs[3]);

					// Randomize within bounds
					double randomNumber = (Math.random() * (upperBound - lowerBound))
							+ lowerBound;

					// Make negative
					if (RandomSpecification.ALWAYS.equals(negativity)
							|| RandomSpecification.SOMETIMES.equals(negativity)
							&& Random.nextBoolean()) {
						randomNumber *= -1;
					}

					BigDecimal randomBigD = new BigDecimal(randomNumber);
					randomBigD = randomBigD.setScale(decPlace,
							BigDecimal.ROUND_DOWN);

					var.setInnerText(randomBigD.toString() + "");

				} catch (NumberFormatException e) {
					e.printStackTrace();
					var.setInnerText(((int) (Math.random() * 10) + 1) + "");
				} catch (ArithmeticException e) {
					e.printStackTrace();
					var.setInnerText(((int) (Math.random() * 10) + 1) + "");
				}
			}
		}
		return mathML;
	}
}
