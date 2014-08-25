package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.DatabaseHelperAsync;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationValidator;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.shared.TypeSGET;

public class SaveButtonHandler implements ClickHandler {

	private final DatabaseHelperAsync dataBase = GWT
			.create(DatabaseHelper.class);
	private EquationValidator eqValidator;
	private static final String RE_CREATE_UNITS = "recreate";

	static ProblemDetails problemDetails = null;

	@Override
	public void onClick(ClickEvent arg0) {
		

		final String mathXML = Moderator.getCurrentEquationTree()
				.getEquationXMLString();

		if (mathXML.contains(ChangeNodeMenu.NOT_SET)) {
			Window.alert("All new entities (" + ChangeNodeMenu.NOT_SET
					+ ") must be set or removed before saving");
			reCreateUnitsCheck(mathXML);
			return;
		}
		if (!mathXML.contains("<" + TypeSGET.Variable.getTag())) {
			Window.alert("The equation must contain at least one variable");
			return;
		}

		try {
			EquationTree currentTree = Moderator.getCurrentEquationTree();
			currentTree.validateTree();
			if(eqValidator == null) {
				eqValidator = new EquationValidator();
			}
			eqValidator.validateQuantityKinds(currentTree);
		} catch (IllegalStateException e) {
			String message = e.getMessage();
			if (message == null) {
				Window.alert("This equation is invalid, please rebuild it and try again");
			} else {
				Window.alert(message);
			}
			JSNICalls.error(e.getCause().toString());
			return;
		}


		if (problemDetails == null) {
			problemDetails = new ProblemDetails();

			problemDetails.addOkHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent arg0) {

					if(!"".equals(problemDetails.titleInput.getText()) && !"".equals(problemDetails.descriptionInput.getText()) && problemDetails.requiredBadge != null) {
					saveEquation(mathXML);
					problemDetails.disappear();
					}
				}
			});
		}

		problemDetails.appear();
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
	

	private void saveEquation(final String mathXML) {
		try {
			String html = JSNICalls.elementToString(Moderator
					.getCurrentEquationTree().getDisplayClone()
					.getElement());

			dataBase.saveEquation(mathXML, html,
					new AsyncCallback<Equation>() {

						@Override
						public void onSuccess(Equation equation) {
							if (equation != null) {
								Window.alert("Saved!");
								JSNICalls.log("Saved: " + equation.getMathML());

								dataBase.saveProblem(problemDetails.titleInput.getText(), problemDetails.descriptionInput.getText(), problemDetails.requiredBadge, equation,
										new AsyncCallback<Problem>() {

											@Override
											public void onSuccess(Problem problem) {
												if (problem != null) {
													JSNICalls.log("Saved problem");
												}
											}

											@Override
											public void onFailure(Throwable caught) {
												Window.alert("Save didn't work");
												JSNICalls.error("Save RPC Failed: "
														+ caught.getCause().toString());
											}
										});
							} else {
								Window.alert("Save didn't work");
								JSNICalls
										.error("Save Failed, MathML not well formed: "
												+ mathXML);
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Save didn't work");
							JSNICalls.error("Save RPC Failed: "
									+ caught.getCause().toString());
						}
					});

			// ///////////////////////////////////////////////
			// //Show HTML
			// /////////////////////////////////////////////
			// System.out.println(",// \n\""+html.replace("\"",
			// "\\\"")+"\"\n\n");
			// JSNICalls.log(",// \n\""+html.replace("\"",
			// "\\\"")+"\"\n\n");

		} catch (Exception e) {
			Window.alert("Could not save equation");
			JSNICalls.error("Save Fail: " + e.toString());
			JSNICalls.error(e.getCause().toString());
			JSNICalls.error(e.getMessage());
		}

	}

	class ProblemDetails extends Prompt {
		TextBox titleInput = new TextBox();
		TextBox descriptionInput = new TextBox();
		Badge requiredBadge;

		ProblemDetails() {

			SelectionPanel badgeSelection = new SelectionPanel("RequiredBadge",
					new SelectionHandler() {
						@Override
						public void onSelect(Cell selected) {
							requiredBadge = (Badge) selected.getEntity();
						}
					});

			for (Badge badge : Badge.values()) {
				badgeSelection.add(badge.toString(), badge.name(), badge);
			}
			add(new Label("Title"));
			add(titleInput);
			add(new Label("Description"));
			add(descriptionInput);
			add(badgeSelection);

		}
		
		@Override
		public void appear() {
			titleInput.setText("");
			descriptionInput.setText("");
			requiredBadge = null;
			
			super.appear();
		}


	}
}