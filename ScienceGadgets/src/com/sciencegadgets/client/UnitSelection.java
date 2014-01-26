package com.sciencegadgets.client;

import com.sciencegadgets.client.SelectionPanel.Cell;
import com.sciencegadgets.client.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.entities.DataModerator;

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

		unitBox.addStyleName("layoutRow");
		quantityBox.addStyleName("layoutRow");
		
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
					reloadUnitBox(quantityBox.getSelectedText());
				}
			});
		}
	}

	public UnitSelection(String quantityKind) {
		super(false);

		this.add(unitBox);
		reloadUnitBox(quantityKind);

	}

	public void reloadUnitBox(String quantityKind) {
		unitBox.clear();
		if (quantityKind != null && !quantityKind.equals("")) {
			DataModerator.fill_UnitsByQuantity(quantityKind, unitBox);
		}

	}

	public boolean isQuantityOnly() {
		return quantityOnly;
	}
	public boolean isUnitsOnly() {
		return unitOnly;
	}
}
