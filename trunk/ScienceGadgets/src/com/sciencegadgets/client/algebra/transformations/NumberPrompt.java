package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.math.MathContext;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.KeyPadNumerical;
import com.sciencegadgets.client.SymbolDisplay;
import com.sciencegadgets.client.Prompt;

public abstract class NumberPrompt extends Prompt {

	private static final int same = 0;
	private static final int lessThan = -1;
	final KeyPadNumerical keyPad = new KeyPadNumerical();
	final SymbolDisplay display = keyPad.getSymbolDisplay();

	NumberPrompt(String question, final BigDecimal totalValue) {

		Label questionDisplay = new HTML(question);
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
					
					// 1% error acceptable
					BigDecimal error = inputValue.divide(totalValue, MathContext.DECIMAL32).subtract(new BigDecimal("1")).abs();
					if (error.compareTo(new BigDecimal(".01")) == lessThan) {
					
						// correct
						disappear();
						onCorrect();
					} else {
						// incorrect
						incorrectResponse.setHTML(incorrectResponse.getHTML()
								+ "<br/>" + inputValue);
						add(incorrectResponse);
						keyPad.displaySelect();
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

	abstract void onCorrect();
}
