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
