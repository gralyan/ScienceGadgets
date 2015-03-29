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
import com.sciencegadgets.client.algebra.ResponseNote;
import com.sciencegadgets.client.algebra.WrapDragController;
import com.sciencegadgets.client.algebra.transformations.InterFractionTransformations.DropType;

public class InterFractionDrop extends TransformationDropController {

	private DropType dropType;
	private InterFractionButton button;


	public InterFractionDrop(WrapDragController dragController, InterFractionButton button) {
		super(button.getTarget().getWrapper());

		this.button = button;
		this.dropType = button.getDropType();

		switch (dropType) {
		case CANCEL:
			response.setText(ResponseNote.Cancel.toString());
			break;
		case DIVIDE:
		case REMOVE_ONE:
			response.setText(ResponseNote.Divide.toString());
			break;
		case EXPONENTIAL:
		case LOG_COMBINE:
		case TRIG_COMBINE:
			response.setText(ResponseNote.Combine.toString());
			break;
		}
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		
		button.transform();
		
//		System.out.println("context "+context);
//		System.out.println("context.draggable "+context.draggable);
//		System.out.println("((AlgebaWrapper) context.draggable).getNode() "+((AlgebaWrapper) context.draggable).getNode());
//		drag = ((AlgebaWrapper) context.draggable).getNode();
//		target = ((AlgebaWrapper) getDropTarget()).getNode();
//
//		dropHTML = "<div style=\"display:inline-block; vertical-align:middle;\">"
//				+ "<div style=\"border-bottom:1px solid;\">"
//				+ target.getHTMLString(true, true)
//				+ "</div>"
//				+ "<div>"
//				+ drag.getHTMLString(true, true) + "</div>" + "</div>";
//
//		switch (dropType) {
//		case CANCEL:
//			cancelDrop();
//			break;
//		case REMOVE_ONE:
//			complete(true);
//			break;
//		case DIVIDE:
//			dividePrompt();
//			break;
//		case EXPONENTIAL:
//			exponentialDrop();
//			break;
//		case LOG_COMBINE:
//			logDrop();
//			break;
//		case TRIG_COMBINE:
//			trigDrop();
//			break;
//		}

	}
	
	public DropType getDropType() {
		return dropType;
	}
	

}
