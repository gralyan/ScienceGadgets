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
package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.ui.CSS;

public abstract class RandomSpecPanel extends FlowPanel {

	RandomSpecPanel randSpec = this;

	public static final String ALWAYS = "A";
	public static final String SOMETIMES = "S";
	public static final String NEVER = "N";

	public static final String RANDOM_SYMBOL = "?";
	public static final String RANDOMNESS_DELIMITER = "_";
	public static final String RANDOM_PROVIDED = "provided";

	private RadioButton neverNeg;
	private RadioButton sometimesNeg;
	private RadioButton alwaysNeg;
	private DoubleBox upparBound;
	private DoubleBox lowerBound;

	private IntegerBox decPlace;
	private Label response;
	Button setRandonButton = new Button("Set Random");

	private final VerticalPanel autoRandomPanel = new VerticalPanel();
	private final FlowPanel providedRandomPanel = new FlowPanel();
	private final ScrollPanel scrollPanel = new ScrollPanel();
	private final ToggleButton autoOrProvidedToggle = new ToggleButton("Provide",
			"Auto");

	public RandomSpecPanel() {

		this.getStyleElement().getStyle().setBackgroundColor("#ADD850");
		this.setWidth("100%");
		this.setHeight("100%");

		makeAutoRandomPanel();
		makeProvidedRandomPanel();

		autoOrProvidedToggle.addStyleName(CSS.TRANSFORMATION_BUTTON);
		autoOrProvidedToggle.getElement().getStyle().setMargin(10, Unit.PX);
		autoOrProvidedToggle.getElement().getStyle().setBackgroundColor("silver");;
		autoOrProvidedToggle.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (autoOrProvidedToggle.getValue()) {
					scrollPanel.remove(autoRandomPanel);
					scrollPanel.add(providedRandomPanel);
					onSetRandom();
				} else {
					scrollPanel.remove(providedRandomPanel);
					scrollPanel.add(autoRandomPanel);
				}
			}
		});

		scrollPanel.add(autoRandomPanel);

		this.add(autoOrProvidedToggle);
		this.add(scrollPanel);

	}

	private void makeProvidedRandomPanel() {
		providedRandomPanel.setSize("100%", "100%");

		providedRandomPanel
				.add(new Label(
						"The random number should be provided programmatically. "
						+ "This allows the linking website to retain and use the "
						+ "numbers it generated. For example, a website can provide "
						+ "a question with random numbers and expect the appropriate "
						+ "answer after the equation has been solved"));

	}

	private void makeAutoRandomPanel() {

		Label label1 = new Label("Negativity");
		label1.setStyleName(CSS.ROW_HEADER);
		autoRandomPanel.add(label1);

		neverNeg = new RadioButton("neg", "Never");
		sometimesNeg = new RadioButton("neg", "Sometimes");
		alwaysNeg = new RadioButton("neg", "Always");
		HorizontalPanel negativeSelection = new HorizontalPanel();
		negativeSelection.add(neverNeg);
		negativeSelection.add(sometimesNeg);
		negativeSelection.add(alwaysNeg);
		autoRandomPanel.add(negativeSelection);

		Label label2 = new Label("Range");
		label2.setStyleName(CSS.ROW_HEADER);
		label2.getElement().getStyle().setMarginTop(5, Unit.PX);
		autoRandomPanel.add(label2);

		HorizontalPanel rangeSelection = new HorizontalPanel();
		lowerBound = new DoubleBox();
		lowerBound.setSize("50px", "30px");
		rangeSelection.add(lowerBound);
		rangeSelection.add(new Label("\u2264 x \u003C"));
		upparBound = new DoubleBox();
		upparBound.setSize("50px", "30px");
		rangeSelection.add(upparBound);
		autoRandomPanel.add(rangeSelection);

		Label label3 = new Label("Decimal places");
		label3.setStyleName(CSS.ROW_HEADER);
		label3.getElement().getStyle().setMarginTop(5, Unit.PX);
		autoRandomPanel.add(label3);

		decPlace = new IntegerBox();
		decPlace.setSize("50px", "30px");
		decPlace.setText("0");
		autoRandomPanel.add(decPlace);

		setRandonButton.setSize("100%", "100px");
		autoRandomPanel.add(setRandonButton);
		
		setRandonButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSetRandom();
			}
		});

		response = new Label("");
		response.getElement().getStyle().setColor("red");
		autoRandomPanel.add(response);

	}
	
	protected abstract void onSetRandom();

	public String getRandomness() {
		
		if(autoOrProvidedToggle.getValue()) {
			return RANDOM_PROVIDED;
		}

		String neg = null;
		if (alwaysNeg.getValue()) {
			neg = ALWAYS;
		} else if (sometimesNeg.getValue()) {
			neg = SOMETIMES;
		} else if (neverNeg.getValue()) {
			neg = NEVER;
		}

		Double upperB = upparBound.getValue();
		Double lowerB = lowerBound.getValue();

		Integer decP = decPlace.getValue();

		// Only proceed if all the input was valid
		if (neg == null || upperB == null || lowerB == null || decP == null) {
			response.setText("All the fields must be filled");
			return null;
		} else if (upperB < lowerB) {
			response.setText("The upper bound must be greater than the lower bound");
			return null;
		} else if (upperB < 0 || lowerB < 0) {
			response.setText("The bounds shouldn't be negative");
			return null;
		} else {
			response.setText("");
			return neg + RANDOMNESS_DELIMITER + lowerB + RANDOMNESS_DELIMITER
					+ upperB + RANDOMNESS_DELIMITER + decP;
		}

	}

}
