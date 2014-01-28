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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.algebra.MathTree.MathNode;

public class RandomSpecPanel extends FlowPanel {

	RandomSpecPanel randSpec = this;

	public static final String ALWAYS = "A";
	public static final String SOMETIMES = "S";
	public static final String NEVER = "N";
	
	public static final String RANDOM_SYMBOL = "?";
	public static final String DELIMITER = "_";

	private RadioButton neverNeg;
	private RadioButton sometimesNeg;
	private RadioButton alwaysNeg;
	private DoubleBox upparBound;
	private DoubleBox lowerBound;

	private IntegerBox decPlace;
	private Label response;
	Button okButton = new Button("Set Random");

	RandomSpecPanel() {

		this.getStyleElement().getStyle().setBackgroundColor("#ADD850");
		this.setWidth("100%");
		this.setHeight("100%");
		
		VerticalPanel mainPanel = new VerticalPanel();
		ScrollPanel scrollPanel = new ScrollPanel(mainPanel);

		Label label1 = new Label("Negativity");
		label1.setStyleName("rowHeader");
		mainPanel.add(label1);

		neverNeg = new RadioButton("neg", "Never");
		sometimesNeg = new RadioButton("neg", "Sometimes");
		alwaysNeg = new RadioButton("neg", "Always");
		HorizontalPanel negativeSelection = new HorizontalPanel();
		negativeSelection.add(neverNeg);
		negativeSelection.add(sometimesNeg);
		negativeSelection.add(alwaysNeg);
		mainPanel.add(negativeSelection);

		Label label2 = new Label("Range");
		label2.setStyleName("rowHeader");
		label2.getElement().getStyle().setMarginTop(5, Unit.PX);
		mainPanel.add(label2);

		HorizontalPanel rangeSelection = new HorizontalPanel();
		lowerBound = new DoubleBox();
		rangeSelection.add(lowerBound);
		rangeSelection.add(new Label("\u2264 x \u003C"));
		upparBound = new DoubleBox();
		rangeSelection.add(upparBound);
		mainPanel.add(rangeSelection);

		Label label3 = new Label(
				"Decimal places (0-integers, 1-tenths place...)");
		label3.setStyleName("rowHeader");
		label3.getElement().getStyle().setMarginTop(5, Unit.PX);
		mainPanel.add(label3);

		decPlace = new IntegerBox();
		mainPanel.add(decPlace);

		mainPanel.add(okButton);

		response = new Label("");
		response.getElement().getStyle().setColor("red");
		mainPanel.add(response);

		this.add(scrollPanel);

	}
	
	public void addOkClickHandler(ClickHandler handler) {
		okButton.addClickHandler(handler);
	}

	public String getRandomness() {

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
			
			//Only proceed if all the input was valid
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
//				randSpec.hide();
//				mlNode.setSymbol(RANDOM_SYMBOL);
//				mlNode.getMLNode().setAttribute("data-randomness", neg + DELIMITER + lowerB + DELIMITER + upperB + DELIMITER + decP);
//				numberInput.setText(RANDOM_SYMBOL);
//				numberInput.setTitle(neg + DELIMITER + lowerB + DELIMITER + upperB + DELIMITER + decP);
//				Moderator.reloadEquationPanel(null, null);
//				Moderator.setActivity(Activity.algebra);
				return neg + DELIMITER + lowerB + DELIMITER + upperB + DELIMITER + decP;
			}

	}

}