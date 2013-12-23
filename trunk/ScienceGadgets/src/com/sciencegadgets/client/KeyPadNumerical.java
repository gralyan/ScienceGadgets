package com.sciencegadgets.client;

import java.util.HashSet;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

public class KeyPadNumerical extends FlowPanel {

HashSet<NumberButton> buttons = new HashSet<NumberButton>();
	
	public KeyPadNumerical() {
		for (int i = 0; i < 10; i++) {
			NumberButton b = new NumberButton(i + "");
			buttons.add(b);
			this.add(b);
		}
		this.add(new FlowPanel());
		
		NumberButton e = new NumberButton("E");
		e.setTitle("x10^");
		buttons.add(e);
		this.add(e);
		
		NumberButton exp = new NumberButton("^");
		exp.setTitle("exponent");
		buttons.add(exp);
		this.add(exp);
		
		NumberButton neg = new NumberButton("-");
		neg.setTitle("negative");
		buttons.add(neg);
		this.add(neg);
		
	}
	
	public void addNumberClickHandler(ClickHandler handler) {
		for(NumberButton b : buttons) {
			b.addClickHandler(handler);
		}
	}
	public void addNumberTouchHandler(TouchStartHandler handler) {
		for(NumberButton b : buttons) {
			b.addTouchStartHandler(handler);
		}
	}
	
	
}

class NumberButton extends Button {
	public NumberButton(String string) {
		super(string);
		addStyleName("smallestButton");
	}
}

