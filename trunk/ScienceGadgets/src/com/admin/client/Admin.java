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

	private final AdminServiceAsync adminService = GWT
			.create(AdminService.class);

	@Override
	public void onModuleLoad() {
		Button saveButton = new Button("Save");
		RootPanel.get().add(inputBox);
		RootPanel.get().add(saveButton);

		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String newEquation = inputBox.getText().toLowerCase()
						.replaceAll("\r|\n|\r\n|\t| {2,}", "")
						.replaceAll("> <", "><");

				adminService.saveEquation(newEquation,
						new AsyncCallback<String>() {

							@Override
							public void onSuccess(String result) {
								Window.alert("Saved as: " + result);
							}
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("FAILED save!!!");
							}
						});

			}
		});

	}

}
