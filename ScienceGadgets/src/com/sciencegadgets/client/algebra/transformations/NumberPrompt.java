package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.KeyPadNumerical;
import com.sciencegadgets.client.Prompt;

public abstract class NumberPrompt extends Prompt {

	private static final int same = 0;

	NumberPrompt(String question, final BigDecimal totalValue) {

		Label questionDisplay = new HTML(question);
		questionDisplay.addStyleName("layoutRow");
		add(questionDisplay);

		final Label display = new Label();
		display.addStyleName("layoutRow");

		final KeyPadNumerical keyPad = new KeyPadNumerical(display);

		add(display);
		add(keyPad);

		final HTML incorrectResponse = new HTML("<b>Incorrect</b>");

		addOkHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				try {
					BigDecimal inputValue = new BigDecimal(display.getText());
					if (inputValue.compareTo(totalValue) == same) {
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
