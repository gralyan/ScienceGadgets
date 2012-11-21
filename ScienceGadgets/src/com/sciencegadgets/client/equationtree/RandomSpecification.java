package com.sciencegadgets.client.equationtree;

import java.awt.ScrollPane;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.algebramanipulation.Moderator;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class RandomSpecification extends PopupPanel {

	MathMLBindingNode mlNode;
	RandomSpecification randSpec = this;

	public static final String ALWAYS = "A";
	public static final String SOMETIMES = "S";
	public static final String NEVER = "N";
	public static final String RANDOM_SYMBOL = "?";

	private RadioButton neverNeg;
	private RadioButton sometimesNeg;
	private RadioButton alwaysNeg;
	private DoubleBox upparBound;
	private DoubleBox lowerBound;

	private IntegerBox decPlace;
	private Label response;

	RandomSpecification(MathMLBindingNode mlNode) {
		this.mlNode = mlNode;

		VerticalPanel mainPanel = new VerticalPanel();
		ScrollPanel scrollPanel = new ScrollPanel(mainPanel);

		Label l1 = new Label("Negative:");
		l1.setStyleName("rowHeader");
		mainPanel.add(l1);

		neverNeg = new RadioButton("neg", "Never");
		sometimesNeg = new RadioButton("neg", "Sometimes");
		alwaysNeg = new RadioButton("neg", "Always");
		HorizontalPanel negativeSelection = new HorizontalPanel();
		negativeSelection.add(neverNeg);
		negativeSelection.add(sometimesNeg);
		negativeSelection.add(alwaysNeg);
		mainPanel.add(negativeSelection);

		Label l2 = new Label("Range:");
		l2.setStyleName("rowHeader");
		mainPanel.add(l2);

		HorizontalPanel rangeSelection = new HorizontalPanel();
		lowerBound = new DoubleBox();
		rangeSelection.add(lowerBound);
		rangeSelection.add(new Label("\u2264 x \u003C"));
		upparBound = new DoubleBox();
		rangeSelection.add(upparBound);
		mainPanel.add(rangeSelection);

		Label label3 = new Label(
				"Decimal places (0-integers, 1-tenths place...):");
		label3.setStyleName("rowHeader");
		mainPanel.add(label3);

		decPlace = new IntegerBox();
		mainPanel.add(decPlace);

		Button okButton = new Button("OK", new OkClickHandler());
		mainPanel.add(okButton);

		response = new Label("");
		response.getElement().getStyle().setColor("red");
		mainPanel.add(response);

		this.add(scrollPanel);

	}

	public void setNode(MathMLBindingNode mlNode) {
		this.mlNode = mlNode;
	}

	private class OkClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
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
				return;
			} else if (upperB < lowerB) {
				response.setText("The upper bound must be greater than the lower bound");
				return;
			} else if (upperB < 0 || lowerB < 0) {
				response.setText("The bounds shouldn't be negative");
				return;
			} else {
				response.setText("");
				randSpec.hide();
				mlNode.setSymbol(RANDOM_SYMBOL);
				mlNode.getMLNode().setAttribute("data-randomness", neg + "-" + lowerB + "-" + upperB + "-" + decP);
				Moderator.reloadEquationPanel("");
			}
		}

	}

}
