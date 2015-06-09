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
package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.util.DragClientBundle;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class WrapDragController extends PickupDragController {

	LinkedList<DropController> dropControllers = null;
	private SimplePanel proxy;
	private int moveCounter = 0;
	private boolean isDragging = false;

	public WrapDragController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);

		this.setBehaviorDragStartSensitivity(2);
		this.setBehaviorDragProxy(true);
	}
	
	public boolean isDragging() {
		return isDragging;
	}

	@Override
	public void dragStart() {
		
		isDragging = true;
		
		
		((Wrapper)context.draggable).moved = true;
		((Wrapper)context.draggable).select();

		super.dragStart();

		proxy.getElement().getStyle().setOpacity(0);
	}

	@Override
	public void dragMove() {
		super.dragMove();
		
		// Wait a bit to avoid flicker
		if(moveCounter == 2) {
			proxy.getElement().getStyle().setOpacity(1);
			context.draggable.getElement().getStyle().setOpacity(0);
		}
		moveCounter++;
	}
	
	@Override
	public void dragEnd() {
		super.dragEnd();

		moveCounter=0;
		
		proxy.getElement().getStyle().clearOpacity();
		context.draggable.getElement().getStyle().clearOpacity();
		
		isDragging = false;
	}

	@Override
	protected Widget newDragProxy(DragContext context) {
		Widget drag = context.draggable;
		Element selectedElement = drag.getElement();
		Node dragEl = selectedElement.cloneNode(true);
		double pxPerEm = EquationHTML.getPxPerEm(selectedElement);

		proxy = new SimplePanel();
		proxy.getElement().getStyle().setWidth(drag.getOffsetWidth(), Unit.PX);
		proxy.getElement().getStyle().setHeight(drag.getOffsetHeight(), Unit.PX);
		proxy.addStyleName(DragClientBundle.INSTANCE.css().movablePanel());
		proxy.getElement().getStyle().setFontSize(pxPerEm, Unit.PX);
		proxy.getElement().appendChild(dragEl);

		return proxy;
	}
	
	public DragContext getContext() {
		return context;
	}

//	@Override
//	public void registerDropController(DropController dropC) {
//		super.registerDropController(dropC);
//		if (dropControllers == null) {
//			dropControllers = new LinkedList<DropController>();
//		}
//		dropControllers.add(dropC);
//	}
//
//	@Override
//	public void unregisterDropControllers() {
//		super.unregisterDropControllers();
//		if (dropControllers != null) {
//			dropControllers.clear();
//		}
//	}
//
//	@Override
//	public void unregisterDropController(DropController dropC) {
//		super.unregisterDropController(dropC);
//		if (dropControllers != null) {
//			dropControllers.remove(dropC);
//		}
//	}

}