package com.tumojava.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This is our input box. It is a composite because it has multiple widgets
 * working together. It is also "abstract" because we want onInput() to be open
 *
 */
public abstract class AnswerInputBox extends Composite {

	TextBox inputBox = new TextBox();
	Button okButton = new Button("OK");

	/**
	 * Constructor
	 */
	AnswerInputBox() {

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
		panel.add(inputBox);
		panel.add(okButton);

		// This must be called for Composite widgets
		initWidget(panel);

	}

	@Override
	protected void onLoad() {
		super.onLoad();

		// In order to center the inBox, we must set its size to its contents
		int width = inputBox.getOffsetWidth() + okButton.getOffsetWidth();
		setWidth(width + "px");
	}

	/**
	 * This is "abstract" meaning it has no method body. The body can be made in
	 * a subclass or when making an object. This allows the method to be open so
	 * we can use this class in many ways.
	 */
	abstract void onInput();

	/**
	 * This method allows us to focus on the inputBox
	 */
	public void setFocus(boolean focused) {
		inputBox.setFocus(focused);
	}
}