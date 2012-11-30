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
import com.sciencegadgets.client.Wrapper;
import com.sciencegadgets.client.equationtree.CoordinateConverter;
import com.sciencegadgets.client.equationtree.EquationPanel;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class WrapDragController extends PickupDragController {

	private Map<DropController, MLElementWrapper> dropMap = new HashMap<DropController, MLElementWrapper>();
	private String startTransform;
	private double startTranslate;
	private double startMouseX;
	private double nextWrapsTrans;
	private double prevWrapsTrans;
	private MathMLBindingNode nextNode;
	private MathMLBindingNode prevNode;
	private MLElementWrapper wrap;
	private Element svg;
	private CoordinateConverter coordinateConverter;

	public WrapDragController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);

		this.setBehaviorDragStartSensitivity(5);
	}

	@Override
	public void dragMove() {
		super.dragMove();

		double wrapTrans = coordinateConverter.XtoSVG(wrap.getAbsoluteLeft());

		// Switch wrapper with the next if dragged far enough
		if (wrapTrans > nextWrapsTrans && nextNode != null) {

			//The "next" wrapper replaces the dragging wrapper
			setTranslate(nextNode.getSVG(), startTranslate, 0);
			
			//The wrapper is placed to the right of the "next" Wrapper
			double wrapPlacement = (wrap.getNextSiblingWrapper().getOffsetWidth()+wrap.paddingLeft)
					* coordinateConverter.getXsvgPerGlobal();
			startTranslate += wrapPlacement;
			setTranslate(svg, startTranslate, 0);

			// switch nodes
			MathMLBindingNode wrapNode = wrap.getNode();
			wrapNode.getParent().getMLNode()
					.insertBefore(nextNode.getMLNode(), wrapNode.getMLNode());

			initializeFeilds();
			return;
		}
		// Switch wrapper with the previous if dragged far enough
		if (wrapTrans < prevWrapsTrans && prevNode != null) {
			
			startTranslate = prevWrapsTrans;
			setTranslate(svg, prevWrapsTrans, 0);
			double wrapWidth = wrap.getOffsetWidth()
					* coordinateConverter.getXsvgPerGlobal();
			setTranslate(prevNode.getSVG(), prevWrapsTrans + wrapWidth, 0);

			// switch nodes
			MathMLBindingNode wrapNode = wrap.getNode();
			wrapNode.getParent().getMLNode()
					.insertAfter(prevNode.getMLNode(), wrapNode.getMLNode());

			initializeFeilds();
			return;
		}

		// Incrementally move svg with mouse
		setTranslate(svg, wrapTrans, 0);
	}

	@Override
	public void dragStart() {
		super.dragStart();

		wrap = (MLElementWrapper) context.draggable;
		coordinateConverter = wrap.getEqPanel().getCoordinateConverter();
		svg = wrap.getSVG();

		// Save initial state as fields for later use
		// startMouseX = context.mouseX;
		startTranslate = getTranslate(svg, true);

		initializeFeilds();
	}

	@Override
	public void dragEnd() {
		super.dragEnd();

		// Revert back to initial state
		wrap.unselect();
		svg.setAttribute("transform", startTransform);

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

	private void initializeFeilds() {

		startMouseX = coordinateConverter.XtoGlobal(startTranslate);

		// Save initial transform attribute
		startTransform = svg.getAttribute("transform");

		// Save immediate siblings for switching
		try {
			Wrapper nextWrap = wrap.getNextSiblingWrapper();
			nextNode = nextWrap.getNode();
			nextWrapsTrans = getTranslate(nextNode.getSVG(), true);
		} catch (IndexOutOfBoundsException e) {
		}
		try {
			Wrapper prevWrap = wrap.getPrevSiblingWrapper();
			prevNode = prevWrap.getNode();
			prevWrapsTrans = getTranslate(prevNode.getSVG(), true);
		} catch (IndexOutOfBoundsException e) {
		}
	}

	private static native double getTranslate(Element element, boolean isX)/*-{
		var xforms = element.transform.baseVal; // An SVGTransformList
		for ( var i = 0; i < xforms.numberOfItems; i++) {
			var curXForm = xforms.getItem(0); // An SVGTransform
			if (curXForm.type == SVGTransform.SVG_TRANSFORM_TRANSLATE) {
				if (isX) {
					return curXForm.matrix.e;
				} else {
					return curXForm.matrix.f
				}
			}
		}
	}-*/;

	private static native void setTranslate(Element element, double transX,
			double transY)/*-{
		var xforms = element.transform.baseVal; // An SVGTransformList
		for ( var i = 0; i < xforms.numberOfItems; i++) {
			var curXForm = xforms.getItem(0); // An SVGTransform
			if (curXForm.type == SVGTransform.SVG_TRANSFORM_TRANSLATE) {
				curXForm.setTranslate(transX, transY);
			}
		}
	}-*/;
	//
	// /**
	// * Sets the translate portion of the transform attribute to the given
	// value <br/>
	// * If there was no translate attribute, it is made
	// *
	// * @param element
	// * - the element with the attribute to replace
	// * @param newTranslate
	// * - the new translate value
	// * @return the old translate value
	// */
	// private double setTranslate(Element element, double newTranslate) {
	//
	// // Get the old transform attribute
	// String oldAttribute = element.getAttribute("transform");
	// String oldTranslate = getTranslateString(element);
	//
	// String newAttribute;
	// if ("".equals(oldTranslate)) {
	// newAttribute = oldAttribute + " translate(" + newTranslate + ")";
	// } else {
	// // Replace transform attribute
	// newAttribute = oldAttribute.replaceFirst(oldTranslate, newTranslate
	// + "");
	// }
	// element.setAttribute("transform", newAttribute);
	//
	// return Double.parseDouble(oldTranslate);
	// }
	//
	// /**
	// * @return The <b>double value</b> of the translate portion of the
	// transform
	// * attribute in the given element
	// */
	// private double getTranslateValue(Element element) {
	// String translate = getTranslateString(element);
	// if ("".equals(translate)) {
	// return 0;
	// }
	// return Double.parseDouble(translate);
	// }
	//
	// /**
	// * @return The <b>exact string</b> of the translate portion of the
	// transform
	// * attribute in the given element
	// */
	// private String getTranslateString(Element element) {
	// String transform = element.getAttribute("transform");
	//
	// if ("".equalsIgnoreCase(transform)) {
	// return "";
	// }
	//
	// int start = transform.indexOf("translate") + 10;
	// int end = transform.indexOf(")", start);
	// return transform.substring(start, end);
	// }

}