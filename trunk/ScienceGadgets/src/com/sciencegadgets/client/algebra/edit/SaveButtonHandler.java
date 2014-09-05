package com.sciencegadgets.client.algebra.edit;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.DatabaseHelperAsync;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.UploadButton;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.EquationValidator;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.SelectionButton;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.shared.TypeSGET;

public class SaveButtonHandler implements ClickHandler {

	private final DatabaseHelperAsync dataBase = GWT
			.create(DatabaseHelper.class);
	private EquationValidator eqValidator;
	private static final String RE_CREATE_UNITS = "recreate";
	public static final String PROBLEM_DESCRIPTION_VARIABLE_DELIMETER_START = " -[";
	public static final String PROBLEM_DESCRIPTION_VARIABLE_DELIMETER_END = "]- ";

	static ProblemDetails problemDetails = null;

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

		if (problemDetails == null) {
			problemDetails = new ProblemDetails();

			problemDetails.addOkHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent arg0) {

					if (problemDetails.problemSelected != null) {
						saveEquation(eTree, problemDetails.problemSelected);
						problemDetails.disappear();
					} else if (!"".equals(problemDetails.titleInput.getText())

					&& !"".equals(problemDetails.descriptionInput.getText())
							&& problemDetails.requiredBadge != null) {
						saveEquation(eTree, null);
						problemDetails.disappear();
					}
				}
			});
		}

		problemDetails.refresh();
		problemDetails.varIdPanel.refresh(eTree);
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

	private void saveEquation(EquationTree eTree, final Problem problem) {
		try {
			final String mathXML = eTree.getEquationXMLString();
			String html = JSNICalls.elementToString(eTree.getDisplayClone()
					.getElement());

			dataBase.saveEquation(mathXML, html, new AsyncCallback<Equation>() {

				@Override
				public void onSuccess(Equation equation) {
					if (equation != null) {
						Window.alert("Saved!");
						JSNICalls.log("Saved: " + equation.getMathML());

						if (problem != null) {
							problem.addEquation(equation);
							problem.setDescription(problemDetails.descriptionInput
									.getText());
							dataBase.saveEntity(problem,
									new AsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											Window.alert("Save didn't work");
											JSNICalls.error("Save RPC Failed: "
													+ caught.getCause()
															.toString());
										}

										@Override
										public void onSuccess(Void arg0) {
											JSNICalls.log("Saved equation");
										}
									});

						} else {

							dataBase.saveProblem(
									problemDetails.titleInput.getText(),
									problemDetails.descriptionInput.getText(),
									problemDetails.requiredBadge, equation,
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
													+ caught.getCause()
															.toString());
										}
									});
						}
					} else {
						Window.alert("Save didn't work");
						JSNICalls.error("Save Failed, MathML not well formed: "
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
		protected FlowPanel topPanel = new FlowPanel();
		protected FlowPanel leftPanel = new FlowPanel();
		protected FlowPanel rightPanel = new FlowPanel();
		
		protected SelectionButton newProblemButton;
		protected SelectionButton addToProblemButton;

		protected Label titleLabel = new Label("Title");
		protected TextBox titleInput = new TextBox();
		protected Label descriptionLabel = new Label("Description");
		protected TextBox descriptionInput = new TextBox();
		
		protected VariableIdPanel varIdPanel = new VariableIdPanel();
		
		protected UploadButton uploadButton = new UploadButton();
		protected AbsolutePanel imageContainer = new AbsolutePanel();
		
		protected Badge requiredBadge = null;
		protected Problem problemSelected = null;

		protected final SelectionPanel problemSelection = new SelectionPanel(
				"Problems");
		protected SelectionPanel badgeSelection = new SelectionPanel(
				"RequiredBadge");

		ProblemDetails() {

			uploadButton.addSubmitCompleteHandler(new SubmitCompleteHandler() {

                @Override
                public void onSubmitComplete(SubmitCompleteEvent event) {
                	
                		String response = event.getResults();
                        String newUrl = event.getResults().split("imgurlstart")[1].split("imgurlend")[0];

                        System.err.println("ST: "+event.getResults().split("imgurlstart")[1]);
                        imageContainer.setSize("100%", "200%");
                        //TODO returning null
                        System.out.println("full results: \n"+response);
                        System.out.println("extr results: \n"+newUrl);
                        System.out.println("url('"+newUrl+"')");
//                        imageContainer.getElement().getStyle().setBackgroundImage("url('"+"CSStyles/images/chalkboard2.jpeg"+"')");
                        imageContainer.getElement().getStyle().setBackgroundImage("url('"+newUrl+"')");
                        uploadButton.removeFromParent();
                        leftPanel.add(imageContainer);
                        leftPanel.getElement().setScrollTop(varIdPanel.getElement().getAbsoluteTop());
                }
        });
			newProblemButton = new SelectionButton("New Problem") {

				@Override
				protected void onSelect() {
					refresh();
					newProblemButton.setEnabled(false);

					if (!badgeSelection.hasSelectionHandler()) {
						badgeSelection
								.addSelectionHandler(new SelectionHandler() {
									@Override
									public void onSelect(Cell selected) {
										requiredBadge = (Badge) selected
												.getEntity();
									}
								});
						for (Badge badge : Badge.values()) {
							badgeSelection.add(badge.toString(), badge.name(),
									badge);
						}
					}
					leftPanel.add(titleLabel);
					leftPanel.add(titleInput);
					leftPanel.add(descriptionLabel);
					leftPanel.add(descriptionInput);
					leftPanel.add(varIdPanel);
					leftPanel.add(uploadButton);
					rightPanel.add(badgeSelection);
				}
			};

			addToProblemButton = new SelectionButton("Add to Problem") {

				@Override
				protected void onSelect() {
					refresh();
					addToProblemButton.setEnabled(false);

					if (!problemSelection.hasSelectionHandler()) {
						problemSelection
								.addSelectionHandler(new SelectionHandler() {

									@Override
									public void onSelect(Cell selected) {
										problemSelected = (Problem) selected
												.getEntity();
										descriptionInput
												.setText(problemSelected
														.getDescription());
									}
								});
						dataBase.getProblemsByBadges(null,
								new AsyncCallback<ArrayList<Problem>>() {

									@Override
									public void onFailure(Throwable arg0) {
									}

									@Override
									public void onSuccess(
											ArrayList<Problem> problems) {
										for (Problem problem : problems) {
											problemSelection.add(
													problem.getTitle(), null,
													problem);
										}
									}
								});
					}
					leftPanel.add(descriptionLabel);
					leftPanel.add(descriptionInput);
					leftPanel.add(varIdPanel);
					rightPanel.add(problemSelection);
				}
			};
			
			topPanel.setSize("100%", "10%");
			leftPanel.setSize("50%", "90%");
			rightPanel.setSize("50%", "90%");
			leftPanel.getElement().getStyle().setOverflowY(Overflow.AUTO);
			leftPanel.addStyleName(CSS.LAYOUT_ROW);
			rightPanel.addStyleName(CSS.LAYOUT_ROW);
			add(topPanel);
			add(leftPanel);
			add(rightPanel);

			newProblemButton.addStyleName(CSS.LAYOUT_ROW);
			topPanel.add(newProblemButton);

			addToProblemButton.addStyleName(CSS.LAYOUT_ROW);
			topPanel.add(addToProblemButton);


		}

		void refresh() {
			newProblemButton.setEnabled(true);
			addToProblemButton.setEnabled(true);

			titleInput.setText("");
			descriptionInput.setText("");
			requiredBadge = null;
			problemSelected = null;

			leftPanel.clear();
			rightPanel.clear();

		}

		@Override
		public void appear() {
			refresh();
			super.appear();
		}

	}

	class VariableIdPanel extends FlowPanel {

		public void refresh(EquationTree eTree) {
			clear();

			LinkedList<EquationNode> terminals = eTree
					.getNodesByType(TypeSGET.Variable);
			terminals.addAll(eTree.getNodesByType(TypeSGET.Number));

			for (final EquationNode node : terminals) {
				add(new Button(node.getSymbol(), new ClickHandler() {

					@Override
					public void onClick(ClickEvent arg0) {
						TextBox input = problemDetails.descriptionInput;
						int cursorPos = input.getCursorPos();
						String inputText = input.getText();
						String inputTextPreCursor = inputText.substring(0,
								cursorPos).trim();
						String inputTextPostCursor = inputText.substring(
								cursorPos).trim();
						String insertion = " "
								+ PROBLEM_DESCRIPTION_VARIABLE_DELIMETER_START
								+ node.getId()
								+ PROBLEM_DESCRIPTION_VARIABLE_DELIMETER_END
								+ " ";
						input.setText(inputTextPreCursor + insertion
								+ inputTextPostCursor);
						input.setFocus(true);
						input.setCursorPos(inputTextPreCursor.length()
								+ insertion.length());
					}
				}));
			}
		}
	}
}