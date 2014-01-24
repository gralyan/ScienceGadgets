package com.sciencegadgets.client;

import java.util.HashSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;

public class KeyPadNumerical extends FlowPanel {

	HashSet<NumberButton> buttons = new HashSet<NumberButton>();
	HashSet<NumberButton> oneAllowedButtons = new HashSet<NumberButton>();
	Label symbolDisplay;

	public KeyPadNumerical(final Label symbolDisplay) {
		addStyleName("keyPadNumerical");
		
		this.symbolDisplay = symbolDisplay;
		symbolDisplay.addStyleName("numberDisplay");

		for (int i = 0; i < 10; i++) {
			NumberButton b = new NumberButton(i + "");
			buttons.add(b);
			this.add(b);
		}
		//separate numbers from special characters with a div
		this.add(new FlowPanel());
		
		NumberButton neg = new NumberButton("-");
		neg.setTitle("negative");
		buttons.add(neg);
		oneAllowedButtons.add(neg);
		this.add(neg);
		
		NumberButton period = new NumberButton(".");
		period.setTitle("decimal dot");
		buttons.add(period);
		oneAllowedButtons.add(period);
		this.add(period);

		NumberButton e = new NumberButton("E");
		e.setTitle("x10^");
		buttons.add(e);
		oneAllowedButtons.add(e);
		this.add(e);

		NumberButton exp = new NumberButton("^");
		exp.setTitle("exponent");
		buttons.add(exp);
		oneAllowedButtons.add(exp);
		this.add(exp);

		if (Moderator.isTouch) {
			//Clear Display on Touch - clear
			symbolDisplay.addTouchStartHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					displaySelect();
				}
			});
			//Input
			addNumberTouchHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					event.preventDefault();
					numberSelect(((Button) event.getSource()));
				}
			});
		} else {
			//Clear Display on Click - clear
			symbolDisplay.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					displaySelect();
				}
			});
			//Input
			addNumberClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					numberSelect(((Button) event.getSource()));
				}
			});
		}

	}

	public void addNumberClickHandler(ClickHandler handler) {
		for (NumberButton b : buttons) {
			b.addClickHandler(handler);
		}
	}

	public void addNumberTouchHandler(TouchStartHandler handler) {
		for (NumberButton b : buttons) {
			b.addTouchStartHandler(handler);
		}
	}

	public void displaySelect() {
		for(Button b : oneAllowedButtons) {
			b.setEnabled(true);
		}
		symbolDisplay.setText("");
	}
	protected void numberSelect(Button button) {
		
		String oldText = symbolDisplay.getText();
		String newText = "";
		if (RandomSpecPanel.RANDOM_SYMBOL.equals(oldText)) {
			newText = button.getText();
		} else {
			newText = oldText + button.getText();
		}
		symbolDisplay.setText(newText);
		
		if(oneAllowedButtons.contains(button)) {
			button.setEnabled(false);
		}
	}
}

class NumberButton extends Button {
	public NumberButton(String string) {
		super(string);
		addStyleName("smallestButton");
	}
}
