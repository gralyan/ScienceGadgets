package com.sciencegadgets.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class ScienceGadgets implements EntryPoint {

	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	

	public void onModuleLoad() {
		
		try {
			// /////////////////////////////////////////
			// experimenting with RPC calls
			// ////////////////////////////////

			final Button sendButton = new Button("Send");

			RootPanel.get("appArea").add(sendButton);
			// RootPanel.get().add(new EquationWriter());

			ClickHandler handler = new ClickHandler() {
				public void onClick(ClickEvent event) {
					//string2MathML_BySymja_OnServer("a+b");
				}
			};

			sendButton.addClickHandler(handler);
			
		} catch (Exception e) {
			e.printStackTrace();
			Window.alert("Please refresh Page");
		}
	}

	/**
	 * Async call to server to parse the equation using Symja
	 * 
	 * @param textToServer
	 */
	@SuppressWarnings("unused")
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

}
