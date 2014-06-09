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
import com.sciencegadgets.shared.TypeSGET;

public class SaveButtonHandler implements ClickHandler {

	private final DatabaseHelperAsync dataBase = GWT
			.create(DatabaseHelper.class);
	private static final String RE_CREATE_UNITS = "recreate";

	@Override
	public void onClick(ClickEvent arg0) {
		try {
			final String mathXML = Moderator.getCurrentEquationTree()
					.getEquationXMLString();

			if (mathXML.contains(ChangeNodeMenu.NOT_SET)) {
				Window.alert("All new entities (" + ChangeNodeMenu.NOT_SET
						+ ") must be set or removed before saving");
				reCreateUnitsCheck(mathXML);
				return;
			}
//			if (!mathXML.contains("<" + TypeSGET.Variable.getTag())) {
//				Window.alert("The equation must contain at least one variable");
//				return;
//			}
			
			try {
				Moderator.getCurrentEquationTree().validateTree();
			} catch (IllegalStateException e) {
				String message = e.getMessage();
				if(message == null) {
					Window.alert("This equation is invalid, please rebuild it and try again");
				}else {
					Window.alert(message);
				}
				JSNICalls.error(e.getCause().toString());
				return;
			}

			String html = JSNICalls.elementToString(Moderator
					.getCurrentEquationTree().getDisplayClone().getElement());

//			dataBase.saveEquation(mathXML, html, new AsyncCallback<String>() {
//
//				@Override
//				public void onSuccess(String result) {
//					if (result != null) {
//						Window.alert("Saved!");
//						JSNICalls.log("Saved: " + result);
//					} else {
//						Window.alert("Save didn't work");
//						JSNICalls.error("Save Failed, MathML not well formed: "
//								+ mathXML);
//					}
//				}
//
//				@Override
//				public void onFailure(Throwable caught) {
//					Window.alert("Save didn't work");
//					JSNICalls.error("Save RPC Failed: "
//							+ caught.getCause().toString());
//				}
//			});
			
			
			///////////////////////////////////////////////
			//Show HTML
			/////////////////////////////////////////////
			System.out.println(",// \n\""+html.replace("\"", "\\\"")+"\"\n\n");
			JSNICalls.log(",// \n\""+html.replace("\"", "\\\"")+"\"\n\n");
			
			
			
			
			
			
			
		} catch (Exception e) {
			Window.alert("Could not save equation");
			JSNICalls.error("Save Fail: " + e.toString());
			JSNICalls.error(e.getCause().toString());
			JSNICalls.error(e.getMessage());
		}
	}

	private void reCreateUnitsCheck(String mathML) {

		// Simple method of re-creating the entire set of units
		if (mathML.contains(RE_CREATE_UNITS)) {
			dataBase.reCreateUnits(new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable arg0) {
					Window.alert("Re-creation FAILED");
				}

				@Override
				public void onSuccess(Void arg0) {
					Window.alert("Re-creation success!");
				}
			});
		}
	}
}