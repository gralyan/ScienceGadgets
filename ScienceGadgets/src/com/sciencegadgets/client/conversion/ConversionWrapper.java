package com.sciencegadgets.client.conversion;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.conversion.ConversionActivity.UnitDisplay;
import com.sciencegadgets.shared.UnitUtil;

public class ConversionWrapper extends Wrapper {

	ConversionActivity conversionActivity = null;
	private UnitDisplay unitDisplay;

	ConversionWrapper(UnitDisplay unitDisplay, AbsolutePanel panel, ConversionActivity conversionAvtivity) {
		super(unitDisplay.wrappedNode, panel, unitDisplay.wrappedNode.getHTML());
		this.conversionActivity = conversionAvtivity;
		this.unitDisplay = unitDisplay;
		
		this.addStyleName("conversionWrapper");
	}
	
	public UnitDisplay getUnitDisplay() {
		return unitDisplay;
	}
	
	public ConversionActivity getConversionActivity() {
		return conversionActivity;
	}

	@Override
	public void select() {
		if (this.equals(ConversionActivity.selectedWrapper)) {
			return;
		}

		String unitName = UnitUtil.getUnitNames(node)[0];
		conversionActivity.fillUnitSelection(unitName);

		if (ConversionActivity.selectedWrapper != null) {
			ConversionActivity.selectedWrapper.unselect();
		}
		ConversionActivity.selectedWrapper = this;
		super.select();
	}

	@Override
	public void unselect() {
		super.unselect();
		ConversionActivity.unitSelection.unitBox.clear();
		ConversionActivity.derivedUnitsSelection.clear();
		ConversionActivity.selectedWrapper = null;
	}

}