package com.sciencegadgets.client.conversion;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.conversion.ConversionActivity.UnitDisplay;
import com.sciencegadgets.shared.UnitUtil;

class ConversionWrapper extends Wrapper {

	ConversionActivity conversionAvtivity = null;
	private UnitDisplay unitDisplay;

	ConversionWrapper(UnitDisplay unitDisplay, AbsolutePanel panel, ConversionActivity conversionAvtivity) {
		super(unitDisplay.wrappedNode, panel, unitDisplay.wrappedNode.getHTML());
		this.conversionAvtivity = conversionAvtivity;
		this.unitDisplay = unitDisplay;
	}
	
	public UnitDisplay getUnitDisplay() {
		return unitDisplay;
	}

	@Override
	public void select() {
		if (this.equals(ConversionActivity.selectedWrapper)) {
			return;
		}

		String unitName = UnitUtil.getUnitNames(node)[0];
		conversionAvtivity.fillUnitSelection(unitName);

		if (ConversionActivity.selectedWrapper != null) {
			ConversionActivity.selectedWrapper.unselect();
		}
		ConversionActivity.selectedWrapper = this;
		super.select();
	}

	@Override
	public void unselect() {
		super.unselect();
		conversionAvtivity.unitSelection.unitBox.clear();
		ConversionActivity.selectedWrapper = null;
	}

}