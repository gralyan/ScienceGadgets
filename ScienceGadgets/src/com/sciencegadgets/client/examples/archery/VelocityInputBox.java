package com.sciencegadgets.client.examples.archery;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This is our input box. It is a composite because it has multiple widgets
 * working together. It is also "abstract" because we want onInput() to be open
 *
 */
public abstract class VelocityInputBox extends Composite {

	HTML varLabel = new HTML();
	TextBox inputBox = new TextBox();
	HTML unitLabel = new HTML();
	Button okButton = new Button("\u2197");

	/**
	 * Constructor
	 */
	VelocityInputBox(String varSymbol, String unitSymbol) {
		varLabel.setHTML(varSymbol+" = ");
		unitLabel.setHTML(unitSymbol);
		
		varLabel.setWidth("20");
		inputBox.setWidth("20%");
		unitLabel.setWidth("20");
		okButton.setWidth("20%");

		varLabel.setHeight("100%");
		inputBox.setHeight("100%");
		unitLabel.setHeight("100%");
		okButton.setHeight("100%");
		
		varLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		unitLabel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);

		// Listens for Enter, Tab, lose focus...
		inputBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onInput();
			}
		});

		// Listens for a button click
		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onInput();
			}
		});
		
		FlowPanel panel = new FlowPanel();
		panel.add(varLabel);
		panel.add(inputBox);
		panel.add(unitLabel);
		panel.add(okButton);

		// This must be called for Composite widgets
		initWidget(panel);

	}

	private void onInput() {
		String inputValue = inputBox.getValue();
		if(inputValue != null && !"".equals(inputValue)) {
			onInput(inputValue);
		}
	}
	/**
	 * This is "abstract" meaning it has no method body. The body can be made in
	 * a subclass or when making an object. This allows the method to be open so
	 * we can use this class in many ways.
	 */
	abstract void onInput(String inputValue);

	/**
	 * This method allows us to focus on the inputBox
	 */
	public void setFocus(boolean focused) {
		inputBox.setFocus(focused);
	}
}