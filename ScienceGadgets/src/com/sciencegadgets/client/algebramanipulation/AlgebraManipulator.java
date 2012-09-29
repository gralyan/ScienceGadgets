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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Log;

public class AlgebraManipulator extends AbsolutePanel {
	HTML draggableEquation;
	AbsolutePanel parentPanel;
	LinkedList<MLElementWrapper> wrappers;
	private Timer timer;

	public AlgebraManipulator(HTML draggableEquation,
			LinkedList<MLElementWrapper> wrappers, AbsolutePanel parentPanel) {

		this.draggableEquation = draggableEquation;
		this.parentPanel = parentPanel;
		this.wrappers = wrappers;

		parentPanel.clear();
		parentPanel.add(draggableEquation);
		parseMathJax();

		//Only make wrappers after the equation has been typeset by MathJax
		timer = new Timer() {
			public void run() {
				System.out.println("2CHECKINGGGGGG");
				checkIfWeCanDraw();
			}
		};
		timer.scheduleRepeating(100);
	}

	//Repeatedly polls to see if MathJax is done typesetting
	private void checkIfWeCanDraw() {
		String eqId = "svg" + wrappers.get(0).getJohnNode().getId();
		Element eqEl = DOM.getElementById(eqId);

		if (eqEl != null) {
			System.out.println("2CAN DRAWWW!!!!!!");
			timer.cancel();
			makeWrappers();
		}
	}

	// Make draggable overlays on the equation
	private void makeWrappers() {

		int algLeft = parentPanel.getAbsoluteLeft();
		int algTop = parentPanel.getAbsoluteTop();

		for (MLElementWrapper wrap : wrappers) {

			if (wrap == null) {
				continue;
			}

			// Wrap will be placed around the MathJax SVG images
			if (wrap.getJohnNode() == null) {
				Log.severe("no element for: " + wrap.toString());
				continue;
			}

			Element el = DOM.getElementById("svg" + wrap.getJohnNode().getId());

			int wrapLeft = el.getAbsoluteLeft();
			int wrapTop = el.getAbsoluteTop();

			int positionLeft = wrapLeft - algLeft;
			int positionTop = wrapTop - algTop;

			wrap.setWidth(getWidth(el) + "px");
			wrap.setHeight(getHeight(el) + "px");

			parentPanel.add(wrap, positionLeft, positionTop);
		}
	}

	public native void parseMathJax() /*-{
		$doc.prettify("scienceGadgetArea");
	}-*/;

	public static native double getWidth(Element elm) /*-{

		return elm.getBoundingClientRect().width;
		//		return elm.getBBox().width;
	}-*/;

	public static native double getHeight(Element elm) /*-{
		return elm.getBoundingClientRect().height;
		//		return elm.getBBox().height;
	}-*/;
}
