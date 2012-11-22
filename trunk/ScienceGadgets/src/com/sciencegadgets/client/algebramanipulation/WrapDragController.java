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

public class WrapDragController extends PickupDragController {

	private Map<DropController, MLElementWrapper> dropMap;
	private String startTransform;
	private int startTranslate;
	private int startMouseX;

	public WrapDragController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);

		dropMap = new HashMap<DropController, MLElementWrapper>();
		this.setBehaviorDragStartSensitivity(5);
	}

	@Override
	public void dragMove() {
		MLElementWrapper wrap = (MLElementWrapper) context.draggable;
		Element svg = wrap.getSVG();

		String oldTrans = svg.getAttribute("transform");
		int start = oldTrans.indexOf("translate") + 10;
		int end = oldTrans.indexOf(")", start);
		String oldTransValue = oldTrans.substring(start, end);
		
		//TODO scale it accordingly
		int newTrans = startTranslate + 4*(context.mouseX - startMouseX);

		String newAttr = oldTrans.replaceFirst(oldTransValue,
				newTrans+"");

		svg.setAttribute("transform", newAttr);
		
		super.dragMove();
	}

	@Override
	public void dragStart() {
		super.dragStart();
		MLElementWrapper wrap = (MLElementWrapper) context.draggable;
		Element svg = wrap.getSVG();
		
		startTransform = svg.getAttribute("transform");
		startMouseX = context.mouseX;

		String transform = svg.getAttribute("transform");
		int start = transform.indexOf("translate") + 10;
		int end = transform.indexOf(")", start);
		String oldTransValue = transform.substring(start, end);
		startTranslate = Integer.parseInt(oldTransValue);
	}

	@Override
	public void dragEnd() {
		super.dragEnd();
		MLElementWrapper wrap = (MLElementWrapper) context.draggable;
		Element svg = wrap.getSVG();

		wrap.unselect();

		svg.setAttribute("transform", startTransform);
	}

	Set<DropController> getDropControllers() {
		return dropMap.keySet();
	}
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
}