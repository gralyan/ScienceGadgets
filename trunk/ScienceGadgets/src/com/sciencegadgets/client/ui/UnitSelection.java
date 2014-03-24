package com.sciencegadgets.client.ui;

import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.shared.UnitAttribute;

public class UnitSelection extends CommunistPanel {

	final public SelectionPanel quantityBox = new SelectionPanel(
			"Quantity Kind");
	final public SelectionPanel unitBox = new SelectionPanel("Unit");
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
	public UnitSelection( boolean isHorizontal) {
		this(false, false, isHorizontal);
	}

	public UnitSelection(boolean quantityOnly, boolean unitOnly, boolean isHorizontal) {
		super(isHorizontal);
		this.quantityOnly = quantityOnly;
		this.unitOnly = unitOnly;
		
		if(quantityOnly && unitOnly){
			JSNICalls.error("Can't be quantityOnly and unitOnly");
		}

		unitBox.addStyleName(CSS.UNIT);
		unitBox.addStyleName(CSS.LAYOUT_ROW);
		quantityBox.addStyleName(CSS.LAYOUT_ROW);
		
		if (quantityOnly) {
			this.add(quantityBox);

		} else if (unitOnly) {
			this.add(unitBox);
		}else {
			this.add(quantityBox);
			this.add(unitBox);

			quantityBox.addSelectionHandler(new SelectionHandler() {
				@Override
				public void onSelect(Cell selection) {
					reloadUnitBox(quantityBox.getSelectedText(), null);
				}
			});
		}
	}

	public UnitSelection(String quantityKind) {
		super(false);

		this.add(quantityBox);
		this.add(unitBox);
		
		quantityFilled = true;

		// The Prefix quantity is special, should stand out
		quantityBox.add(UnitAttribute.PREFIX_QUANTITY_KIND, UnitAttribute.PREFIX_QUANTITY_KIND);
		quantityBox.getWidget(1).addStyleName(CSS.QUANTITY_KIND_PREFIX);
		quantityBox.add(quantityKind, quantityKind);

		quantityBox.addSelectionHandler(new SelectionHandler() {
			@Override
			public void onSelect(Cell selection) {
				reloadUnitBox(quantityBox.getSelectedText(), null);
			}
		});
	}

	public void reloadUnitBox(String quantityKind, String excludedUnitName) {
		unitBox.clear();
		if (quantityKind != null && !quantityKind.equals("")) {
			DataModerator.fill_UnitsByQuantity(quantityKind, unitBox, excludedUnitName);
		}

	}

	public boolean isQuantityOnly() {
		return quantityOnly;
	}
	public boolean isUnitsOnly() {
		return unitOnly;
	}
}
