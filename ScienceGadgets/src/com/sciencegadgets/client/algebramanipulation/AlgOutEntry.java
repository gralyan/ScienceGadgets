package com.sciencegadgets.client.algebramanipulation;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.equationbrowser.EquationDatabase;

public class AlgOutEntry implements EntryPoint {
	public static Grid algOut = new Grid(1, 1);
	public static HorizontalPanel algebraPanel = new HorizontalPanel();
	public static ListBox varBox;
	public static ListBox funBox;
	public static TextBox coefBox;
	public static AbsolutePanel algDragPanel = new AbsolutePanel();
	public static ScrollPanel spAlg = new ScrollPanel(algOut);
	EquationDatabase data;

	public void onModuleLoad() {
		createAlgebraPanel();
		RootPanel.get().add(algebraPanel);

	}

	private void createAlgebraPanel() {

		// Assemble algebra menu panel
		HorizontalPanel algMenuPanel = new HorizontalPanel();
		algMenuPanel.setHeight("2em");
		algMenuPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		// function
		funBox = new ListBox();
		data = new EquationDatabase();
		for (String fun : data.functions) {
			funBox.addItem(fun);
		}
		algMenuPanel.add(funBox);
		// coefficient
		coefBox = new TextBox();
		coefBox.setValue("1");
		coefBox.setWidth("2em");
		algMenuPanel.add(coefBox);
		// variable
		varBox = new ListBox();
		algMenuPanel.add(varBox);
		// to both sides button
		Button toBothSides = new Button("To Both Sides",
				new ToBothSidesHandler());
		toBothSides.setWidth("5em");
		algMenuPanel.setHeight("2em");
		algMenuPanel.add(toBothSides);

		// Assemble Algebra panel
		algebraPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		algebraPanel.setStyleName("algebraPanel");
		VerticalPanel AlgebraVerticalPanel = new VerticalPanel();
		AlgebraVerticalPanel.add(spAlg);
		AlgebraVerticalPanel.add(algDragPanel);
		algebraPanel.add(AlgebraVerticalPanel);

		// Add styles
		algOut.setStyleName("algOutPanel");
		spAlg.setStyleName("algOutPanel");
		algDragPanel.setStyleName("algDragPanel");

	}

	public static void updateAlgOut(HTML mathML) {
		int newRowCount = algOut.getRowCount() + 2;
		algOut.resizeRows(newRowCount);

		algOut.setWidget(newRowCount - 2, 0, new Label(" to both sides"
		// inpFun + inpCoef+ inpVar + "    " + inpFun + inpCoef + inpVar
				));
		algOut.setWidget(newRowCount - 1, 0, mathML);
		spAlg.scrollToBottom();
	}

	class ToBothSidesHandler implements ClickHandler {

		public void onClick(ClickEvent event) {

			String inpFun = funBox.getItemText(funBox.getSelectedIndex());
			String inpCoef = coefBox.getText();
			String inpVar = varBox.getItemText(varBox.getSelectedIndex());

			try {
				Integer.parseInt(inpCoef);
			} catch (NumberFormatException e) {
				Window.alert("The coefficient must be a number");
				return;
			}
			
			updateAlgOut(new HTML("<span>equation</span>"));
		}

	}
}
