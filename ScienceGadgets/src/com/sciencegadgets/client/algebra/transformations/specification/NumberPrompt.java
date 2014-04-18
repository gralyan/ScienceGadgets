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
import com.sciencegadgets.client.ui.SymbolDisplay;

public abstract class NumberPrompt extends Prompt {

	static final BigDecimal ACCEPTABLE_ERROR = new BigDecimal(".01");
	private static final int same = 0;
	private static final int lessThan = -1;
	final KeyPadNumerical keyPad = new KeyPadNumerical();
	final SymbolDisplay display = keyPad.getSymbolDisplay();
	protected int incorrectCount = 0;

	public NumberPrompt(String question, final BigDecimal totalValue) {

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
						int skillIncrease = 1 - incorrectCount;
						onCorrect(skillIncrease);
					} else {
						// incorrect
						incorrectCount++;
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
	
	public abstract void onCorrect(int skillIncrease);
}
