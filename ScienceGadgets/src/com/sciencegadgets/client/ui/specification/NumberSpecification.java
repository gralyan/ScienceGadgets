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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.KeyPadNumerical;
import com.sciencegadgets.client.ui.UnitSelection;
import com.sciencegadgets.shared.dimensions.CommonConstants;
import com.sciencegadgets.shared.dimensions.UnitAttribute;

public class NumberSpecification extends QuantitySpecification {

	KeyPadNumerical numPad;
	RandomSpecPanel randSpec;

	String randomness = "";
	CommonConstants constantSeleced = null;
	private UnitSelection unitSelection;

	public NumberSpecification(boolean clearDisplays, boolean canHaveUnits,
			boolean allowRandomSpec) {
		super(clearDisplays, canHaveUnits);

		// Number Pad
		numPad = new KeyPadNumerical(symbolDisplay);
		symbolPalette.add(numPad);

		// Randomness Spec
		randSpec = new RandomSpecPanel() {

			@Override
			protected void onSetRandom() {
				randomness = randSpec.getRandomness();
				if (randomness != null) {
					symbolDisplay.setText(RandomSpecPanel.RANDOM_SYMBOL);
				} else {
					symbolDisplay.clear();
				}
			}
		};
		symbolPalette.add(randSpec);
		randSpec.setVisible(false);

		// Symbol Toggle - switch Number Pad and Randomness Spec
		if (allowRandomSpec) {
			symbolCaseToggle.setOptions("#", "?", true);
			symbolCaseToggle.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (symbolCaseToggle.isFistSelected()) {
						randSpec.setVisible(true);
						numPad.setVisible(false);

					} else {
						randSpec.setVisible(false);
						numPad.setVisible(true);
					}
				}
			});
		} else {
			symbolCaseToggle.removeFromParent();
			symbolDisplay.setWidth("100%");
		}

		// Unit Selection
		unitSelection = new UnitSelection();
		unitPalette.add(unitSelection);
		unitSelection.addStyleName(CSS.FILL_PARENT);
		unitSelection.unitBox.addSelectionHandler(new UnitSelectionHandler());
	}

	public String getRandomness() {
		return randomness;
	}
	
	public void limitUnits(UnitAttribute unit) {
		unitSelection.limitUnits(unit);
	}
}
