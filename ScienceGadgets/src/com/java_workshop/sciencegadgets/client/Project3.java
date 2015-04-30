package com.java_workshop.sciencegadgets.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class Project3 implements EntryPoint {

	public static final String CENTERED_CSS = "centered";
	public static final String UPPER_AREA_ID = "upperArea";
	public static final String LOWER_AREA_ID = "lowerArea";

	// We can make the minimum and maximum fields so they can be changed. You
	// may want to change them when the user increases levels to make it harder
	int minBoxes = 10;
	int maxBoxes = 20;

	public void onModuleLoad() {

		final int boxCount = (int) (Math.random() * (maxBoxes - minBoxes) + minBoxes);
		final Boxes boxes = new Boxes(RootPanel.get(UPPER_AREA_ID), boxCount);
		boxes.addStyleName(CENTERED_CSS);
		boxes.getElement().getStyle().setPaddingTop(50, Unit.PX);
		RootPanel.get(UPPER_AREA_ID).add(boxes);

		// Make a new object and specify the abstract method.
		AnswerInputBox inBox = new AnswerInputBox() {
			@Override
			void onInput() {

				// Since the input value may not be a number, we try to parse it
				// an catch if we can't
				try {
					int inputInt = Integer.parseInt(inputBox.getValue());
					GWT.log(inputBox.getValue());
					boxes.onAnswer(inputInt == boxCount);
				} catch (NumberFormatException e) {
					Window.alert("That's not a number");
				}
			}
		};

		RootPanel.get(LOWER_AREA_ID).add(inBox);
		inBox.addStyleName(CENTERED_CSS);
		inBox.setFocus(true);
	}
}