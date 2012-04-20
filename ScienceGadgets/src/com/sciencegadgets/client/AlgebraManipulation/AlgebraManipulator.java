package com.sciencegadgets.client.AlgebraManipulation;

import java.util.LinkedList;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;

public class AlgebraManipulator extends AbsolutePanel {
	HTML draggableEquation;
	AbsolutePanel parentPanel;

	public AlgebraManipulator(HTML draggableEquation,
			LinkedList<MLElementWrapper> wrappers, AbsolutePanel parentPanel) {

		this.draggableEquation = draggableEquation;
		this.parentPanel = parentPanel;
		//this.setStyleName(parentPanel.getStyleName());
		
		Element draggableEquationElement = (Element) draggableEquation
				.getElement().getElementsByTagName("math").getItem(0);
		if (draggableEquationElement == null) {
			// The main mathML element is fmath in chrome, math in firefox
			draggableEquationElement = (Element) draggableEquation.getElement()
					.getElementsByTagName("fmath").getItem(0);
		}
		DOM.setElementAttribute(draggableEquationElement, "mathsize", "300%");
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
