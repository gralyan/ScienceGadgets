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
package com.sciencegadgets.client.algebra.transformations.specification;

import java.math.BigDecimal;
import java.math.MathContext;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.KeyPadNumerical;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.Quiz;
import com.sciencegadgets.client.ui.SymbolDisplay;

public abstract class NumberQuiz extends Quiz {

	static final BigDecimal ACCEPTABLE_ERROR = new BigDecimal(".01");
	private static final int same = 0;
	private static final int lessThan = -1;
	final KeyPadNumerical keyPad = new KeyPadNumerical();
	final SymbolDisplay display = keyPad.getSymbolDisplay();

	public NumberQuiz(String question, final BigDecimal totalValue) {

		HTML questionDisplay = new HTML(question + " = ");
		questionDisplay.addStyleName(CSS.LAYOUT_ROW+" "+CSS.DOUBLE_FONT_SIZE);
		add(questionDisplay);

		display.addStyleName(CSS.LAYOUT_ROW+" "+CSS.DOUBLE_FONT_SIZE);
		display.setWidth("3em");

		add(display);
		add(keyPad);

		final HTML incorrectResponse = new HTML("<b>Incorrect</b>");

		addOkHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					BigDecimal inputValue = new BigDecimal(display.getText());
					
					// acceptable error
					BigDecimal error;
					if(totalValue.compareTo(new BigDecimal(0)) == same) {
						error = totalValue.subtract(inputValue).abs();
					}else {
						error = inputValue.divide(totalValue, MathContext.DECIMAL32).subtract(new BigDecimal("1")).abs();
					}

					if (error.compareTo(ACCEPTABLE_ERROR) == lessThan) {
						// correct
						disappear();
						onCorrect();
					} else {
						// incorrect
						incorrectResponse.setHTML(incorrectResponse.getHTML()
								+ "<br/>" + inputValue);
						add(incorrectResponse);
						keyPad.displaySelect();
						onIncorrect();
					}
				} catch (NumberFormatException e) {
					if ("".equals(display.getText())) {
						disappear();
					} else {
						incorrectResponse.setHTML(incorrectResponse.getHTML()
								+ "<br/>Not a number");
						add(incorrectResponse);
						keyPad.displaySelect();
					}
				}
			}
		});
	}
	
}
