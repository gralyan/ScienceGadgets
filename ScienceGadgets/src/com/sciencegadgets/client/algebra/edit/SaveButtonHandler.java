package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.DatabaseHelperAsync;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;


public class SaveButtonHandler implements ClickHandler {

	private final DatabaseHelperAsync dataBase = GWT
			.create(DatabaseHelper.class);

	@Override
	public void onClick(ClickEvent arg0) {
		try {
			String equation = Moderator.mathTree.getRoot().toString();
			if (equation.contains(ChangeNodeMenu.NOT_SET)) {
				JSNICalls.log("Not set: " + equation);
				Window.alert("All new entities (" + ChangeNodeMenu.NOT_SET
						+ ") must be set or removed before saving");
				return;
			}
			dataBase.saveEquation(equation, new AsyncCallback<String>() {

				@Override
				public void onSuccess(String result) {
					Window.alert("Saved!");
					JSNICalls.log("Saved: " + result);
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Save failed "
							+ caught.getCause().toString());
					JSNICalls.error("Save Failed: "
							+ caught.getCause().toString());
				}
			});
		} catch (Exception e) {
			Window.alert("Could not save equation");
			JSNICalls.log("Save Fail: " + e.toString());
		}
	}
}