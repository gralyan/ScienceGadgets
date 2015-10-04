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
package com.sciencegadgets.client.ui.specification;

import java.util.HashSet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.KeyPadNumerical;
import com.sciencegadgets.client.ui.UnitSelection;

public class VariableSpecification extends QuantitySpecification {

	private HashSet<SymbolButton> latinButtons = new HashSet<SymbolButton>();
	private HashSet<SymbolButton> greekButtons = new HashSet<SymbolButton>();

	SymbolClickHandler symbolClick = new SymbolClickHandler();
	SymbolTouchHandler symbolTouch = new SymbolTouchHandler();

	public VariableSpecification(boolean clearDisplays, boolean canHaveUnits) {
		super(clearDisplays, canHaveUnits);

		// Symbol Selection
		symbolPalette
				.add(new KeyPadNumerical(symbolDisplay, false, true, false));
		symbolDisplay.addStyleName(CSS.MATH_FONT);

		symbolPalette.add(new Label("Latin"));
		for (char i = 0; i < 26; i++) {
			SymbolButton button = new SymbolButton((char) ('a' + i) + "");
			latinButtons.add(button);
			symbolPalette.add(button);
		}
		symbolPalette.add(new Label("Greek"));
		for (char i = 0; i < 25; i++) {
			if (i != 17) {
				SymbolButton button = new SymbolButton((char) ('α' + i) + "");
				greekButtons.add(button);
				symbolPalette.add(button);
			}
		}

		symbolCaseToggle.setOptions("a", "A", true);
		symbolCaseToggle.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean isLower = symbolCaseToggle.isFistSelected();
				int latinDiff = isLower ? 'A' - 'a' : 'a' - 'A';
				for (SymbolButton button : latinButtons) {

					int changed = button.getText().toCharArray()[0] + latinDiff;
					button.setText("" + (char) changed);
				}
				int greekDiff = isLower ? 'Α' - 'α' : 'α' - 'Α';
				for (SymbolButton button : greekButtons) {

					int changed = button.getText().toCharArray()[0] + greekDiff;
					button.setText("" + (char) changed);
				}
			}
		});

		// QuantityKind Selection
		UnitSelection quantityBox = new UnitSelection(true, false);
		unitPalette.add(quantityBox);
		quantityBox.addStyleName(CSS.FILL_PARENT);
		quantityBox.quantityBox.addSelectionHandler(new UnitSelectionHandler());

	}

	class SymbolButton extends Button {
		public SymbolButton(String string) {
			super(string);
			if (Moderator.isTouch) {
				addTouchStartHandler(symbolTouch);
			} else {
				addClickHandler(symbolClick);
			}
			addStyleName(CSS.VAR_SPEC_INS+" "+ CSS.SMALLEST_BUTTON);
		}
	}

	private class SymbolClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			String oldText = symbolDisplay.getText();
			symbolDisplay.setText(oldText
					+ ((Button) event.getSource()).getText());
		}
	}

	private class SymbolTouchHandler implements TouchStartHandler {
		@Override
		public void onTouchStart(TouchStartEvent event) {
			String oldText = symbolDisplay.getText();
			symbolDisplay.setText(oldText
					+ ((Button) event.getSource()).getText());
		}
	}

}
