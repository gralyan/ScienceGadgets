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

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.conversion.ConversionActivity.UnitDisplay;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitMultiple;
import com.sciencegadgets.shared.dimensions.UnitName;

public class UnitCancelDropController extends AbstractDropController {

	private ConversionActivity conversionActivity;
	private ConversionWrapper targetWrapper;
	private UnitName unitName;
	private int combinedExp;
	private String unitSymbol;

	public UnitCancelDropController(ConversionWrapper targetWrapper,
			UnitMultiple targetUnit, UnitMultiple dragUnit,
			UnitName unitname) {
		super(targetWrapper);
		conversionActivity = targetWrapper.getConversionActivity();
		this.targetWrapper = targetWrapper;

		this.unitName = unitname;
		this.unitSymbol = unitName.getSymbol();
		int dragExp = Integer.parseInt(dragUnit.getUnitExponent());
		int targetExp = Integer.parseInt(targetUnit.getUnitExponent());
		this.combinedExp = targetExp - dragExp;
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);

		EquationNode drag = ((Wrapper) context.draggable).getNode();
		EquationNode target = ((Wrapper) getDropTarget()).getNode();

		ConversionWrapper dragWraper = ((ConversionWrapper) context.draggable);
		UnitDisplay dragDisplay = dragWraper.getUnitDisplay();
		UnitDisplay targetDisplay = targetWrapper.getUnitDisplay();

		if (combinedExp == 0) {
			targetDisplay.isCanceled = true;
			dragDisplay.isCanceled = true;
			
		} else {
			EquationNode nodeToChange;
			UnitDisplay displayToChange;
			
			if (combinedExp > 0) {
				nodeToChange = target;
				dragDisplay.isCanceled = true;
				displayToChange = targetDisplay;
				
			} else {
				combinedExp = Math.abs(combinedExp);
				nodeToChange = drag;
				targetDisplay.isCanceled = true;
				displayToChange = dragDisplay;
			}
			
			EquationNode combinedNode = nodeToChange.replace(TypeSGET.Variable, unitSymbol);
			if (combinedExp > 1) {
				combinedNode = combinedNode.encase(TypeSGET.Exponential);
				combinedNode.append(TypeSGET.Number, combinedExp + "");
			}
			combinedNode.setAttribute(MathAttribute.Unit, unitName
					+ UnitAttribute.EXP_DELIMITER + combinedExp);
			
			displayToChange.wrappedNode = combinedNode;
		}

		conversionActivity.reloadEquation();
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		getDropTarget().addStyleName(CSS.SELECTED_DROP_WRAPPER);
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		getDropTarget().removeStyleName(CSS.SELECTED_DROP_WRAPPER);
	}
}
