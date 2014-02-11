/*   Copyright 2012 John Gralyan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
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
import com.sciencegadgets.client.CSS;

public class WrapDragController extends PickupDragController {

	LinkedList<DropController> dropControllers = null;
	private SimplePanel proxy;
	private int moveCounter = 0;

	public WrapDragController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);

		this.setBehaviorDragStartSensitivity(2);
		this.setBehaviorDragProxy(true);
	}

	@Override
	public void dragStart() {
		super.dragStart();

		proxy.getElement().getStyle().setOpacity(0);
		
		((Wrapper)context.draggable).moved = true;

		proxy.addStyleName(CSS.SELECTED_DROP_WRAPPER);
//		for (DropController dropC : dropControllers) {
//			Widget target = dropC.getDropTarget();
//			if (!(target instanceof AbsolutePanel)) {
//				target.addStyleName(CSS.SELECTED_DROP_WRAPPER);
//			}
//		}
	}

	@Override
	public void dragMove() {
		super.dragMove();
		
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
		
		proxy.removeStyleName(CSS.SELECTED_DROP_WRAPPER);
//		for (DropController dropC : dropControllers) {
//			dropC.getDropTarget().removeStyleName(CSS.SELECTED_DROP_WRAPPER);
//		}
		
		proxy.getElement().getStyle().clearOpacity();
		context.draggable.getElement().getStyle().clearOpacity();
	}

	@Override
	protected Widget newDragProxy(DragContext context) {
		Element selectedElement = context.draggable.getElement();
		Node dragEl = selectedElement.cloneNode(true);
		double pxPerEm = EquationHTML.getPxPerEm(selectedElement);

		proxy = new SimplePanel();
		proxy.addStyleName(DragClientBundle.INSTANCE.css().movablePanel());
		proxy.getElement().getStyle().setFontSize(pxPerEm, Unit.PX);
		proxy.getElement().appendChild(dragEl);

		return proxy;
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