package com.sciencegadgets.client.equationbrowser;

import com.google.gwt.user.client.ui.FlowPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SelectionButton;
import com.sciencegadgets.client.ui.specification.NumberSpecification;

public class ConversionSpecification extends FlowPanel {
	NumberSpecification conversionSpec = new NumberSpecification(true, true, false);
	SelectionButton convertButton = new SelectionButton("Convert") {
		@Override
		protected void onSelect() {
			Moderator.switchToConversion(conversionSpec.getSymbolDisplay().getText(), conversionSpec.getUnitMap().getUnitAttribute(), false);
		}
	};

	ConversionSpecification() {
		setSize("100%", "100%");
		conversionSpec.setHeight("80%");
		convertButton.setHeight("20%");
		convertButton.addStyleName(CSS.MAKE_EQ_BUTTON);

		add(conversionSpec);
		add(convertButton);
	}
}
