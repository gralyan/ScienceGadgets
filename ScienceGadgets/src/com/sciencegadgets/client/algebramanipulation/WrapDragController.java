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
package com.sciencegadgets.client.algebramanipulation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Wrapper;
import com.sciencegadgets.client.equationtree.EquationPanel;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class WrapDragController extends PickupDragController {

	private Map<DropController, MLElementWrapper> dropMap = new HashMap<DropController, MLElementWrapper>();
	private double startMouseX;
	private double nextWrapsX;
	private double prevWrapsX;
	private MathMLBindingNode nextNode;
	private MathMLBindingNode prevNode;
	private MLElementWrapper wrap;
	private Element svg;
	public WrapDragController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);

		this.setBehaviorDragStartSensitivity(5);
		this.setBehaviorDragProxy(true);
	}

	@Override
	public void dragMove() {
		super.dragMove();
//		
//		int mouseX = context.mouseX;
//
////		 Switch wrapper with the next if dragged far enough
//		if (mouseX > nextWrapsX && nextNode != null) {
//
//			//The "next" wrapper replaces the dragging wrapper
//			setTranslate(nextNode.getSVG(), startTranslate, 0);
//			
//			//The wrapper is placed to the right of the "next" Wrapper
//			double wrapPlacement = (wrap.getNextSiblingWrapper().getOffsetWidth()+wrap.paddingLeft)
//					* coordinateConverter.getXsvgPerGlobal();
//			startTranslate += wrapPlacement;
//			setTranslate(svg, startTranslate, 0);
//
//			com.google.gwt.user.client.Element selectNode = wrap.getElement();
//			System.out.println("selectNode: "+selectNode.getString());
//			com.google.gwt.user.client.Element nextEl = nextNode.getWrapper().getElement();
//			System.out.println("nextEl: "+nextEl.getString());
//			com.google.gwt.user.client.Element parentEl = wrap.getParentWrapper().getElement();
//					System.out.println("parentEl: "+parentEl.getString());
//					
//			
//			parentEl.insertAfter(selectNode, nextEl);
//			
//			// switch nodes
//			MathMLBindingNode wrapNode = wrap.getNode();
//			wrapNode.getParent().getMLNode()
//					.insertBefore(nextNode.getMLNode(), wrapNode.getMLNode());
//
//			initializeFeilds();
//			return;
//		}
//		// Switch wrapper with the previous if dragged far enough
//		if (wrapTrans < prevWrapsTrans && prevNode != null) {
//			
//			startTranslate = prevWrapsTrans;
//			setTranslate(svg, prevWrapsTrans, 0);
//			double wrapWidth = wrap.getOffsetWidth()
//					* coordinateConverter.getXsvgPerGlobal();
//			setTranslate(prevNode.getSVG(), prevWrapsTrans + wrapWidth, 0);
//
//			// switch nodes
//			MathMLBindingNode wrapNode = wrap.getNode();
//			wrapNode.getParent().getMLNode()
//					.insertAfter(prevNode.getMLNode(), wrapNode.getMLNode());
//
//			initializeFeilds();
//			return;
//		}
//
//		// Incrementally move svg with mouse
//		setTranslate(svg, wrapTrans, wrapTransY);
	}

	@Override
	public void dragStart() {
		super.dragStart();

//		wrap = (MLElementWrapper) context.draggable;
//
		// Save initial state as fields for later use
//		 startMouseX = context.mouseX;
//		 
//		initializeFeilds();
	}

	@Override
	public void dragEnd() {
		super.dragEnd();

		// Revert back to initial state
//		wrap.unselect();
//		svg.setAttribute("transform", startTransform);

	}

	/**
	 * @return - The drop controllers for this drag controller
	 */
	Set<DropController> getDropControllers() {
		return dropMap.keySet();
	}

	/**
	 * @return - The drop targets for this drag controller
	 */
	Collection<MLElementWrapper> getDropTargets() {
		return dropMap.values();
	}

	@Override
	public void registerDropController(DropController dropController) {
		super.registerDropController(dropController);
		if (dropController.getDropTarget() instanceof MLElementWrapper) {
			dropMap.put(dropController,
					(MLElementWrapper) dropController.getDropTarget());
		} else if (!(dropController.getDropTarget() instanceof AbsolutePanel)) {
			throw new ClassCastException(
					"The Drop controller must have an MLElementWrapper as a target");
		}
	}

	@Override
	public void unregisterDropController(DropController dropController) {
		super.unregisterDropController(dropController);
		dropMap.remove(dropController);
	}

	@Override
	public void unregisterDropControllers() {
		super.unregisterDropControllers();
		dropMap.clear();
	}

//	private void initializeFeilds() {
//
//		// Save immediate siblings for switching
//		try {
//			Wrapper nextWrap = wrap.getNextSiblingWrapper();
//			nextNode = nextWrap.getNode();
//			nextWrapsX = nextWrap.getAbsoluteLeft();
//		} catch (IndexOutOfBoundsException e) {
//		}
//		try {
//			Wrapper prevWrap = wrap.getPrevSiblingWrapper();
//			prevNode = prevWrap.getNode();
//			prevWrapsX = prevWrap.getAbsoluteLeft()+ prevWrap.getOffsetWidth();
//		} catch (IndexOutOfBoundsException e) {
//		}
//	}
}