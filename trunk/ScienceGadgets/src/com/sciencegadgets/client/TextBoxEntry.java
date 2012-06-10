package com.sciencegadgets.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.algebramanipulation.EquationTransporter;

public class TextBoxEntry implements EntryPoint {

	TextBox mlInput;
	TextBox jqInput;
	private enum InputTypes { ml, jq}

	@Override
	public void onModuleLoad() {

		mlInput = new TextBox();
		jqInput = new TextBox();
		Button mlButton = new Button("use", new InputHandler(InputTypes.ml));
		Button jqButton = new Button("use", new InputHandler(InputTypes.jq));
		Label mlLabel = new Label("Math ML");

		Grid inputGrid = new Grid(2, 3);
		inputGrid.setStyleName("inputGrid");
		inputGrid.setWidget(0,0 , mlLabel);
		inputGrid.setWidget(0,2 , mlButton);
		inputGrid.setWidget(1,0 , new Label("jqMath"));
		inputGrid.setWidget(1,2 , jqButton);
		inputGrid.setWidget(0,1 , mlInput);
		inputGrid.setWidget(1,1 , jqInput);
		mlInput.setWidth("50em");
		jqInput.setWidth("50em");
		// TODO don't hard code width
//		int maxInputWidth = inputGrid.getOffsetWidth() - (mlButton.getOffsetWidth() + mlLabel.getOffsetWidth());
//		inputGrid.getColumnFormatter().setWidth(1, inputGrid.getOffsetWidth()+"px");

		RootPanel.get().add(inputGrid);
	}

	private class InputHandler implements ClickHandler {

		InputTypes inputType;

		public InputHandler(InputTypes inputType) {
			this.inputType = inputType;
		}

		@Override
		public void onClick(ClickEvent arg0) {

			String equation = "";
			
			switch (inputType) {
			case ml:
				equation = mlInput.getText();
				EquationTransporter.transport(new HTML(equation));
				break;
			case jq:
				equation = jqInput.getText();
				String mathml = EquationTransporter.transport(equation);
				mlInput.setText(mathml);
				break;
			}
			
		}
	}

}
