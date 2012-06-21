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

import java.util.LinkedList;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Log;

public class AlgebraManipulator extends AbsolutePanel {
	HTML draggableEquation;
	AbsolutePanel parentPanel;

	public AlgebraManipulator(HTML draggableEquation,
			LinkedList<MLElementWrapper> wrappers, AbsolutePanel parentPanel) {

		this.draggableEquation = draggableEquation;
		this.parentPanel = parentPanel;

		/*
		 Element draggableEquationElement = (Element) draggableEquation
				.getElement().getElementsByTagName("math").getItem(0);
		if (draggableEquationElement == null) {
			// The main mathML element is fmath in chrome, math in firefox
			draggableEquationElement = (Element) draggableEquation.getElement()
					.getElementsByTagName("fmath").getItem(0);
		}
		DOM.setElementAttribute(draggableEquationElement, "mathsize", "300%");
		*/
		parentPanel.clear();
		parentPanel.add(draggableEquation);
		makeWrappers(wrappers);
	}

	private void makeWrappers(LinkedList<MLElementWrapper> wrappers) {
		// Make draggable overlays on the equation
		int algLeft = parentPanel.getAbsoluteLeft();
		int algTop = parentPanel.getAbsoluteTop();
		for (MLElementWrapper wrap : wrappers) {

			if (wrap == null) {
				continue;
			}
			if(wrap.getElementWrapped() == null){
				Log.severe("no element for: " + wrap.toString());
				continue;
			}
			int wrapLeft = wrap.getElementWrapped().getAbsoluteLeft();
			int wrapTop = wrap.getElementWrapped().getAbsoluteTop();

			int positionLeft = wrapLeft - algLeft;
			int positionTop = wrapTop - algTop;

			int width = (int) ((0.75) * draggableEquation.getOffsetHeight());
			int height = draggableEquation.getOffsetHeight();

			wrap.setWidth(width + "px");
			wrap.setHeight(height + "px");

			parentPanel.add(wrap, positionLeft, positionTop);
		}
	}
}
