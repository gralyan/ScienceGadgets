package com.sciencegadgets.client.ui.specification;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.KeyPadNumerical;
import com.sciencegadgets.client.ui.UnitSelection;
import com.sciencegadgets.shared.dimensions.CommonConstants;

public class NumberSpecification extends QuantitySpecification {

	KeyPadNumerical numPad;
	RandomSpecPanel randSpec = new RandomSpecPanel();

	String randomness = "";
	CommonConstants constantSeleced = null;

	public NumberSpecification(boolean clearDisplays, boolean canHaveUnits, boolean allowRandomSpec) {
		super(clearDisplays, canHaveUnits);

		// Number Pad
		numPad = new KeyPadNumerical(symbolDisplay);
		symbolPalette.add(numPad);

		// Randomness Spec
		symbolPalette.add(randSpec);
		randSpec.setVisible(false);
		randSpec.addOkClickHandler((new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				randomness = randSpec.getRandomness();
				if (randomness != null) {
					symbolDisplay.setText(RandomSpecPanel.RANDOM_SYMBOL);
				} else {
					symbolDisplay.clear();
				}
			}
		}));

		// Symbol Toggle - switch Number Pad and Randomness Spec
		if(allowRandomSpec) {
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
		}else {
			symbolCaseToggle.removeFromParent();
			symbolDisplay.setWidth("100%");
		}

		// Unit Selection
		UnitSelection unitBox = new UnitSelection();
		unitPalette.add(unitBox);
		unitBox.addStyleName(CSS.FILL_PARENT);
		unitBox.unitBox.addSelectionHandler(new UnitSelectionHandler());
	}

}
