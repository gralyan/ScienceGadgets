package com.admin.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class Admin implements EntryPoint {

	TextBox inputBox = new TextBox();
	AppEngineData data = new AppEngineData();


	@Override
	public void onModuleLoad() {
		Button saveButton = new Button("Save");
		RootPanel.get().add(inputBox);
		RootPanel.get().add(saveButton);

		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				data.saveEquation(inputBox.getText());
			}
		});

	}

}
