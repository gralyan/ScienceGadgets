package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sciencegadgets.client.DatabaseHelperAsync;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationValidator;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.shared.Diagram;
import com.sciencegadgets.shared.TypeSGET;

public class SaveButtonHandler implements ClickHandler {

	private DatabaseHelperAsync dataBase = DataModerator.database;
	private EquationValidator eqValidator;
	private static final String RE_CREATE_UNITS = "recreate";

	static ProblemSpecification problemSpec = null;

	@Override
	public void onClick(ClickEvent arg0) {

		final EquationTree eTree = Moderator.getCurrentEquationTree();
		final String mathXML = eTree.getEquationXMLString();

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
			eTree.validateTree();
			if (eqValidator == null) {
				eqValidator = new EquationValidator();
			}
			eqValidator.validateQuantityKinds(eTree);
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

		if (problemSpec == null) {
			problemSpec = new ProblemSpecification(this);

			problemSpec.addOkHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent arg0) {

					if (problemSpec.problemSelected != null) {
						saveEquation(eTree, problemSpec.problemSelected);
						problemSpec.disappear();
					} else if (!"".equals(problemSpec.titleInput.getText())
							&& !"".equals(problemSpec.descriptionInput
									.getText())
							&& problemSpec.requiredBadge != null
							&& !problemSpec.varIdPanel.toSolve.getTitle()
									.equals("")) {
						saveEquation(eTree, null);
						problemSpec.disappear();
					} else {
						String errors = "The following are required:\n";
						if("".equals(problemSpec.titleInput.getText())) {
							errors = errors + "Title\n";
						}
						if("".equals(problemSpec.descriptionInput.getText())){
							errors = errors + "Description\n";
						}
						if(problemSpec.requiredBadge == null){
							errors = errors + "Prerequisite Badge\n";
						}
						if(problemSpec.varIdPanel.toSolve.getTitle().equals("")){
							errors = errors + "Variable to Solve\n";
						}
						Window.alert(errors);
					}
				}
			});
		}

		problemSpec.setEquationTree(eTree);
		problemSpec.appear();
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

	private void saveEquation(EquationTree eTree, final Problem problem) {
		try {
			final String mathXML = eTree.getEquationXMLString();
			String html = JSNICalls.elementToString(eTree.getDisplayClone()
					.getElement());

			dataBase.saveEquation(mathXML, html, new AsyncCallback<Equation>() {

				@Override
				public void onSuccess(Equation equation) {
					if (equation == null) {
						Window.alert("Save didn't work");
						JSNICalls
								.error("Save equation returned with equation==null: ");
					} else {
						JSNICalls.log("Saved: " + equation.getMathML());

						if (problem != null) {
							// add equation to selected problem

							problem.addEquation(equation);
							problem.setDescription(problemSpec.descriptionInput
									.getText());
							problem.setToSolveID(problemSpec.varIdPanel.toSolve.getTitle());
							//TODO possibly update image
//							problem.setImage(problemDetails.imageBlobKey,
//									problemDetails.imageURL);
							dataBase.saveEntity(problem,
									new AsyncCallback<Void>() {

										@Override
										public void onSuccess(Void arg0) {
											Window.alert("Saved equation!");
											JSNICalls.log("Saved equation");
										}

										@Override
										public void onFailure(Throwable caught) {
											Window.alert("Update problem failed");
											JSNICalls
													.error("Update problem Failed: "
															+ caught.toString());
											JSNICalls.error(caught.getMessage());
											JSNICalls.error(caught.getCause()
													.toString());
										}
									});

						} else {
							// save new problem

							dataBase.saveProblem(
									problemSpec.titleInput.getText(),
									problemSpec.descriptionInput.getText(),
									problemSpec.requiredBadge,problemSpec.diagram, equation,problemSpec.varIdPanel.toSolve.getTitle(),
									new AsyncCallback<Problem>() {

										@Override
										public void onSuccess(Problem problem) {
											if (problem != null) {
												Window.alert("Saved problem!");
												JSNICalls.log("Saved problem");
											}
										}

										@Override
										public void onFailure(Throwable caught) {
											Window.alert("Save problem failed");
											JSNICalls
													.error("Save Problem Failed: "
															+ caught.toString());
											JSNICalls.error(caught.getMessage());
											JSNICalls.error(caught.getCause()
													.toString());
										}
									});
						}
					}
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Save equation didn't failed");
					JSNICalls.error("Save equation Failed: "
							+ caught.toString());
					JSNICalls.error(caught.getMessage());
					JSNICalls.error(caught.getCause().toString());
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
			Window.alert("Could not save");
			JSNICalls.error("Save Fail: " + e.toString());
			JSNICalls.error(e.getCause().toString());
			JSNICalls.error(e.getMessage());
		}

	}

}