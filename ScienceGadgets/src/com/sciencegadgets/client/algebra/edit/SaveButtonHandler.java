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
import com.sciencegadgets.client.algebra.EquationValidator;
import com.sciencegadgets.shared.TypeML;

public class SaveButtonHandler implements ClickHandler {

	private final DatabaseHelperAsync dataBase = GWT
			.create(DatabaseHelper.class);

	@Override
	public void onClick(ClickEvent arg0) {
		try {
			final String mathML = JSNICalls.elementToString(Moderator.getCurrentMathTree().getMathMLClone());
			if (mathML.contains(ChangeNodeMenu.NOT_SET)) {
				Window.alert("All new entities (" + ChangeNodeMenu.NOT_SET
						+ ") must be set or removed before saving");
				return;
			}
			if (!mathML.contains("<"+TypeML.Variable.getTag())) {
				Window.alert("The equation must contain at least one variable");
				return;
			}
			if(!new EquationValidator().validateQuantityKinds(Moderator.getCurrentMathTree())) {
				return;
			}

			String html = JSNICalls.elementToString(Moderator.getCurrentMathTree()
					.getDisplayClone().getElement());

			dataBase.saveEquation(mathML, html, new AsyncCallback<String>() {

				@Override
				public void onSuccess(String result) {
					if (result != null) {
						Window.alert("Saved!");
						JSNICalls.log("Saved: " + result);
					} else {
						Window.alert("Save didn't work");
						JSNICalls.error("Save Failed, MathML not well formed: "
								+ mathML);
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Save didn't work");
					JSNICalls.error("Save RPC Failed: "
							+ caught.getCause().toString());
				}
			});
		} catch (Exception e) {
			Window.alert("Could not save equation");
			JSNICalls.log("Save Fail: " + e.toString());
		}
	}
}