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
	boolean unitFilled = false;

	@Override
	protected void onLoad() {
		super.onLoad();
		if (!quantityFilled && !unitOnly) {
			quantityFilled = true;
			DataModerator.fill_Quantities(quantityBox);
		}

	}

	/**
	 * Regular vertical unit selection with mandatory QuantityKind selection to
	 * narrow units
	 */
	public UnitSelection() {
		this(false);
	}

	public UnitSelection(boolean quantityOnly) {
		super(false);
		this.quantityOnly = quantityOnly;

		this.add(quantityBox);

		if (!quantityOnly) {
			this.add(unitBox);

			quantityBox.addSelectionHandler(new SelectionHandler() {
				@Override
				public void onSelect(Cell selection) {
					unitBox.clear();
					String selected = quantityBox.getSelectedText();
					if (selected != null && !selected.equals("")) {
						DataModerator.fill_UnitsByQuantity(selected, unitBox);
					}
				}
			});
		}
	}

	public UnitSelection(String quantityKind) {
		super(false);

		this.add(unitBox);

		unitBox.clear();
		if (quantityKind != null && !quantityKind.equals("")) {
			DataModerator.fill_UnitsByQuantity(quantityKind, unitBox);
		}

	}
}
