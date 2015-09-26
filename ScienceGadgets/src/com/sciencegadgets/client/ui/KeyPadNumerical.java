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
package com.sciencegadgets.client.ui;

import java.util.HashSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;

public class KeyPadNumerical extends FlowPanel{

	HashSet<NumberButton> buttons = new HashSet<NumberButton>();
	SymbolDisplay symbolDisplay;

	public static final String NEG = "-";
	public static final String PERIOD = ".";
	public static final String E = "E";
	public static final String CLEAR = "clear";

	NumberButton negButton = new NumberButton(NEG);
	NumberButton periodButton = new NumberButton(PERIOD);
	NumberButton eButton = new NumberButton(E);
	NumberButton clearButton = new NumberButton(CLEAR);

	public KeyPadNumerical(SymbolDisplay symbolDisplay, Boolean includeNumbers,
			Boolean includeNegative, Boolean includeOtherSymbols) {
		super();
		initialize(symbolDisplay, includeNumbers, includeNegative,
				includeOtherSymbols);
	}

	public KeyPadNumerical(SymbolDisplay symbolDisplay) {
		this(symbolDisplay, true, true, true);
	}

	public KeyPadNumerical(Boolean includeNumbers, Boolean includeNegative,
			Boolean includeOtherSymbols) {
		super();
		initialize(new SymbolDisplay(this), includeNumbers, includeNegative,
				includeOtherSymbols);
	}

	public KeyPadNumerical() {
		this(true, true, true);
	}

	private void initialize(SymbolDisplay symbolDisplay,
			Boolean includeNumbers, Boolean includeNegative,
			Boolean includeOtherSymbols) {
		symbolDisplay.setKeyPad(this);
		addStyleName(CSS.KEY_PAD_NUMERICAL);

		this.symbolDisplay = symbolDisplay;
		this.symbolDisplay.addStyleName(CSS.NUMBER_DISPLAY);

		//CLEAR
		clearButton.setTitle("clear");
		buttons.add(clearButton);
		this.add(clearButton);
		

		//SPECIAL SYMBOLS
		if (includeNegative) {
			negButton.setTitle("negative");
			buttons.add(negButton);
			this.add(negButton);
		}
		if (includeOtherSymbols) {
			periodButton.setTitle("decimal dot");
			buttons.add(periodButton);
			this.add(periodButton);

			eButton.setTitle("x10^");
			buttons.add(eButton);
			this.add(eButton);
		}

		// separate numbers from special characters
		FlowPanel gap = new FlowPanel();
		gap.setSize("100%", "5px");
		this.add(gap);
		
		//NUMBERS
		if (includeNumbers) {
			for (int i = 0; i < 10; i++) {
				NumberButton b = new NumberButton(i + "");
				buttons.add(b);
				this.add(b);
			}
		}

		if (Moderator.isTouch) {
			// Clear Display on Touch - clear
			symbolDisplay.addTouchStartHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					displaySelect();
				}
			});
			// Input
			addNumberTouchHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					event.preventDefault();
					numberSelect(((Button) event.getSource()));
				}
			});
		} else {
			// Clear Display on Click - clear
			symbolDisplay.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					displaySelect();
				}
			});
			// Input
			addNumberClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					numberSelect(((Button) event.getSource()));
				}
			});
		}

		// reload to reset keys enabled
		symbolDisplay.setText(symbolDisplay.getText());
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
		symbolDisplay.setText("");
	}

	protected void numberSelect(Button button) {
		if(button == clearButton ) {
			symbolDisplay.setText("");
		}else {
			numberSelect(button.getText());
		}
	}

	protected void numberSelect(String number) {

		String oldText = symbolDisplay.getText();
		String newText = "";
		if (RandomSpecPanel.RANDOM_SYMBOL.equals(oldText)) {
			newText = number;
		} else {
			newText = oldText + number;
		}
		symbolDisplay.setText(newText);
	}

	public SymbolDisplay getSymbolDisplay() {
		return symbolDisplay;
	}

}

class NumberButton extends Button {
	public NumberButton(String string) {
		super(string);
		addStyleName(CSS.SMALLEST_BUTTON);
	}
}
