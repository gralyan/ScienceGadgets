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

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SearchBox;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitName;

public class UnitSelection extends FlowPanel {

	final public SelectionPanel quantityBox = new SelectionPanel(
			"Quantity Kind");
	final public UnitSelectionPanel unitBox = new UnitSelectionPanel();
	SearchBox searchBox;
	boolean quantityOnly = false;
	boolean quantityFilled = false;
	boolean unitOnly = false;

	@Override
	protected void onLoad() {
		super.onLoad();
		if (!quantityFilled && !unitOnly) {
			quantityFilled = true;
			DataModerator.fill_Quantities(this);
		}

	}

	/**
	 * Regular vertical unit selection with mandatory QuantityKind selection to
	 * narrow units
	 */
	public UnitSelection() {
		this(false, false);
	}

	public UnitSelection(boolean quantityOnly, boolean unitOnly) {
		super();
		this.quantityOnly = quantityOnly;
		this.unitOnly = unitOnly;

		if (quantityOnly && unitOnly) {
			JSNICalls.error("Can't be quantityOnly and unitOnly");
		}

		// unitBox.addStyleName(CSS.UNIT);
		unitBox.addStyleName(CSS.LAYOUT_ROW);
		quantityBox.addStyleName(CSS.LAYOUT_ROW);

		if (quantityOnly) {
			quantityBox.setSize("100%", "100%");
			this.add(quantityBox);
		} else if (unitOnly) {
			unitBox.setSize("100%", "100%");
			this.add(unitBox);
		} else {

			searchBox = unitBox.makeSearchBox();
			searchBox.setBrowser(quantityBox);
			searchBox.addStyleName(CSS.SEARCH_BOX);
			this.add(searchBox);

			quantityBox.addStyleName(CSS.UNIT_SELECTION);
			unitBox.addStyleName(CSS.UNIT_SELECTION);
			this.add(quantityBox);
			this.add(unitBox);

			quantityBox.addSelectionHandler(new SelectionHandler() {
				@Override
				public void onSelect(Cell selection) {
					reloadUnitBox(quantityBox.getSelectedValue(), null, false);
				}
			});
		}
	}

	public void limitUnits(UnitAttribute unit) {

		// TODO only works for variables with a single unit
		if (unit != null && unit.getUnitMultiples().length == 1) {
			searchBox.getElement().getStyle().setDisplay(Display.NONE);
			quantityBox.getElement().getStyle().setDisplay(Display.NONE);
			unitBox.getElement().getStyle().setWidth(100, Unit.PCT);
			unitBox.getElement().getStyle().setHeight(100, Unit.PCT);
			unitBox.getElement().getStyle().setMarginTop(0, Unit.PX);
			String qKind = unit.getUnitMultiples()[0].getUnitName()
					.getQuantityKind();
			reloadUnitBox(qKind, null, false);
		} else {
			searchBox.getElement().getStyle().clearDisplay();
			quantityBox.getElement().getStyle().clearDisplay();
			unitBox.getElement().getStyle().clearWidth();
			unitBox.getElement().getStyle().clearHeight();
			unitBox.getElement().getStyle().clearMarginTop();
		}

	}

	// public UnitSelection(String quantityKind) {
	// super(false);
	//
	// this.add(quantityBox);
	// this.add(unitBox);
	//
	// quantityFilled = true;
	//
	// // The Prefix quantity is special, should stand out
	// quantityBox.add(UnitAttribute.PREFIX_QUANTITY_KIND,
	// UnitAttribute.PREFIX_QUANTITY_KIND);
	// quantityBox.getWidget(1).addStyleName(CSS.QUANTITY_KIND_PREFIX);
	// quantityBox.add(quantityKind, quantityKind);
	//
	// quantityBox.addSelectionHandler(new SelectionHandler() {
	// @Override
	// public void onSelect(Cell selection) {
	// reloadUnitBox(quantityBox.getSelectedText(), null, false);
	// }
	// });
	// }

	public void reloadUnitBox(String searchSpec, String excludedUnitName,
			boolean isSearch) {
		unitBox.clear();
		if (searchSpec == null || searchSpec.equals("")) {
			return;
		}
		if (isSearch) {
			DataModerator.fill_UnitsBySearch(searchSpec, unitBox,
					excludedUnitName);
		} else {
			DataModerator.fill_UnitsByQuantity(searchSpec, unitBox,
					excludedUnitName);
		}
	}

	public boolean isQuantityOnly() {
		return quantityOnly;
	}

	public boolean isUnitsOnly() {
		return unitOnly;
	}

	public class UnitSelectionPanel extends SelectionPanel {

		public UnitSelectionPanel() {
			super("Unit");
		}

		@Override
		public void search(String searchQ) {
			if ("".equals(searchQ)) {
				unitBox.clear();
			} else {
				reloadUnitBox(searchQ, null, true);
			}
		}
	}
}
