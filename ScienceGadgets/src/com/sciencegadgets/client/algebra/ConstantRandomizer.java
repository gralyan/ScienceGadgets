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
package com.sciencegadgets.client.algebra;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;

public class ConstantRandomizer {

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
	public static void randomizeNumbers(EquationTree mTree, boolean randomize) {

		LinkedList<EquationNode> numbers = mTree
				.getNodesByType(TypeSGET.Number);

		for (EquationNode num : numbers) {
			String varRandomness = num.getAttribute(MathAttribute.Randomness);

			if (varRandomness.contains(RandomSpecPanel.RANDOMNESS_DELIMITER)) {

				if (randomize) {
					if (RandomSpecPanel.RANDOM_SYMBOL.equals(num.getSymbol())) {
						BigDecimal randonNumber = getRandomNumber(varRandomness);
						num.setSymbol(randonNumber.toPlainString());
					} else {
						num.setAttribute(MathAttribute.Randomness, null);
					}
				} else {
					num.setSymbol(RandomSpecPanel.RANDOM_SYMBOL);
				}
			}
		}
	}

	public static HashMap<String, String> randomizeNumbers(Element xml,
			boolean randomize) {

		HashMap<String, String> randomMap = new HashMap<String, String>();

		NodeList<Element> numbers = xml.getElementsByTagName(TypeSGET.Number
				.getTag());
		NodeList<Element> variable = xml.getElementsByTagName(TypeSGET.Variable
				.getTag());

		for (int i = 0; i < variable.getLength(); i++) {
			Element var = variable.getItem(i);
			randomMap.put(var.getAttribute("id"), var.getInnerText());

		}
		for (int i = 0; i < numbers.getLength(); i++) {
			Element num = numbers.getItem(i);

			String randomness = num.getAttribute(MathAttribute.Randomness
					.getAttributeName());

			if (randomness.contains(RandomSpecPanel.RANDOMNESS_DELIMITER)) {

				if (randomize) {
					if (RandomSpecPanel.RANDOM_SYMBOL
							.equals(num.getInnerText())) {
						BigDecimal randonNumber = getRandomNumber(randomness);
						randomMap.put(num.getAttribute("id"),
								randonNumber.toPlainString());
						num.setInnerText(randonNumber.toPlainString());
					} else {
						num.setAttribute(
								MathAttribute.Randomness.getAttributeName(),
								null);
					}
				} else {
					num.setInnerText(RandomSpecPanel.RANDOM_SYMBOL);
				}
			}
		}
		return randomMap;
	}

	public static HashMap<Parameter, String> insertRandomProvided(
			HashMap<Parameter, String> parameterMap) {

		String randomProvidedString = parameterMap
				.get(Parameter.randomprovided);
		if(randomProvidedString == null || "".equals(randomProvidedString)) {
			return parameterMap;
		}
		String[] randomProvidedArray = randomProvidedString
				.split(URLParameters.RANDOM_PROVIDED_DELIMITER);

		String equationString = parameterMap.get(Parameter.equation);
		Element equationXML = new HTML(equationString).getElement()
				.getFirstChildElement();

		NodeList<Element> numberNodes = equationXML
				.getElementsByTagName(TypeSGET.Number.getTag());
		LinkedList<Element> nodesExpectingProvidedNumber = new LinkedList<Element>();

		for (int i = 0; i < numberNodes.getLength(); i++) {
			Element num = numberNodes.getItem(i);
			String randomness = num.getAttribute(MathAttribute.Randomness
					.getAttributeName());
			if (randomness.contains(RandomSpecPanel.RANDOM_PROVIDED)) {
				nodesExpectingProvidedNumber.add(num);
			}
		}

		if (randomProvidedArray.length == nodesExpectingProvidedNumber.size()) {

			for (int i = 0; i < nodesExpectingProvidedNumber.size(); i++) {
				Element expecting = nodesExpectingProvidedNumber.get(i);
				String provided = randomProvidedArray[i];
				expecting.setInnerText(provided);
				
				expecting.removeAttribute(MathAttribute.Randomness
					.getAttributeName());
			}
			
			parameterMap.remove(Parameter.randomprovided);
			parameterMap.put(Parameter.equation, JSNICalls.elementToString(equationXML));

		} else {
			String nodeAvailableList = "";
			for (Element nodeAvailable : nodesExpectingProvidedNumber) {
				nodeAvailableList = nodeAvailableList + "\n"
						+ JSNICalls.elementToString(nodeAvailable);
			}
			JSNICalls
					.error("Random numbers provided don't match the spots available:\n"
							+ randomProvidedArray.length
							+ " provided: "
							+ randomProvidedString
							+ "\n"
							+ nodesExpectingProvidedNumber.size()
							+ " available: " + nodeAvailableList);
		}
		return parameterMap;
	}

	private static BigDecimal getRandomNumber(String varRandomness) {

		// negative_lowerBound_upperBound_decimal place
		String[] specs = varRandomness
				.split(RandomSpecPanel.RANDOMNESS_DELIMITER);
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

			return randomBigD;

		} catch (NumberFormatException | ArithmeticException e) {
			e.printStackTrace();
			return new BigDecimal((Math.random() * 10) + 1);
		}
	}
	
}
