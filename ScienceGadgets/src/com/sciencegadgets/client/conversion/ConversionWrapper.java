package com.sciencegadgets.client.conversion;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.WrapDragController;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.conversion.ConversionActivity.UnitDisplay;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.UnitMultiple;
import com.sciencegadgets.shared.UnitName;

public class ConversionWrapper extends Wrapper {

	ConversionActivity conversionActivity = null;
	private UnitDisplay unitDisplay;

	ConversionWrapper(UnitDisplay unitDisplay, AbsolutePanel panel,
			ConversionActivity conversionAvtivity) {
		super(unitDisplay.wrappedNode, panel, unitDisplay.wrappedNode.getHTML(false, false));
		this.conversionActivity = conversionAvtivity;
		this.unitDisplay = unitDisplay;

		this.addStyleName(CSS.CONVERSION_WRAPPER);
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

		if (ConversionActivity.selectedWrapper != null) {
			ConversionActivity.selectedWrapper.unselect();
		}

		UnitMultiple[] mult = node.getUnitAttribute().getUnitMultiples();
		if (mult.length > 0) {
			String unitName = mult[0].getUnitName().toString();
			conversionActivity.fillUnitSelection(unitName);
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

	public void addUnitCancelDropControllers() {
		EquationNode thisSide = node.getParent();
		if (!TypeEquationXML.Fraction.equals(thisSide.getParentType())) {
			return;
		}
		EquationNode otherSide = thisSide.getIndex() == 0 ? thisSide
				.getNextSibling() : thisSide.getPrevSibling();

		for (EquationNode targetNode : otherSide.getChildren()) {
			ConversionWrapper targetWrapper = (ConversionWrapper) targetNode
					.getWrapper();
			if (targetWrapper == null) {
				continue;
			}
			UnitMultiple nodeUnitMultiple = node.getUnitAttribute()
					.getUnitMultiples()[0];
			UnitMultiple targetUnitMultiple = targetNode.getUnitAttribute()
					.getUnitMultiples()[0];
			UnitName nodeUnitName = nodeUnitMultiple.getUnitName();
			UnitName targetUnitName = targetUnitMultiple.getUnitName();
			if (!nodeUnitName.equals(targetUnitName)) {
				continue;
			}
			UnitCancelDropController dropController = new UnitCancelDropController(
					targetWrapper, targetUnitMultiple, nodeUnitMultiple,
					nodeUnitName);
			WrapDragController dragC = addDragController();
			dragC.registerDropController(dropController);
		}
	}

}