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
package com.sciencegadgets.client.conversion;

import java.util.LinkedList;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.WrapDragController;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.conversion.ConversionActivity.UnitDisplay;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Unit;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.dimensions.UnitMultiple;
import com.sciencegadgets.shared.dimensions.UnitName;

public class ConversionWrapper extends Wrapper {

	ConversionActivity conversionActivity = null;
	private UnitDisplay unitDisplay = null;
	private Unit unit = new Unit();
	private UnitName unitName = null;

	ConversionWrapper(UnitDisplay unitDisplay, AbsolutePanel panel,
			ConversionActivity conversionAvtivity) {
		super(unitDisplay.wrappedNode, panel, unitDisplay.wrappedNode.getHTML(
				false, false));
		this.conversionActivity = conversionAvtivity;
		this.unitDisplay = unitDisplay;

		this.addStyleName(CSS.CONVERSION_WRAPPER);

		UnitMultiple[] mult = node.getUnitAttribute().getUnitMultiples();
		if (mult.length > 0) {
			unitName = mult[0].getUnitName();
		}else {
			JSNICalls.error("ConversionWrapper node has incorrect attribute: "+node.getUnitAttribute());
		}
		
		DataModerator.findUnit(unit, unitName);
	}

	public UnitDisplay getUnitDisplay() {
		return unitDisplay;
	}

	public ConversionActivity getConversionActivity() {
		return conversionActivity;
	}
	
	/**
	 * WARNING - the unit may not be set yet because it uses an async RPC to set
	 */
	public Unit getUnit() {
		if(unit.getName() == null || "".equals(unit.getName())) {
			return null;
		}else {
			return unit;
		}
	}

	@Override
	public void select() {
		if (this.equals(ConversionActivity.selectedWrapper)) {
			return;
		}

		if (ConversionActivity.selectedWrapper != null) {
			ConversionActivity.selectedWrapper.unselect();
		}

		if(unitName != null) {
			conversionActivity.fillUnitSelection(unitName.toString());
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
		if (!TypeSGET.Fraction.equals(thisSide.getParentType())) {
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