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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.KeyPadNumerical;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.SymbolDisplay;
import com.sciencegadgets.shared.dimensions.CommonConstants;

public class LogBaseSpecification extends Prompt {
	public final String[] bases = { "2", "10", CommonConstants.EULER.getSymbol() };
	private SymbolDisplay symbolDisplay;
	KeyPadNumerical keyPad = new KeyPadNumerical(true, false, false);

	public LogBaseSpecification() {
		super();
		symbolDisplay = keyPad.getSymbolDisplay();
		add(new Label("What base?"));
		add(symbolDisplay);
		
		ClickHandler baseClick = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSpecify(((Button) event.getSource()).getText());
			}
		};

		for (String base : bases) {
			Button baseButton = new Button(base, baseClick);
			baseButton.addStyleName(CSS.MEDIUM_BUTTON);
			add(baseButton);
		}
		add(keyPad);
		
		addOkHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!"".equals(symbolDisplay.getText())) {
					onSpecify(symbolDisplay.getText());
				} else {
					disappear();
				}
			}
		});
	}
	public void reload() {
		symbolDisplay.setText("");
		appear();
	}
	public void onSpecify(String base) {
		disappear();
	}
	
}