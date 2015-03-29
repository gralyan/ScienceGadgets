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
package com.sciencegadgets.client.algebra.transformations;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.FitParentHTML;

public class TransformationDropController extends AbstractDropController {

	protected FitParentHTML response = new FitParentHTML();
	
	public TransformationDropController(Widget dropTarget) {
		super(dropTarget);
		
		response.addStyleName(CSS.DROP_ENTER_RESPONSE);
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		getDropTarget().addStyleName(CSS.SELECTED_DROP_WRAPPER);
		Moderator.getCurrentAlgebraActivity().lowerEqArea.clear();
		Moderator.getCurrentAlgebraActivity().lowerEqArea.add(response);
	}
	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		getDropTarget().removeStyleName(CSS.SELECTED_DROP_WRAPPER);
		Moderator.getCurrentAlgebraActivity().lowerEqArea.remove(response);
	}
}
