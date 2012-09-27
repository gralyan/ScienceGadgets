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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.Log;
import com.sun.java.swing.plaf.windows.resources.windows;

public class AlgebraManipulator extends AbsolutePanel {
	HTML draggableEquation;
	AbsolutePanel parentPanel;
	LinkedList<MLElementWrapper> wrappers;

	public AlgebraManipulator(HTML draggableEquation,
			LinkedList<MLElementWrapper> wrappers, AbsolutePanel parentPanel) {

		this.draggableEquation = draggableEquation;
		this.parentPanel = parentPanel;
		this.wrappers = wrappers;
		
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
		parseMathJax();
		
		//TODO make Wrappers should be queued after mathjax typsetting, this timer is a workaround for now
//		queueMakeWrappers();
	    Timer t = new Timer() {
	      public void run() {
	        makeWrappers();
	      }
	    };
	    t.schedule(2000);
	}

	private void makeWrappers() {
		
		// Make draggable overlays on the equation
		int algLeft = parentPanel.getAbsoluteLeft();
		int algTop = parentPanel.getAbsoluteTop();
		
		for (MLElementWrapper wrap : wrappers) {

			if (wrap == null) {
				continue;
			}
			
			// Wrap will be placed around the MathJax SVG images
			if(wrap.getJohnNode() == null){
				Log.severe("no element for: " + wrap.toString());
				continue;
			}
			
			Element el = DOM.getElementById("svg"+wrap.getJohnNode().getId());

			int wrapLeft = el.getAbsoluteLeft();
			int wrapTop = el.getAbsoluteTop();

//			using native mathML rather than MathJax
			
//			if(wrap.getElementWrapped() == null){
//				Log.severe("no element for: " + wrap.toString());
//				continue;
//			}
//			int wrapLeft = wrap.getElementWrapped().getAbsoluteLeft();
//			int wrapTop = wrap.getElementWrapped().getAbsoluteTop();

			int positionLeft = wrapLeft - algLeft;
			int positionTop = wrapTop - algTop;

//			using native mathML rather than MathJax
			
//			int width = (int) ((0.75) * draggableEquation.getOffsetHeight());
//			int height = draggableEquation.getOffsetHeight();
			
			wrap.setWidth(getWidth(el) + "px");
			wrap.setHeight(getHeight(el) + "px");
			
//			System.out.println("\nwidth"+getWidth(el)+" height"+getHeight(el)+" top"+wrapTop+" left"+wrapLeft);

			parentPanel.add(wrap, positionLeft, positionTop);
//			RootPanel.get().add(wrap, positionLeft, positionTop);
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
	//Neither of these approaches seem to work
//	public native void queueMakeWrappers() /*-{
	
	//Sets makeWrappers as a global function to be queued by MathJax.Hub.Queue()
//		$wnd.makeWrappers = $entry(this.@com.sciencegadgets.client.algebramanipulation.AlgebraManipulator::makeWrappers());
	
	//Queues makeWrappers directly
//		$doc.queue(this.@com.sciencegadgets.client.algebramanipulation.AlgebraManipulator::makeWrappers());
//															}-*/;


}
