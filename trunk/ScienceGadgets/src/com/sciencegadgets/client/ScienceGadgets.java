package com.sciencegadgets.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ScienceGadgets implements EntryPoint {

	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	public static Grid algOut = new Grid(1, 1);
	public static HorizontalPanel algebraPanel = new HorizontalPanel();
	public static ListBox varBox;
	public static ListBox funBox;
	public static TextBox coefBox;
	public static AbsolutePanel algDragPanel = new AbsolutePanel();
	public static ScrollPanel spAlg = new ScrollPanel(algOut);
	public static AbsolutePanel apTree = new AbsolutePanel();
	EquationDatabase data;

	public void onModuleLoad() {
		//EquationBrowser eq = new EquationBrowser();
		createAlgebraPanel();

		RootPanel.get().add(algebraPanel);

		
		// /////////////////////////////////////////
		// experimental
		// ////////////////////////////////

		final Button sendButton = new Button("Send");

		RootPanel.get().add(sendButton);
		// RootPanel.get().add(new EquationWriter());

		ClickHandler handler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				//string2MathML_BySymja_OnServer("a+b");
			}
		};

		sendButton.addClickHandler(handler);
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
		varBox.getElement().setId("varBox");
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
		algebraPanel.setStyleName("albebraPanel");
		VerticalPanel AlgebraVerticalPanel = new VerticalPanel();
		AlgebraVerticalPanel.add(spAlg);
		AlgebraVerticalPanel.add(algDragPanel);
		algebraPanel.add(AlgebraVerticalPanel);
		algebraPanel.add(apTree);

		// Add styles
		algOut.setStyleName("algOutPanel");
		algOut.getElement().setId("algOut");
		spAlg.setStyleName("algOutPanel");
		algDragPanel.setStyleName("algDragPanel");
		algDragPanel.getElement().setId("algDragPanel");
		// eqTreePanel.setStyleName("treePanel");
		apTree.setStyleName("apTree");
		apTree.getElement().setId("apTree");

	}

	/**
	 * Async call to server to parse the equation using Symja
	 * 
	 * @param textToServer
	 */
	private void string2MathML_BySymja_OnServer(String textToServer) {

		greetingService.greetServer(textToServer, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Window.alert("Math parseing FAIL :(");
			}

			public void onSuccess(String result) {
				//labelSumEq.setHTML(result);
				//onEqSelect(result);
			}
		});
	}
	class ToBothSidesHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			int newRowCount = algOut.getRowCount() + 2;
			algOut.resizeRows(newRowCount);

			String inpFun = funBox.getItemText(funBox.getSelectedIndex());
			String inpCoef = coefBox.getText();
			String inpVar = varBox.getItemText(varBox.getSelectedIndex());

			try {
				Integer.parseInt(inpCoef);
			} catch (NumberFormatException e) {
				Window.alert("The coefficient must be a number");
				return;
			}

			algOut.setWidget(newRowCount - 2, 0, new Label(inpFun + inpCoef
					+ inpVar + "    " + inpFun + inpCoef + inpVar));
			algOut.setWidget(newRowCount - 1, 0, new Label("equation"));
			spAlg.scrollToBottom();
		}

	}

}
