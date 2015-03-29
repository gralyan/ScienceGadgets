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

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.conversion.ConversionActivity.UnitDisplay;
import com.sciencegadgets.client.ui.CSS;

public class ReorderDropController extends AbstractDropController {

	private ConversionActivity conversionActivity;
	private ConversionWrapper targetWrapper;

	public ReorderDropController(ConversionWrapper targetWrapper) {
		super(targetWrapper);
		conversionActivity = targetWrapper.getConversionActivity();
		this.targetWrapper = targetWrapper;
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);

		EquationNode drag = ((Wrapper) context.draggable).getNode();
		EquationNode target = ((Wrapper) getDropTarget()).getNode();
		EquationNode parent = drag.getParent();

		LinkedList<UnitDisplay> displayList = conversionActivity.unitDisplays;
		ConversionWrapper dragWraper = ((ConversionWrapper) context.draggable);
		UnitDisplay dragDisplay = dragWraper.getUnitDisplay();
		UnitDisplay targetDisplay = targetWrapper.getUnitDisplay();

		if (drag.getIndex() < target.getIndex()) {// add after drop
			parent.addAfter(target.getIndex(), drag);
			displayList.remove(targetDisplay);
			displayList.add(displayList.indexOf(dragDisplay), targetDisplay);

		} else {// add before drop
			parent.addBefore(target.getIndex(), drag);
			displayList.remove(dragDisplay);
			displayList.add(displayList.indexOf(targetDisplay), dragDisplay);
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
