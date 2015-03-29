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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.ui.FitParentHTML;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.SymbolDisplay;
import com.sciencegadgets.client.ui.ToggleSlide;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitHTML;
import com.sciencegadgets.shared.dimensions.UnitMap;
import com.sciencegadgets.shared.dimensions.UnitName;

public abstract class QuantitySpecification extends Composite {

	interface VariableSpecUiBinder extends
			UiBinder<FlowPanel, QuantitySpecification> {
	}

	private static VariableSpecUiBinder uiBinder = GWT
			.create(VariableSpecUiBinder.class);

	@UiField
	SymbolDisplay symbolDisplay;
	@UiField
	ToggleSlide symbolCaseToggle;
	@UiField
	FlowPanel symbolPalette;

	@UiField
	FlowPanel unitArea;
	@UiField
	FlowPanel unitDisplay;
	@UiField
	ToggleSlide unitReciprocalToggle;
	@UiField
	FlowPanel unitPalette;


	private boolean isReciprocal = false;

	// Stores the unit name and associated exponent
	protected UnitMap unitMap = new UnitMap();
	protected UnitAttribute dataUnit = new UnitAttribute("");
	protected FitParentHTML unitHTML = new FitParentHTML();

	public QuantitySpecification(boolean clearDisplays,
			boolean canHaveUnits) {
		initWidget(uiBinder.createAndBindUi(this));


		unitDisplay.add(unitHTML);

		if (Moderator.isTouch) {
			// Unit Display Touch - clear
			unitDisplay.addDomHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					dataUnit.setString("");
					unitMap.clear();
					unitHTML.setHTML("");
				}
			}, TouchStartEvent.getType());

			// Symbol Display on Touch - clear
			symbolDisplay.addTouchStartHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					symbolDisplay.clear();
				}
			});
		} else {
			// Unit Display Click - clear
			unitDisplay.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					dataUnit.setString("");
					unitMap.clear();
					unitHTML.setHTML("");
				}
			}, ClickEvent.getType());

			// Symbol Display on Click - clear
			symbolDisplay.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					symbolDisplay.clear();
				}
			});
		}

		unitReciprocalToggle
				.setOptionsHtml(
						"<div><div style=\"border-bottom: thin solid;\">"+TypeSGET.NOT_SET+"</div><div>1</div></div>",
						"<div><div style=\"border-bottom: thin solid;\">1</div><div>"+TypeSGET.NOT_SET+"</div></div>",
						!isReciprocal);
		unitReciprocalToggle.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				isReciprocal = !isReciprocal;
			}
		});

		reload(clearDisplays, canHaveUnits);
	}

	public void reload(boolean clearDisplays, 
			boolean canHaveUnits) {

		dataUnit.setString("");
		unitMap.clear();
		unitHTML.setHTML("");
		symbolDisplay.clear();

		if (canHaveUnits) {
			unitArea.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		} else {
			unitArea.getElement().getStyle().setDisplay(Display.NONE);
		}

	}

	public void setUnit(UnitMap map) {
		unitMap = map;
		dataUnit = unitMap.getUnitAttribute();
		Element unitElement = UnitHTML.create(dataUnit, null, false);
		String unitHTMLString = JSNICalls.elementToString(unitElement);
		unitHTML.setHTML(unitHTMLString);
		unitHTML.resize();
	}

	class UnitSelectionHandler implements SelectionHandler {

		@Override
		public void onSelect(Cell selected) {

			UnitName name = new UnitName(selected.getValue());

			int direction = isReciprocal ? -1 : 1;
			unitMap.put(name, direction);

			setUnit(unitMap);

		}
	}

	public UnitMap getUnitMap() {
		return unitMap;
	}

	public UnitAttribute getDataUnit() {
		return dataUnit;
	}

	public SymbolDisplay getSymbolDisplay() {
		return symbolDisplay;
	}
}
