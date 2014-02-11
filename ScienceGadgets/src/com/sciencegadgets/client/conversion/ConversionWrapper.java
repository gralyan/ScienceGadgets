package com.sciencegadgets.client.conversion;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.WrapDragController;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.conversion.ConversionActivity.UnitDisplay;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.UnitUtil;

public class ConversionWrapper extends Wrapper {

	ConversionActivity conversionActivity = null;
	private UnitDisplay unitDisplay;

	ConversionWrapper(UnitDisplay unitDisplay, AbsolutePanel panel,
			ConversionActivity conversionAvtivity) {
		super(unitDisplay.wrappedNode, panel, unitDisplay.wrappedNode.getHTML());
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
		
		String unitName = UnitUtil.getUnitNames(node)[0];
		conversionActivity.fillUnitSelection(unitName);
		
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
		MathNode thisSide = node.getParent();
		if (TypeML.Fraction.equals(thisSide.getParentType())) {
			MathNode otherSide = thisSide.getIndex() == 0 ? thisSide
					.getNextSibling() : thisSide.getPrevSibling();

			for (MathNode targetNode : otherSide.getChildren()) {
				String nodeUnitAttribute = node.getAttribute(MathAttribute.Unit);
				String targetUnitAttribute = targetNode.getAttribute(MathAttribute.Unit);
				String nodeUnitName = UnitUtil.getUnitNames(nodeUnitAttribute)[0];
				String targetUnitName = UnitUtil.getUnitNames(targetUnitAttribute)[0];
				if(nodeUnitName.equals(targetUnitName)) {
					ConversionWrapper targetWrapper = (ConversionWrapper) targetNode.getWrapper();
					UnitCancelDropController dropController = new UnitCancelDropController(targetWrapper, targetUnitAttribute,nodeUnitAttribute, nodeUnitName );
					WrapDragController dragC = addDragController();
					dragC.registerDropController(dropController);
				}
			}
		}
	}

}